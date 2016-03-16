package com.hobbyte.touringandroid;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Created by max on 06/03/16.
 */
public class App extends Application {

    public static Context context;
    private Activity currentActivity = null;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public Activity getCurrentActivity(){
        return currentActivity;
    }
    public void setCurrentActivity(Activity currentActivity){
        this.currentActivity = currentActivity;
    }
}
