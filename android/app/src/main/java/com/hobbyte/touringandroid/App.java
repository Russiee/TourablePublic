package com.hobbyte.touringandroid;

import android.app.Application;
import android.content.Context;

/**
 * Created by max on 06/03/16.
 */
public class App extends Application {

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
