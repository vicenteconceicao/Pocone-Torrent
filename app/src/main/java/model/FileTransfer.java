package model;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import database.SharedFileCRUD;

/**
 * Created by horgun on 09/08/17.
 * Implementation for transferring files
 */

public class FileTransfer implements FileTransferInterface {

    @Override
    public HashMap<String, Object> getFile(String hash) {
        HashMap<String, Object> hm = new HashMap<>();
        SharedFileCRUD sfCRUD = new SharedFileCRUD();
        SharedFile sf = sfCRUD.find(hash);
        if (sf == null)
            return hm;
        File f = new File(sf.getFilename());
        if (!f.exists())
            return hm;

        try {
            FileInputStream fis = new FileInputStream(f);
            byte[] bytes = new byte[sf.getSize()];
            fis.read(bytes);
            hm.put("filename", f.getName());
            hm.put("size", sf.getSize());
            hm.put("fileContent", bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hm;
    }
}
