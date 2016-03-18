package com.hobbyte.touringandroid.io;

import android.util.Log;

import com.danikula.videocache.CacheListener;

import java.io.File;

public class VideoStreamSaver implements CacheListener {

    private static final String TAG = "VideoStreamSaver";

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {

        Log.d(TAG, "onCacheAvailable called for file at url: " + url);

        Log.d(TAG, percentsAvailable + "% of this file is cached");

        if (percentsAvailable == 100) {
            Log.d(TAG, "File cached at: " + cacheFile.toURI());

            //TODO possibly save video in the main directory?

        }
    }
}
