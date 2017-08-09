package br.ic.ufmt.quick;

import android.app.Application;
import android.content.Context;

/**
 * Created by horgun on 09/08/17.
 * Used for getting the application context
 */

public class PoconeTorrent extends Application {
    private static PoconeTorrent instance;

    public static PoconeTorrent getInstance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
