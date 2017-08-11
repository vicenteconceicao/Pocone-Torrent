package model;

import android.content.res.AssetFileDescriptor;
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

/**
 * Created by horgun on 10/08/17.
 */

public class TorrentFileHelper {
    private static String torrentsFolder = "arquivosPocone";

    public static File writePoconeTorrentFile(File sharedFile){
        File f = new File(torrentsFolder);
        if (!f.exists()){
            f.mkdir();
        }

        PoconeTorrentFile ptf = new PoconeTorrentFile();

        ptf.setFilename(torrentsFolder + sharedFile.getName() + ".pocone");
        ptf.setSize((int)sharedFile.length());
        ptf.setTrackerAddress(Tracker.trackerAddress);
        ptf.setTrackerPort(Tracker.trackerPort);

        try {
            FileInputStream fis = new FileInputStream(sharedFile);
            byte[] bytes = new byte[(int)sharedFile.length()];
            fis.read(bytes);
            fis.close();

            MessageDigest md = MessageDigest.getInstance("SHA-1");
            ptf.setHash(new String(md.digest(bytes)));

            File torrent = new File(ptf.getFilename());
            BufferedWriter bw = new BufferedWriter(new FileWriter(torrent));
            bw.write(ptf.getTrackerAddress() + "\n");
            bw.write(ptf.getTrackerPort() + "\n");
            bw.write(ptf.getFilename() + "\n");
            bw.write(ptf.getSize() + "\n");
            bw.write(ptf.getHash() + "\n");
            bw.close();

            return torrent;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PoconeTorrentFile readPoconeTorrentFile(AssetFileDescriptor torrentFile){
        File f = new File(torrentsFolder);
        if (!f.exists()){
            f.mkdir();
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
