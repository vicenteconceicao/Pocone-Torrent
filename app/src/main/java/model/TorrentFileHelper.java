package model;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import br.ic.ufmt.quick.PoconeTorrent;

/**
 * Created by horgun on 10/08/17.
 */

public class TorrentFileHelper {
    private static String torrentsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public static File writePoconeTorrentFile(Uri fileUri){

        AssetFileDescriptor sharedFile = null;
        try {
            sharedFile = PoconeTorrent.getContext().getApplicationContext().getContentResolver().openAssetFileDescriptor(fileUri, "r");
        } catch (FileNotFoundException e) {
            Log.d("Conexao", "Arquivo não encontrado.");
            return null;
        }

        PoconeTorrentFile ptf = new PoconeTorrentFile();

        String[] path = fileUri.getPath().split("/");

        ptf.setFilename(torrentsFolder + "/" + path[path.length-1] + ".pocone");
        ptf.setSize((int)sharedFile.getLength());
        ptf.setTrackerAddress(Tracker.trackerAddress);
        ptf.setTrackerPort(Tracker.trackerPort);

        try {
            FileInputStream fis = sharedFile.createInputStream();
            byte[] bytes = new byte[(int)sharedFile.getLength()];
            fis.read(bytes);
            fis.close();

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            String hash = Base64.encodeToString(md.digest(bytes), Base64.DEFAULT);
            ptf.setHash(hash);
            Log.d("Conexao", "Hash antes: "+ptf.getHash());
            File torrent = new File(ptf.getFilename());
            BufferedWriter bw = new BufferedWriter(new FileWriter(torrent));
            bw.write(ptf.getTrackerAddress() + "\n");
            bw.write(ptf.getTrackerPort() + "\n");
            bw.write(ptf.getFilename() + "\n");
            bw.write(ptf.getSize() + "\n");
            bw.write(ptf.getHash() + "\n");
            bw.close();

            sharedFile.close();
            return torrent;

        } catch (IOException | NoSuchAlgorithmException e) {
            Log.d("Conexao", e.getMessage());
        }
        return null;
    }

    public static PoconeTorrentFile readPoconeTorrentFile(Uri fileUri){

        AssetFileDescriptor torrentFile = null;
        try {
            torrentFile = PoconeTorrent.getContext().getApplicationContext().getContentResolver().openAssetFileDescriptor(fileUri, "r");
        } catch (FileNotFoundException e) {
            Log.d("Conexao", "Arquivo .pocone não encontrado.");
            return null;
        }

        PoconeTorrentFile ptf = new PoconeTorrentFile();
        try {
            BufferedReader br = new BufferedReader(new FileReader(torrentFile.getFileDescriptor()));
            String txt = "";
            while (br.ready()){
                txt += br.readLine() + "\n";
            }
            String[] lines = txt.split("\n");
            if (lines.length != 5){
                Log.d("Conexao", "Arquivo torrent inválido!");
                return null;
            }
            ptf.setTrackerAddress(lines[0]);
            ptf.setTrackerPort(Integer.valueOf(lines[1]));
            ptf.setFilename(lines[2]);
            ptf.setSize(Integer.valueOf(lines[3]));
            ptf.setHash(lines[4]);
            br.close();

            return ptf;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
