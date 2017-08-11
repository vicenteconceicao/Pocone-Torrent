package model;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import br.ic.ufmt.quick.PoconeTorrent;
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
        if (sf == null) {
            Log.d("Conexao", "Nao achou Hash no SQLite");
            return hm;
        }
        AssetFileDescriptor f = null;
        try {
            f = PoconeTorrent.getContext().getApplicationContext().getContentResolver().openAssetFileDescriptor(Uri.parse(sf.getFilename()), "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Conexao", e.getMessage());
            return hm;
        }

        try {
            FileInputStream fis = f.createInputStream();
            byte[] bytes = new byte[sf.getSize()];
            fis.read(bytes);
            fis.close();
            hm.put("filename", sf.getFilename());
            hm.put("size", sf.getSize());
            hm.put("fileContent", bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d("Conexao", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Conexao", e.getMessage());
        }

        return hm;
    }
}
