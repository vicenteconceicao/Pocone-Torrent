package model;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import database.SharedFileCRUD;

/**
 * Created by horgun on 09/08/17.
 * Implementation for transferring files
 */

public class FileTransfer implements FileTransferInterface {

    @Override
    public HashMap<String, Object> getFile(String hash, int offset) throws IOException {
        HashMap<String, Object> hm = new HashMap<>();
        SharedFileCRUD sfCRUD = new SharedFileCRUD();
        SharedFile sf = sfCRUD.find(hash);
        if (sf == null) {
            Log.d("Conexao", "Nao achou Hash no SQLite");
            return hm;
        }

        File file = new File(sf.getFilename());
        if (!file.exists()){
            Log.d("Conexao", "Arquivo nao encontrado!");
            return hm;
        }

//        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[524288];
            String[] s = sf.getFilename().split("/");
            String filename = s[s.length-1];
            int size;
//            int desloc = 0;

            long skipped = fis.skip(offset);
            if (skipped == offset){
                if ((size = fis.read(bytes)) != -1) {
                    hm.put("offset", offset+size);
                    hm.put("length", size);
                    hm.put("bytes", bytes);
                    hm.put("size",(int) file.length());
                    fis.close();
                    return hm;
                }
                hm.put("filename", filename);
                hm.put("last", 1);
                fis.close();
                return hm;
            }

//            while ((size = fis.read(bytes)) != -1) {
//                desloc+=size;
//                if (desloc == offset + size){
//                    hm.put("offset", desloc);
//                    hm.put("length", file.length());
//                    hm.put("bytes", bytes);
//                    fis.close();
//                    return hm;
//                }
//            }
//            hm.put("filename", filename);
//            hm.put("last", 1);
//            fis.close();

//        } catch (IOException e) {
//            e.printStackTrace();
//            Log.d("Conexao", e.getMessage());
//        }
        return hm;
    }
}
