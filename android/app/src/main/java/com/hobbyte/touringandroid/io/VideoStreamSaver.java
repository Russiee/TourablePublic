package com.hobbyte.touringandroid.io;

import android.util.Log;

import com.danikula.videocache.CacheListener;

import java.io.File;

public class VideoStreamSaver implements CacheListener {
    private static final String TAG = "VideoStreamSaver";


    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        Log.i(TAG, "onCacheAvaliable called");
        Log.i(TAG, cacheFile.getAbsolutePath());
        Log.i(TAG, url);
        Log.i(TAG, "Percents avaliable: " + percentsAvailable);

    }
}
