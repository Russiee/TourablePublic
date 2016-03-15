package com.hobbyte.touringandroid.io;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.internet.ServerAPI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Downloads the bundle JSON and then does two things with it:
 * <ul><li>Processes the JSON for use in creating a {@link com.hobbyte.touringandroid.tourdata.Tour}
 * </li><li>Strips out all URLs, then downloads and saves them to the device</li></ul>
 */
public class DownloadTourTask extends Thread {
    private static final String TAG = "DownloadTourTask";

    public static final int STATE_DOWNLOADING = 0;
    public static final int STATE_FINISHED = 1;
    public static final String STATE = "downloadState";
    public static final String PROGRESS = "downloadProgress";

    public static final String FILE_NAME_PATTERN = "https?:\\/\\/[-\\w\\.\\/]*\\/(.+\\.(jpe?g|png|avi|mp4))";
//    public static final String FILE_NAME_PATTERN = "https?:\\/\\/[-\\w\\.\\/]*\\/([\\p{Punct}\\w]+\\.(jpe?g|png|mp4))";

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

        // on a separate thread, save the bundle and POI JSON
        BundleSaver bundleSaver = new BundleSaver(App.context, bundleString, keyID);
        bundleSaver.start();

        try {
            bundleSaver.join();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }

        // used to separate the file name and extension from the rest of the URL
        Pattern namePattern = Pattern.compile(FILE_NAME_PATTERN);

        HashSet<String> imageURLs = bundleSaver.getImageURLs();
        HashSet<String> videoURLs = bundleSaver.getVideoURLs();

        float total = (float) imageURLs.size() + 2 * videoURLs.size(); // make video files fill more progress
        float count = 0.0f;

        for (Iterator<String> i = imageURLs.iterator(); i.hasNext(); ) {
            String urlString = i.next();
            Log.d(TAG, urlString);
            Matcher m = namePattern.matcher(urlString);

            if (m.matches()) {
                String img = m.group(1);
                Log.d(TAG, img);
                saveFile(urlString, img, "image", 8192);
            } else {
                Log.d(TAG, "Could not match " + urlString);
            }

            informActivity(++count / total);
        }

        if (getVideo) {
            for (Iterator<String> i = videoURLs.iterator(); i.hasNext(); ) {
                String urlString = i.next();
                Matcher m = namePattern.matcher(urlString);

                if (m.matches()) {
                    String img = m.group(1);
                    saveFile(urlString, img, "video", 8192); // TODO figure out if there's ever a reason to use bigger than 8192
                }

                count += 2;
                informActivity(count / total);
            }
        }

        // inform the calling activity that the download is complete
        Bundle bundle = new Bundle();
        bundle.putInt(STATE, STATE_FINISHED);
        Message msg = handler.obtainMessage();
        msg.setData(bundle);
        handler.handleMessage(msg);
    }

    /**
     * Creates a stream from the URL connection, which is read in chunks of bytes and written
     * straight to disk.
     *
     * @param url the URL of the file
     * @param name the file name & extension
     * @param folder the destination folder (image or video)
     * @param bufferSize the amount of memory to read/write at a time. Please use a whole number
     *                   of kB
     */
    private void saveFile(String url, String name, String folder, int bufferSize) {
        HttpURLConnection connection = ServerAPI.getConnection(url);

        try {
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream(), bufferSize);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(
                    new File(App.context.getFilesDir(), String.format("%s/%s/%s", keyID, folder, name))
            ), bufferSize);

            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            int bytesTotal = 0;

            while ((bytesRead = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
                bytesTotal += bytesRead;
            }

            connection.disconnect();
            bis.close();
            bos.close();

            Log.d(TAG, String.format("Downloaded %s - %d bytes total", name, bytesTotal));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a message to the calling activity's ProgressHandler, which updates the progress bar.
     */
    private void informActivity(float progress) {
        Bundle bundle = new Bundle();
        bundle.putInt(STATE, STATE_DOWNLOADING);
        bundle.putFloat(PROGRESS, progress);
        Message msg = handler.obtainMessage();
        msg.setData(bundle);
        handler.handleMessage(msg);
    }
}
