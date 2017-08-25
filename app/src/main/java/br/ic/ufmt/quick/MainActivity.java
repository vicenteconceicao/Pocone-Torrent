package br.ic.ufmt.quick;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import database.SharedFileCRUD;
import model.FileTransfer;
import model.HashManagerInterface;
import model.Peer;
import model.SharedFile;
import model.Tracker;
import rmi.ServerRMI;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        verifyStoragePermissions(this);

        setContentView(R.layout.activity_main);

        Button button2 = (Button) findViewById(R.id.button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChooseFile();
            }
        });

        Button btnDownload = (Button) findViewById(R.id.btn_downloads);
        btnDownload.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                onClickDownloads();
            }
        });

        Button btnCompartilhar = (Button) findViewById(R.id.btn_compartilhar);
        btnCompartilhar.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                onClickCompartilhar();
            }
        });

        Intent intent = new Intent(this, ServerRMI.class);
        startService(intent);

        verificaArquivos();
        enviarHashs();

    }

    private void verificaArquivos() {

        List<SharedFile> files = SharedFileCRUD.findAll();

        for(SharedFile f : files){
            if(!(new File(f.getFilename()).exists())){
                SharedFileCRUD.delete(f.getHash());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 44 && resultCode == Activity.RESULT_OK){
            data.setAction(Intent.ACTION_OPEN_DOCUMENT);
            if(data != null) {
                Intent intent = new Intent(this, DownloadInfo.class);
                intent.putExtra("path", data.getData());
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, ServerRMI.class);
        stopService(intent);
        removerHashs();
        super.onDestroy();
    }

    public void onClickChooseFile(){
        verificaArquivos();
        enviarHashs();
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        startActivityForResult(Intent.createChooser(intent,"Selecionar arquivo"), 44);
    }

    public void onClickCompartilhar(){
        Intent intent = new Intent(this, CompartSelectFile.class);
        startActivity(intent);
    }

    public void onClickBuscar(){
        Intent intent = new Intent(this, Baixando.class);
        startActivity(intent);
    }

    public void onClickDownloads(){
        Intent intent = new Intent(this, Baixando.class);
        startActivity(intent);
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void enviarHashs(){
        final List<SharedFile> lsf = SharedFileCRUD.findAllStatus(0);

        //enviar para tracker
        new Thread(){
            @Override
            public void run() {
                try {
                    CallHandler call = new CallHandler();
                    Client c = new Client(Tracker.trackerAddress, Tracker.trackerPort, call);
                    HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    for (SharedFile sf : lsf) {
                        boolean r = hmi.shareFile(sf.getHash());
                    }
                    c.close();
                } catch (final IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"Não foi possível conectar ao tracker: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                }
            }
        }.start();
    }

    private void removerHashs(){
        final List<SharedFile> lsf = SharedFileCRUD.findAllStatus(0);

        //remover do tracker
        new Thread(){
            @Override
            public void run() {
                try {
                    CallHandler call = new CallHandler();
                    Client c = new Client(Tracker.trackerAddress, Tracker.trackerPort, call);
                    HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    for (SharedFile sf : lsf) {
                        boolean r = hmi.unshareFile(sf.getHash());
                    }
                    c.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"Não foi possível conectar ao tracker: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.start();
    }

}
