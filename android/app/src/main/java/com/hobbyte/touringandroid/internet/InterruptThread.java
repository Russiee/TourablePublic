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

    public void run() {
        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {

        }
                ((HttpURLConnection) connection).disconnect();
                System.out.println("Closed connection");

    }
}