package br.ic.ufmt.quick;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import rmi.ServerRMI;

public class DownloadInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);

        Intent intent = getIntent();

        TextView fileName = (TextView) findViewById(R.id.fileName1);

        final Uri fileUri = (Uri)intent.getParcelableExtra("path");

        fileName.setText(fileUri.getPath());

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
        Log.d("Conexao", "ENTROUU AQUI");
        AssetFileDescriptor afd = null;
        try {
            afd = getContentResolver().openAssetFileDescriptor(fileUri, "r");
        } catch (FileNotFoundException e) {
            Log.d("Conexao", "Arquivo .pocone não encontrado.");
            return;
        }
        Log.d("Conexao", "Oiiiii");
        final PoconeTorrentFile ptf = TorrentFileHelper.readPoconeTorrentFile(afd);
        if (ptf == null){
            Log.d("Conexao", "Falha ao baixar arquivo!");
            return;
        }

        Log.d("Conexao", "PoconeTorretFile: " + ptf.getFilename());


        final AssetFileDescriptor finalAfd = afd;
        new Thread(){
            @Override
            public void run() {
                try {
                    //pegar peers com o tracker
                    CallHandler call = new CallHandler();
                    Log.d("Conexao", "IP: " + ptf.getTrackerAddress() + ":" + ptf.getTrackerPort());
                    Client c = new Client(ptf.getTrackerAddress(), ptf.getTrackerPort(), call);
                    HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    List<Peer> peers = hmi.getPeers(ptf.getHash());
                    c.close();
                    Log.d("Conexao", "Passou aqui1");
                    Peer p = getRandomPeer(peers);

                    //baixar arquivo
                    c = new Client(p.getIp(), p.getPort(), call);
                    FileTransferInterface fti = (FileTransferInterface) c.getGlobal(FileTransferInterface.class);
                    HashMap<String, Object> hm = fti.getFile(ptf.getHash());
                    finalAfd.close();
                    Log.d("Conexao", "Passou aqui2");
                    File toSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + hm.get("filename"));

                    FileOutputStream fos = new FileOutputStream(toSave);
                    byte[]  bytes = new byte[(Integer)hm.get("size")];

                    fos.write(bytes);
                    fos.close();
                    c.close();

                    SharedFileCRUD sfc = new SharedFileCRUD();
                    SharedFile sf = new SharedFile(ptf.getHash(), new Date(), toSave.getAbsolutePath(), (int)toSave.length(), 1);
                    sfc.insert(sf);
                    Log.d("Conexao", "Inseriu no sqlite");

                    //enviar para tracker
                    c = new Client(ptf.getTrackerAddress(), ptf.getTrackerPort(), call);
                    hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
                    boolean r = hmi.shareFile(ptf.getHash(), new Peer(ServerRMI.serverIP, ServerRMI.serverPort));
                    c.close();

                } catch (IOException e) {
                    Log.d("Conexao", e.getMessage());
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
