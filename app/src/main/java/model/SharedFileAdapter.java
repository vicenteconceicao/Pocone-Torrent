package model;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import br.ic.ufmt.quick.Baixando;
import br.ic.ufmt.quick.DownloadInfo;
import br.ic.ufmt.quick.PoconeTorrent;
import br.ic.ufmt.quick.R;
import database.SharedFileCRUD;
import util.FileConverter;

/**
 * Created by Vicente Conceicao on 10/08/2017.
 */

public class SharedFileAdapter extends ArrayAdapter<SharedFile> {

    public SharedFileAdapter(@NonNull Context context, ArrayList<SharedFile> sharedFiles) {
        super(context, 0, sharedFiles);

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Get the data item for this position
        final SharedFile sharedFile = getItem(position);
        if (sharedFile.getFilename().contains("poc_temp") && (int) new File(sharedFile.getFilename()).length() == 0){
            SharedFile ns = SharedFileCRUD.find(sharedFile.getHash());
            if (ns != null) {
                sharedFile.setFilename(ns.getFilename());
                sharedFile.setStatus(ns.getStatus());
            }
        }

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_shared_file, parent, false);
        }

        // Looup progress bar for data population
        ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);

        // Lookup view for data population
        TextView sharedFileName = (TextView) convertView.findViewById(R.id.filename);
        TextView sharedFileSize = (TextView) convertView.findViewById(R.id.size);
        TextView sharedFileSizeDownload = (TextView) convertView.findViewById(R.id.sizeDownloaded);
        TextView sharedFileData = (TextView) convertView.findViewById(R.id.date);


        //Looup view for event population
        Button sharedFileButtonBaixar = (Button) convertView.findViewById(R.id.btnSharedBaixar);
        Button sharedFileButtonApagar =(Button) convertView.findViewById(R.id.btnSharedApagar);

        if(sharedFile.getStatus() == 0) {
            sharedFileButtonBaixar.setVisibility(Button.INVISIBLE);
        }else if(sharedFile.getStatus() ==  1){
            sharedFileButtonBaixar.setBackgroundResource(R.color.colorPrimary);
            sharedFileButtonBaixar.setText("Pausar");
        }else{
            sharedFileButtonBaixar.setBackgroundResource(R.color.colorYellow);
            sharedFileButtonBaixar.setText("Continuar");
        }

        // Populate the data into the progressbar
        progressBar.setMax(sharedFile.getSize());

        //Verificar se é temp para abrir uma thread
        progressBar.setProgress((int) new File(sharedFile.getFilename()).length());

        // Populate the data into the template view using the data object
        sharedFileName.setText(sharedFile.getFilename());
        sharedFileSize.setText(FileConverter.ConverterBytes((long)sharedFile.getSize()));
        sharedFileData.setText(sharedFile.getDate().toString());
        sharedFileSizeDownload.setText(FileConverter.ConverterBytes(new File(sharedFile.getFilename()).length()));

        // Populate the event
        sharedFileButtonBaixar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sharedFile.getStatus() == 1){
                    sharedFile.setStatus(-1);
                    SharedFileCRUD.update(sharedFile);
                }else if(sharedFile.getStatus() == -1){
                    sharedFile.setStatus(1);
                    SharedFileCRUD.update(sharedFile);
                    continueDownload(sharedFile);
                }
                notifyDataSetChanged();
            }
        });
        sharedFileButtonApagar.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                //remover do tracker
                new Thread(){
                    @Override
                    public void run() {
                        Looper.prepare();
                        Handler h = new Handler();
                        try {
                            CallHandler call = new CallHandler();
                            Client c = new Client(Tracker.trackerAddress, Tracker.trackerPort, call);
                            HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                            boolean r = hmi.unshareFile(sharedFile.getHash());
                            c.close();

                            if (sharedFile.getStatus() != 0){
                                sharedFile.setStatus(-1);
                                SharedFileCRUD.update(sharedFile);
                                File apagar = new File(sharedFile.getFilename());
                                Thread.sleep(1000);
                                if (apagar.exists()){
                                    apagar.delete();
                                }
                                SharedFileCRUD.delete(sharedFile.getHash());
                            }
                            else{
                                SharedFileCRUD.delete(sharedFile.getHash());
                            }
                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"Apagando...", Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (final IOException e) {
                            e.printStackTrace();

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"Não foi possível conectar ao tracker: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        interrupt();
                        Looper.loop();
                    }
                }.start();

            }
        });

        //return the completed view to render on screen
        return convertView;
    }

    private void continueDownload(final SharedFile sf){
        new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                Handler h = new Handler();
                try {
                    //pegar peers com o tracker
                    CallHandler call = new CallHandler();
                    Client c = new Client(Tracker.trackerAddress, Tracker.trackerPort, call);
                    HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    List<Peer> peers = hmi.getPeers(sf.getHash());
                    c.close();

                    if (peers == null || peers.isEmpty()){//Colocar aviso para o usuário (dialog)
                        Log.d("Conexao", "Nao existem peers com esse hash!");
                        h.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(),"Não foram encontrados peers para esse arquivo!", Toast.LENGTH_LONG).show();
                            }
                        });

                        return;
                    }

                    Peer p = getRandomPeer(peers);

                    //baixar arquivo
                    c = new Client(p.getIp(), p.getPort(), call);
                    Log.d("SharedFileAdapter", "Hash: " + sf.getHash());
                    FileTransferInterface fti = (FileTransferInterface) c.getGlobal(FileTransferInterface.class);

                    String tempName = "poc_temp_" + sf.getHash();
                    File toSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +"/"+tempName);
                    int offset = (int) toSave.length();

                    FileOutputStream fos = new FileOutputStream(toSave, true);
                    Log.d("SharedFileAdapter", "Chegou aqui");
                    HashMap<String, Object> hm;
                    Log.d("SharedFileAdapter", "Testeeeee: " + offset);
                    do {
                        if (SharedFileCRUD.find(sf.getHash()).getStatus() == -1){
                            Log.d("SharedFileAdapter", "Download interrompido.");

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"Download interrompido.", Toast.LENGTH_LONG).show();
                                }
                            });
                            fos.close();
                            c.close();
                            return;
                        }
                        hm = fti.getFile(sf.getHash(), offset);
                        if (hm.isEmpty()){
                            Log.d("Conexao", "Deu ruim, nao conseguiu achar arquivo com o hash correspondente.");

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"Não foi possível encontrar o arquivo!", Toast.LENGTH_LONG).show();
                                }
                            });
                            fos.close();
                            c.close();
                            return;
                        }
                        if (hm.get("last") != null) {
                            Log.d("Conexao", "Terminou de receber.");

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(),"Arquivo " + sf.getFilename()+ " recebido!", Toast.LENGTH_LONG).show();
                                }
                            });
                            fos.close();
                            c.close();
                            break;
                        }
                        offset = (Integer) hm.get("offset");
                        byte[] bytes = (byte[]) hm.get("bytes");
                        fos.write(bytes, 0, (Integer)hm.get("length"));
                        int fileSize = (Integer) hm.get("size");
                        Log.d("Conexao", "Recebendo...");

                    } while (hm.get("last") == null);
                    fos.close();
                    c.close();
                    toSave.renameTo(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + hm.get("filename")));


                    toSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + hm.get("filename"));
                    sf.setFilename(toSave.getAbsolutePath());
                    sf.setStatus(0);
                    SharedFileCRUD.update(sf);
                    Log.d("Conexao", "Inseriu no sqlite");

                    //enviar para tracker
                    c = new Client(Tracker.trackerAddress, Tracker.trackerPort, call);
                    hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    boolean r = hmi.shareFile(sf.getHash());
                    c.close();
                    Log.d("Conexao", "Enviou pro tracker.");


                } catch (IOException e) {
                    final IOException err = e;
                    Log.d("Conexao", e.getMessage());
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),"Erro: " + err.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    this.interrupt();
                }
                this.interrupt();
                Looper.loop();
            }
        }.start();
    }

    private Peer getRandomPeer(List<Peer> peers){
        //get Random Peer
        int s = peers.size();
        return peers.get(new Random().nextInt(s));
    }

}
