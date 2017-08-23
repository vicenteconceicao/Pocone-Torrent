package util;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by Vicente Conceicao on 23/08/2017.
 */

public class FileConverter {

    /**
     *
     * @param bytes
     * @return
     */
    public static String ConverterBytes(Long bytes){
        Log.d("Conexao", "bytes: "+bytes);
        if(bytes > 1073741823){
            return new DecimalFormat("0.00").format(bytes/1024F/1024/1024)+" GB";
        }else if(bytes >= 1000000){
            return new DecimalFormat("0.00").format(bytes/1024F/1024)+" MB";
        }else{
            return new DecimalFormat("0.00").format(bytes/1024F)+" KB";
        }

    }
}
