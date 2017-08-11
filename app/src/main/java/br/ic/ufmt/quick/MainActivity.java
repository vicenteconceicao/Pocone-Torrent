package br.ic.ufmt.quick;

import android.Manifest;
import android.app.Activity;
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

import java.io.File;
import java.io.FileNotFoundException;

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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == 44 && resultCode == Activity.RESULT_OK){
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
        super.onDestroy();
    }

    public void onClickChooseFile(){
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


}
