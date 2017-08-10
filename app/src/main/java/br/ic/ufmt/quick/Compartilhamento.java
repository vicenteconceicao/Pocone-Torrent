package br.ic.ufmt.quick;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.sf.lipermi.handler.CallHandler;
import net.sf.lipermi.net.Client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import model.FileTransferInterface;
import model.HashManagerInterface;
import model.Peer;
import model.PoconeTorrentFile;
import model.TorrentFileHelper;

public class Compartilhamento extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poconeinfo);

        Intent intent = getIntent();

        final TextView fileName = (TextView) findViewById(R.id.fileName1);
        fileName.setText(intent.getStringExtra("path"));

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
                baixar(fileName.getText().toString());
            }
        });

    }

    private void baixar(String filename){
        File f = new File(filename);

        PoconeTorrentFile ptf = TorrentFileHelper.readPoconeTorrentFile(f);

        CallHandler call = new CallHandler();
        try {
            Client c = new Client(ptf.getTrackerAddress(), ptf.getTrackerPort(), call);
            HashManagerInterface hmi = (HashManagerInterface) c.getGlobal(HashManagerInterface.class);
            List<Peer> peers = hmi.getPeers(ptf.getHash());
            c.close();

            Peer p = getRandomPeer(peers);

            c = new Client(p.getIp(), p.getPort(), call);
            FileTransferInterface fti = (FileTransferInterface) c.getGlobal(FileTransferInterface.class);
            HashMap<String, Object> hm = fti.getFile(ptf.getHash());

            File toSave = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + hm.get("filename"));

            FileOutputStream fos = new FileOutputStream(f);
            byte[]  bytes = new byte[(Integer)hm.get("size")];

            fos.write(bytes);
            fos.close();
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Peer getRandomPeer(List<Peer> peers){
        //get Random Peer
        int s = peers.size();
        return peers.get(new Random().nextInt(s));
    }

}
