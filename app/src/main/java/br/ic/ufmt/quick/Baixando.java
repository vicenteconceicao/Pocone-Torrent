package br.ic.ufmt.quick;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.GregorianCalendar;
import java.util.List;

import database.SharedFileCRUD;
import model.SharedFile;

public class Baixando extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baixando);
        //ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar1);

        SharedFileCRUD dbHelper = new SharedFileCRUD();
        List<SharedFile> pocones =  dbHelper.findAll();
        pocones.add(new SharedFile("019283129787382739127",new GregorianCalendar().getGregorianChange(), "Windows 10", 10,10));

        ArrayAdapter<SharedFile> adapter = new ArrayAdapter<SharedFile>(this, android.R.layout.simple_list_item_activated_1, pocones);

        ListView listaDePocone = (ListView) findViewById(R.id.list_pocone);
        listaDePocone.setAdapter(adapter);

    }
}
