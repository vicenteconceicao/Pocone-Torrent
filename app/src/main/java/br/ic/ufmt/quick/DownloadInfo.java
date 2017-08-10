package br.ic.ufmt.quick;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DownloadInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_info);

        Intent intent = getIntent();

        TextView fileName = (TextView) findViewById(R.id.fileName1);
        fileName.setText(intent.getStringExtra("path"));

        Button btnCancelar = (Button) findViewById(R.id.btn_cancelar_torrent);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
