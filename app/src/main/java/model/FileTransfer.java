package model;

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
        return null;
    }
}
