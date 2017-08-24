package br.ic.ufmt.quick;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import database.SharedFileCRUD;
import model.FileTransferInterface;
import model.HashManagerInterface;
import model.Peer;
import model.PoconeTorrentFile;
import model.SharedFile;
import model.TorrentFileHelper;
import util.FileConverter;

public class DownloadInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);

        Intent intent = getIntent();

        TextView fileName = (TextView) findViewById(R.id.fileName1);

        final Uri fileUri = (Uri)intent.getParcelableExtra("path");

        fileName.setText(fileUri.getPath());


        final PoconeTorrentFile ptf = TorrentFileHelper.readPoconeTorrentFile(fileUri);
        if (ptf == null){
            Log.d("Conexao", "Falha ao ler arquivo .pocone!");
            return;
        }


        TextView fileSize = (TextView) findViewById(R.id.sizeDownload);

        fileSize.setText(FileConverter.ConverterBytes((long)ptf.getSize()));

        Button btnCancelar = (Button) findViewById(R.id.btn_cancelar_torrent);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Button btnIniciar = (Button) findViewById(R.id.btn_inciar_torrent);

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baixar(fileUri);
            }
        });

    }

    private void baixar(Uri fileUri){

        final PoconeTorrentFile ptf = TorrentFileHelper.readPoconeTorrentFile(fileUri);
        if (ptf == null){
            Log.d("Conexao", "Falha ao ler arquivo .pocone!");
            return;
        }

        if(new SharedFileCRUD().find(ptf.getHash()) != null){
            finish();
            Toast.makeText(DownloadInfo.this, "Você já possui este arquivo", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DownloadInfo.this, Baixando.class);
            startActivity(intent);
            return;
        }

        Log.d("Conexao", "PoconeTorretFile: " + ptf.getFilename());

        new Thread(){
            @Override
            public void run() {
                try {
                    //pegar peers com o tracker
                    CallHandler call = new CallHandler();
                    Client c = new Client(ptf.getTrackerAddress(), ptf.getTrackerPort(), call);
                    HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    List<Peer> peers = hmi.getPeers(ptf.getHash());
                    c.close();

                    if (peers == null || peers.isEmpty()){//Colocar aviso para o usuário (dialog)
                        Log.d("Conexao", "Nao existem peers com esse hash!");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DownloadInfo.this,"Não foram encontrados peers para esse arquivo!", Toast.LENGTH_LONG).show();
                            }
                        });

                        return;
                    }

                    Peer p = getRandomPeer(peers);

                    //baixar arquivo
                    c = new Client(p.getIp(), p.getPort(), call);
                    Log.d("Conexao", "Passou aqui");
                    Log.d("Conexao", "Hash: "+ptf.getHash());
                    FileTransferInterface fti = (FileTransferInterface) c.getGlobal(FileTransferInterface.class);

                    String tempName = "poc_temp_"+ptf.getHash();
                    File toSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +"/"+tempName);
                    FileOutputStream fos = new FileOutputStream(toSave);
                    HashMap<String, Object> hm;
                    int offset = 0;
                    do {
                        hm = fti.getFile(ptf.getHash(), offset);
                        if (hm.isEmpty()){
                            Log.d("Conexao", "Deu ruim, nao conseguiu achar arquivo com o hash correspondente.");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DownloadInfo.this,"Não foi possível encontrar o arquivo!", Toast.LENGTH_LONG).show();
                                }
                            });

                            return;
                        }
                        if (hm.get("last") != null) {
                            Log.d("Conexao", "Terminou de receber.");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(DownloadInfo.this,"Arquivo recebido!", Toast.LENGTH_LONG).show();
                                }
                            });

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
                    SharedFileCRUD sfc = new SharedFileCRUD();
                    SharedFile sf = new SharedFile(ptf.getHash(), new Date(new java.util.Date().getTime()), toSave.getAbsolutePath(), (int)toSave.length(), 1);
                    sfc.insert(sf);
                    Log.d("Conexao", "Inseriu no sqlite");

                    //enviar para tracker
                    c = new Client(ptf.getTrackerAddress(), ptf.getTrackerPort(), call);
                    hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    boolean r = hmi.shareFile(ptf.getHash());
                    c.close();
                    Log.d("Conexao", "Enviou pro tracker.");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DownloadInfo.this, "Baixando", Toast.LENGTH_LONG).show();
                        }
                    });
                    finish();
                    Intent intent = new Intent(DownloadInfo.this, Baixando.class);
                    startActivity(intent);

                } catch (IOException e) {
                    final IOException err = e;
                    Log.d("Conexao", e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(DownloadInfo.this,"Erro: " + err.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    this.interrupt();
                }
                this.interrupt();
            }
        }.start();

    }

    private Peer getRandomPeer(List<Peer> peers){
        //get Random Peer
        int s = peers.size();
        return peers.get(new Random().nextInt(s));
    }

}
