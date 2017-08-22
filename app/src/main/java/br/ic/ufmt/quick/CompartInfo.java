package br.ic.ufmt.quick;

import android.content.res.AssetFileDescriptor;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;

import database.SharedFileCRUD;
import model.HashManagerInterface;
import model.Peer;
import model.PoconeTorrentFile;
import model.SharedFile;
import model.TorrentFileHelper;
import model.Tracker;
import rmi.ServerRMI;

public class CompartInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compart_info);

        Intent intent = getIntent();

        TextView fileName = (TextView) findViewById(R.id.fileName1);

        final Uri fileUri = (Uri)intent.getParcelableExtra("path");

        fileName.setText(fileUri.getPath());

        Button btn_compart = (Button) findViewById(R.id.btn_iniciar_compart);
        btn_compart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compartilhar(fileUri);
            }
        });

        Button btn_cancel = (Button) findViewById(R.id.btn_cancelar_compart);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void compartilhar(Uri fileUri){
        AssetFileDescriptor afd = null;
        try {
            afd = getContentResolver().openAssetFileDescriptor(fileUri, "r");
        } catch (FileNotFoundException e) {
            Log.d("Conexao", "Arquivo não encontrado.");
            return;
        }

        final SharedFile sf = new SharedFile();
        sf.setFilename(PoconeTorrent.getPathFromUri(this, fileUri));
        Log.d("Conexao", "name: "+fileUri.toString());
        Log.d("Conexao", "realPath: "+sf.getFilename());
        sf.setStatus(1);
        sf.setSize((int) afd.getLength());
        sf.setDate(new Date(new java.util.Date().getTime()));

        File f = TorrentFileHelper.writePoconeTorrentFile(fileUri);

        if (f == null){
            Log.d("Conexao", "Nao deu pra escrever o .pocone");
            return;
        }
        Log.d("Conexao", "chegouuuu");

        sf.setHash(TorrentFileHelper.readPoconeTorrentFile(Uri.fromFile(f)).getHash());

        SharedFileCRUD sfc = new SharedFileCRUD();
        sfc.insert(sf);

        //enviar para tracker
        new Thread(){
            @Override
            public void run() {
                try {
                    CallHandler call = new CallHandler();
                    Client c = new Client(Tracker.trackerAddress, Tracker.trackerPort, call);
                    HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    boolean r = hmi.shareFile(sf.getHash());
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("Conexao", "Enviou hash para o tracker!");
            }
        }.start();
        Log.d("Conexao", "Passouuuuu");
        Toast toast = Toast.makeText(this, "Compartilhou", Toast.LENGTH_LONG);
        toast.show();
    }
}
