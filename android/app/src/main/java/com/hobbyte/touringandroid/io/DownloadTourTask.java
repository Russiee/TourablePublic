package com.hobbyte.touringandroid.io;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.internet.ServerAPI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Downloads the bundle JSON and then does two things with it:
 * <ul><li>Processes the JSON for use in creating {@link com.hobbyte.touringandroid.tourdata.Tour}
 * objects</li><li>Strips out all URLs, then downloads and saves them to the device</li></ul>
 */
public class DownloadTourTask extends Thread {
    private static final String TAG = "DownloadTourTask";

    public static final int STATE_DOWNLOADING = 0;
    public static final int STATE_FINISHED = 1;
    public static final String STATE = "downloadState";
    public static final String PROGRESS = "downloadProgress";

    private HashSet<String> mediaURLs;
    private String keyID;
    private String tourID;
    private boolean getVideo;

    private Handler handler;

    /**
     *
     * @param handler a {@link Handler} implementation from an Activity that updates a progress bar
     * @param keyID a tour's key ID
     * @param tourID a tour's ID
     * @param getVideo should be false if you want to download images only
     */
    public DownloadTourTask(Handler handler, String keyID, String tourID, boolean getVideo) {
        this.keyID = keyID;
        this.tourID = tourID;
        this.getVideo = getVideo;
        this.handler = handler;
    }

    @Override
    public void run() {
        String bundleString = ServerAPI.getBundleString(tourID);

        String imageOnlyPattern = "https?:\\/\\/[\\w\\d\\.\\/]*\\.(jpe?g|png)";
        String allMediaPattern = "https?:\\/\\/[\\w\\d\\.\\/]*\\.(jpe?g|png|mp4)";

        // use pattern matcher to extract all media URLs
        Pattern p = null;

        if (getVideo) {
            p = Pattern.compile(allMediaPattern);
        } else {
            p = Pattern.compile(imageOnlyPattern);
        }

        Matcher matcher = p.matcher(bundleString);
        mediaURLs = new HashSet<String>();

        while (matcher.find()) {
            mediaURLs.add(matcher.group());
        }

        // on a separate thread, save the bundle and POI JSON
        Log.d(TAG, "About to start bundle saver");
        
        BundleSaver bundleSaver = new BundleSaver(App.context, bundleString, keyID);
        bundleSaver.start();

        float total = (float) mediaURLs.size();
        float count = 0.0f;
        boolean success = false;

        for (Iterator<String> i = mediaURLs.iterator(); i.hasNext(); ) {
            String urlString = i.next();

            // first download the image from the web
            Bitmap bitmap = ServerAPI.downloadBitmap(urlString);

            // then save the image on the device
            success = FileManager.saveImage(App.context, bitmap, urlString, keyID);

            // inform the calling activity that progress has been made
            Bundle bundle = new Bundle();
            bundle.putInt(STATE, STATE_DOWNLOADING);
            bundle.putFloat(PROGRESS, ++count / total);
            Message msg = handler.obtainMessage();
            msg.setData(bundle);
            handler.handleMessage(msg);
        }

        // inform the calling activity that the download is complete
        Bundle bundle = new Bundle();
        bundle.putInt(STATE, STATE_FINISHED);
        Message msg = handler.obtainMessage();
        msg.setData(bundle);
        handler.handleMessage(msg);
    }
}
