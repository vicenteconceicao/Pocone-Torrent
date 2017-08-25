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
import java.util.Timer;
import java.util.TimerTask;

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

//        final SharedFileCRUD dbHelper = new SharedFileCRUD();
//
//        final ArrayList<SharedFile> files = (ArrayList<SharedFile>) dbHelper.findAll();
//
//        final SharedFileAdapter adapter = new SharedFileAdapter(this, files);

        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        SharedFileAdapter adapter = new SharedFileAdapter(Baixando.this, (ArrayList<SharedFile>) SharedFileCRUD.findAll());
                        ListView listView = (ListView) findViewById(R.id.list_pocone);
                        listView.setAdapter(adapter);
                    }
                });
            }
        },0, 2000);


    }
}
