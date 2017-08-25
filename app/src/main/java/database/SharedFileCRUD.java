package database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import br.ic.ufmt.quick.PoconeTorrent;
import model.SharedFile;

/**
 * Created by horgun on 09/08/17.
 * CRUD for SharedFile
 */

public class SharedFileCRUD {
    private static PoconeTorrentDbHelper helper;
    private static SQLiteDatabase db;

    public SharedFileCRUD() {

    }

    public static boolean insert(SharedFile sf){
        if (find(sf.getHash()) != null)
            return false;
        helper = new PoconeTorrentDbHelper(PoconeTorrent.getContext());
        db = helper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put("hash", sf.getHash());
        values.put("filename", sf.getFilename());
        values.put("size", sf.getSize());
        values.put("data", sf.getDate().toString());
        values.put("status", sf.getStatus());

        // Insert the new row, returning the primary key value of the new row
        long r = db.insert("SharedFile", null, values);
        db.close();
        helper.close();
        return r != -1;
    }

    public static boolean delete(String hash){
        helper = new PoconeTorrentDbHelper(PoconeTorrent.getContext());
        db = helper.getReadableDatabase();
        // Define 'where' part of query.
        String selection = "hash LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { hash };
        // Issue SQL statement.
        int r = db.delete("SharedFile", selection, selectionArgs);
        db.close();
        helper.close();
        return r != 0;
    }

    public static SharedFile find(String hash){
        helper = new PoconeTorrentDbHelper(PoconeTorrent.getContext());
        db = helper.getWritableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "hash",
                "data",
                "filename",
                "size",
                "status"
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = "hash = ?";
        String[] selectionArgs = { hash };

        Cursor c = db.query(
                "SharedFile",                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        SharedFile sf = new SharedFile();
        if (!c.moveToFirst())
            return null;
        sf.setHash(c.getString(0));
        sf.setDate(Date.valueOf(c.getString(1)));
        sf.setFilename(c.getString(2));
        sf.setSize(c.getInt(3));
        sf.setStatus(c.getInt(4));
        c.close();
        db.close();
        helper.close();
        return sf;
    }

    public static List<SharedFile> findAll(){
        helper = new PoconeTorrentDbHelper(PoconeTorrent.getContext());
        db = helper.getWritableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "hash",
                "data",
                "filename",
                "size",
                "status"
        };
        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                "filename ASC";

        Cursor c = db.query(
                "SharedFile",                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<SharedFile> lsf = new ArrayList<>();

        if (!c.moveToFirst())
            return lsf;

        do {
            SharedFile sf = new SharedFile();
            sf.setHash(c.getString(0));
            sf.setDate(Date.valueOf(c.getString(1)));
            sf.setFilename(c.getString(2));
            sf.setSize(c.getInt(3));
            sf.setStatus(c.getInt(4));
            lsf.add(sf);
        } while (c.moveToNext());
        c.close();
        db.close();
        helper.close();
        return lsf;
    }

    public static List<SharedFile> findAllStatus(int status){
        helper = new PoconeTorrentDbHelper(PoconeTorrent.getContext());
        db = helper.getWritableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "hash",
                "data",
                "filename",
                "size",
                "status"
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = "status = ?";
        String[] selectionArgs = { "" + status };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                "filename ASC";

        Cursor c = db.query(
                "SharedFile",                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );

        List<SharedFile> lsf = new ArrayList<>();

        if (!c.moveToFirst())
            return lsf;

        do {
            SharedFile sf = new SharedFile();
            sf.setHash(c.getString(0));
            sf.setDate(Date.valueOf(c.getString(1)));
            sf.setFilename(c.getString(2));
            sf.setSize(c.getInt(3));
            sf.setStatus(c.getInt(4));
            lsf.add(sf);
        } while (c.moveToNext());
        c.close();
        db.close();
        helper.close();
        return lsf;
    }

    public static boolean update(SharedFile sf){
        helper = new PoconeTorrentDbHelper(PoconeTorrent.getContext());
        db = helper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put("hash", sf.getHash());
        values.put("data", sf.getDate().toString());
        values.put("filename", sf.getFilename());
        values.put("size", sf.getSize());
        values.put("status", sf.getStatus());

        // Which row to update, based on the title
        String selection = "hash = ?";
        String[] selectionArgs = { sf.getHash() };

        int count = db.update(
                "SharedFile",
                values,
                selection,
                selectionArgs);
        db.close();
        helper.close();
        return count != 0;
    }

}
