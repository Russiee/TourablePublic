package com.hobbyte.touringandroid.helpers;

import android.provider.BaseColumns;

/**
 * This class simply provides a single place where all database tables and their column
 * names can be stored and kept consistent across the app.
 *
 * Largely taken from the Android
 * <a href="https://developer.android.com/training/basics/data-storage/databases.html">training guides.</a>
 */
public final class TourDBContract {

    // intentionally empty
    public TourDBContract() {}

    /**
     * Used to track top-level details about stored tours.
     */
    public static abstract class TourList implements BaseColumns {
        public static final String TABLE_NAME             = "tourList";
        public static final String COL_TOUR_KEY           = "tourKey";
        public static final String COL_DATE_EXPIRES_ON    = "expiresOn";
        public static final String COL_DATE_LAST_ACCESSED = "lastAccessedOn";
        public static final String COL_HAS_VIDEO          = "hasVideo";
    }
}
