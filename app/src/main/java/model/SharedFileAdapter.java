package model;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import br.ic.ufmt.quick.R;

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
        TextView sharedFileData = (TextView) convertView.findViewById(R.id.data);

        // Populate the data into the template view using the data object
        sharedFileName.setText(sharedFile.getFilename());
        sharedFileSize.setText(sharedFile.getSize()+"");
        sharedFileData.setText(sharedFile.getData().toString());

        //return the completed view to render on screen
        return convertView;
    }

}
