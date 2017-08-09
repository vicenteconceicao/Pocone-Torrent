package br.ic.ufmt.quick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.editText3);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCompartilhar();
            }
        });

        Button button1 = (Button) findViewById(R.id.editText4);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBuscar();
            }
        });

        Button button2 = (Button) findViewById(R.id.button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCLickChooser();
            }
        });
    }

    public void onClickCompartilhar(){
        Intent intent = new Intent(this, Compartilhamento.class);
        startActivity(intent);
    }

    public void onClickBuscar(){
        Intent intent = new Intent(this, Baixando.class);
        startActivity(intent);
    }

    public void onCLickChooser(){

    }
}
