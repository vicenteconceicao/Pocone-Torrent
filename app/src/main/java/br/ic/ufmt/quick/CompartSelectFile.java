package br.ic.ufmt.quick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CompartSelectFile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compart_select_file);

        Button btnCompartilhar = (Button) findViewById(R.id.btn_select_compart);
        btnCompartilhar.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                onClickChooseFile();
            }
        });

    }

    public void onClickChooseFile(){
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Selecionar arquivo"), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        TextView nomeArq = (TextView) findViewById(R.id.nome_arquivo);

        if(data != null) {
            nomeArq.setText(data.getData().getPath());
            Intent intent = new Intent(this, CompartInfo.class);
            intent.putExtra("path", data.getData());
            startActivity(intent);
        }
    }
}
