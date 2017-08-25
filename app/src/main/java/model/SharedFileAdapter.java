package model;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
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
import java.io.IOException;
import java.util.ArrayList;

import br.ic.ufmt.quick.PoconeTorrent;
import br.ic.ufmt.quick.R;
import database.SharedFileCRUD;
import util.FileConverter;

/**
 * Created by Vicente Conceicao on 10/08/2017.
 */

public class SharedFileAdapter extends ArrayAdapter<SharedFile> {
    ArrayList<SharedFile> sfs;

    public SharedFileAdapter(@NonNull Context context, ArrayList<SharedFile> sharedFiles) {
        super(context, 0, sharedFiles);
        sfs = sharedFiles;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Get the data item for this position
        final SharedFile sharedFile = getItem(position);
        if (sharedFile.getFilename().contains("poc_temp") && (int) new File(sharedFile.getFilename()).length() == 0){
            SharedFile ns = new SharedFileCRUD().find(sharedFile.getHash());
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
                    new SharedFileCRUD().update(sharedFile);
                }else if(sharedFile.getStatus() == -1){
                    sharedFile.setStatus(1);
                    new SharedFileCRUD().update(sharedFile);

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
                            new SharedFileCRUD().delete(sharedFile.getHash());
                            sfs.remove(sharedFile);
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

}
