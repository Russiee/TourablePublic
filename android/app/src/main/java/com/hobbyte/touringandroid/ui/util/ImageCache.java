package com.hobbyte.touringandroid.ui.util;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

/**
 * Utility class for providing easy access to in-memory caching of bitmaps, that wraps around a
 * {@link LruCache}. The amount of memory that is set aside for the cache is a fraction of the
 * maximum size that the app's heap can expand to.
 */
public class ImageCache {
    private static final String TAG = "ImageCache";

    private static ImageCache cacheInstance;
    private LruCache<String, Bitmap> lruCache;

    /**
     * Instantiates the {@link LruCache} that this class wraps around.
     */
    private ImageCache() {
        if (lruCache == null) {
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory());
            final int cacheSize = maxMemory / 8;

            // need to override sizeOf() to make the cache measure itself by a memory amount
            // instead of a fixed number of entries
            lruCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getAllocationByteCount();
                }
            };

            Log.i(TAG, String.format("Making mem cache with %d MB capacity", cacheSize / (1024 * 1024)));
        }
    }

    public static ImageCache getInstance() {
        if (cacheInstance == null) {
            cacheInstance = new ImageCache();
        }

        return cacheInstance;
    }

    /**
     * Adds a {@link Bitmap} to the cache, associating it with its filename.
     *
     * @param key   an image's filename
     * @param value a Bitmap of that image
     */
    public void addBitmap(String key, Bitmap value) {
        lruCache.put(key, value);

        Log.i(TAG, String.format("Adding bitmap to cache. Size is %d", lruCache.size()));
    }

    /**
     * Return the {@link Bitmap} associated with the provided key.
     *
     * @param key an image's filename
     * @return a Bitmap if the key was found, otherwise null
     */
    public Bitmap getBitmap(String key) {
        Log.i(TAG, "Fetching image...");
        return lruCache.get(key);
    }

    /**
     * Removes all entries from the cache.
     */
    public void clearCache() {
        float size = (float) lruCache.size() / (1024 * 1024);
        Log.i(TAG, String.format("Evicting %.2f MB of memory from cache", size));
        lruCache.evictAll();
    }
}
