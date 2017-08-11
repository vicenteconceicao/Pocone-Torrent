package br.ic.ufmt.quick;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        TextView nomeArq = (TextView) findViewById(R.id.nome_arquivo);

        if(data != null) {
            nomeArq.setText(data.getData().getPath());
            Intent intent = new Intent(this, DownloadInfo.class);
            intent.putExtra("path", data.getData());
            startActivity(intent);
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
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selecionar arquivo"), 1);
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
}
