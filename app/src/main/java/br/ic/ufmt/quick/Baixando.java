package br.ic.ufmt.quick;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Baixando extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baixando);
        ProgressBar bar = (ProgressBar)findViewById(R.id.progressBar1);
        TextView textView = (TextView)findViewById(R.id.dadosEnviados);
        if(bar.getProgress()!=100){
            textView.setVisibility(View.INVISIBLE);
        }
    }
}
