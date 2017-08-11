package br.ic.ufmt.quick;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import database.SharedFileCRUD;
import model.SharedFile;
import model.SharedFileAdapter;

public class Baixando extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baixando);
        //ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar1);

        populateSharedFileList();

    }

    public void populateSharedFileList(){

        SharedFileCRUD dbHelper = new SharedFileCRUD();

        ArrayList<SharedFile> files = (ArrayList<SharedFile>) dbHelper.findAll();

        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));
        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));
        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));
        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));
        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));
        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));
        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));
        files.add(new SharedFile("019283129787382739127",new Date(new java.util.Date().getTime()), "Windows 10", 10,10));

        SharedFileAdapter adapter = new SharedFileAdapter(this, files);

        ListView listView = (ListView) findViewById(R.id.list_pocone);

        listView.setAdapter(adapter);
    }
}
