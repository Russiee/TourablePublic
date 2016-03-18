package com.hobbyte.touringandroid.internet;

import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * Created by Nikita on 17/03/2016.
 */
public class InterruptThread implements Runnable {
    Thread parent;
    URLConnection connection;
    public InterruptThread(Thread parent, URLConnection con) {
        this.parent = parent;
        this.connection = con;
    }

    /**
     * This method is required to bypass a bug with the HttpURLConnection, wherein an InputStream
     * will hang if connection is lost while trying to read input.
     * As an image will not take longer than 120seconds to load, connection will be forced to drop if the stream hangs
     */
    public void run() {
        try {
            Thread.sleep(120000);
        } catch (InterruptedException e) {
        }
        ((HttpURLConnection) connection).disconnect();
    }
}