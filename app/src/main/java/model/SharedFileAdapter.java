package model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.ic.ufmt.quick.PoconeTorrent;
import br.ic.ufmt.quick.R;
import util.FileConverter;

/**
 * Created by Vicente Conceicao on 10/08/2017.
 */

public class SharedFileAdapter extends ArrayAdapter<SharedFile> {
    public SharedFileAdapter(@NonNull Context context, ArrayList<SharedFile> sharedFiles) {
        super(context, 0, sharedFiles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        //Get the data item for this position
        SharedFile sharedFile = getItem(position);

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_shared_file, parent, false);
        }

        // Lookup view for data population
        TextView sharedFileName = (TextView) convertView.findViewById(R.id.filename);
        TextView sharedFileSize = (TextView) convertView.findViewById(R.id.size);
        TextView sharedFileData = (TextView) convertView.findViewById(R.id.date);


        //Looup view for event population
        Button sharedFileButtonBaixar = (Button) convertView.findViewById(R.id.btnSharedBaixar);
        Button sharedFileButtonApagar = (Button) convertView.findViewById(R.id.btnSharedApagar);

        // Populate the data into the template view using the data object
        sharedFileName.setText(sharedFile.getFilename());
        sharedFileSize.setText(FileConverter.ConverterBytes((long)sharedFile.getSize()));
        sharedFileData.setText(sharedFile.getDate().toString());

        // Populate the event
        sharedFileButtonBaixar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBtnBaixar();
            }
        });
        sharedFileButtonApagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBtnApagar();
            }
        });

        //return the completed view to render on screen
        return convertView;
    }

    public void onClickBtnBaixar(){
        Toast.makeText(PoconeTorrent.getContext(), "Compartilhou", Toast.LENGTH_LONG).show();
    }

    public void onClickBtnApagar(){

    }

}
