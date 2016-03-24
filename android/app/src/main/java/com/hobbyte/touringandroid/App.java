package com.hobbyte.touringandroid;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * @author Max
 * @author Jonathan
 * Application class to enable non activity classes to perform methods from {@Link Activity},
 * to get app {@Link Context} and to access the HttpProxyCacheServer instance
 */
public class App extends Application {

    public static Context context;
    private static HttpProxyCacheServer proxy;
    private Activity currentActivity = null;

    /**
     * Gets the HttpProxyCacheServer for video streaming
     *
     * @return the HttpProxyCacheServer instance
     */
    public static HttpProxyCacheServer getProxy() {
        if (proxy == null) {
            proxy = new HttpProxyCacheServer(context);
        }
        return proxy;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /**
     * Returns the current activity to allow it to be used outside an Activity or Fragment
     *
     * @return currentActivity
     */
    public Activity getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.currentActivity = currentActivity;
    }
}
