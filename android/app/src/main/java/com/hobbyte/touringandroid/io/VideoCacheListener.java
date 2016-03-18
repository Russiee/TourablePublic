package com.hobbyte.touringandroid.io;

import android.util.Log;

import java.io.File;

/**
 * Provides an interface to be able to tell how much a video has been cached,
 * and where that cached version has been stored
 */
public class VideoCacheListener implements com.danikula.videocache.CacheListener {

    private static final String TAG = "VideoCacheListener";

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

        Log.d(TAG, "onCacheAvailable called for file at url: " + url);

        Log.d(TAG, percentsAvailable + "% of this file is cached");

        //if we have cached the entire file
        if (percentsAvailable == 100) {
            Log.d(TAG, "File cached at: " + cacheFile.toURI());

            //TODO possibly save video in the main directory?

        }
    }
}
