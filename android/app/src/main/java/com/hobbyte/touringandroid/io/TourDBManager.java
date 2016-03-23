package com.hobbyte.touringandroid.io;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hobbyte.touringandroid.io.TourDBContract.TourList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Performs all required database operations. To use, simply call {@link #getInstance(Context)}.
 * There is no need to worry about readable/writable databases, as this is handled for you.
 * <p>
 * The only piece of housekeeping that you <b>MUST</b> do is to call {@link #close()} in
 * {@link Activity#onPause()} or {@link Activity#onStop()} of any Activity that uses this class.
 * (And close any {@link Cursor} instances that you use outside of this class, naturally).
 * <p>
 * Database operations are not inherently run on separate threads. If you are worried about blocking
 * the UI thread, make a new one. However, it is not expected that this database will ever contain
 * so many rows that it will slow the UI thread.
 * <p>
 * Largely taken from the Android
 * <a href="https://developer.android.com/training/basics/data-storage/databases.html">training guides.</a>
 * Some outside guidance was also had regarding the
 * <a href="http://www.androiddesignpatterns.com/2012/05/correctly-managing-your-sqlite-database.html">
 * Singleton pattern</a> that this class uses.
 */
public class TourDBManager extends SQLiteOpenHelper {
    private static final String TAG = "TourDBManager";

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "TourData.db";

    public static final String SERVER_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static TourDBManager tdbmInstance;
    private SQLiteDatabase db;

    /*=============================================
        SQL QUERY STRINGS
     =============================================*/

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TourList.TABLE_NAME + " (" +
                    TourList.COL_KEY_ID + " TEXT PRIMARY KEY," +
                    TourList.COL_KEY_NAME + " TEXT NOT NULL," +
                    TourList.COL_TOUR_ID + " TEXT NOT NULL," +
                    TourList.COL_TOUR_NAME + " TEXT NOT NULL," +
                    TourList.COL_DATE_EXPIRES_ON + " NUMERIC," +
                    TourList.COL_HAS_MEDIA + " INTEGER DEFAULT 0," +
                    TourList.COL_VERSION + " NUMERIC NOT NULL," +
                    TourList.COL_HAS_UPDATE + " INTEGER DEFAULT 0" +
            ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TourList.TABLE_NAME;

    private static final String SQL_ROW_COUNT =
            "SELECT COUNT(" + TourList.COL_TOUR_ID + ") FROM " + TourList.TABLE_NAME;

    private static final String WHERE_KEY_ID = TourList.COL_KEY_ID + " = ?";

    /*=============================================
        DATABASE METHODS
     =============================================*/

    /**
     * Returns the Singleton instance of this class, through which the database can be queried.
     * For the Context parameter, please use {@link Activity#getApplicationContext()} or
     * {@link com.hobbyte.touringandroid.App#context} instead of an Activity instance.
     */
    public static synchronized TourDBManager getInstance(Context context) {
        if (tdbmInstance == null) {
            tdbmInstance = new TourDBManager(context);
        }

        return tdbmInstance;
    }

    /**
     * Constructor is private to prevent outside instantiation. Use {@link #getInstance(Context)}
     * instead.
     */
    private TourDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the DB using the script above. This method is automatically called the
     * first time the app uses either SQLiteOpenHelper.getReadableDatabase() or
     * SQLiteOpenHelper.getWritableDatabase().
     * <p>
     * Don't use it yourself.
     *
     * @param db an SQLite DB instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    /**
     * Called when {@link #DATABASE_VERSION} is incremented. Not likely to be used in this case.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(TAG, "DB WAS UPGRADED");
        // for now, just start over with fresh db
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    /**
     * Put this at the start of any method that uses the internal {@link SQLiteDatabase} to ensure
     * that you actually have a connection to the database!
     *
     * @param writable true if the app is going to write to the database. (A writable db can also
     *                 be read from).
     */
    private void open(boolean writable) {
        if (db == null) {
            if (writable) {
                db = tdbmInstance.getWritableDatabase();
            } else {
                db = tdbmInstance.getReadableDatabase();
            }
        } else if (writable && db.isReadOnly()) {
            super.close();
            db = tdbmInstance.getWritableDatabase();
        }
    }

    @Override
    public synchronized void close() {
        db = null;
        super.close();
    }

    /**
     * Fetches the columns needed to display tours in the UI for every row. They will be ordered
     * based on when they were entered into the table.
     *
     * @return a {@link Cursor} at position -1
     */
    public Cursor getTourDisplayInfo() {
        open(false);

        String[] cols = {
                TourList.COL_KEY_ID, TourList.COL_TOUR_ID, TourList.COL_TOUR_NAME};

        return db.query(TourList.TABLE_NAME, cols, null, null, null, null, null);
    }

    /**
     * Insert a tour entry into the db. All fields, with the exception of `hasMedia`, will
     * be extracted from JSON that was fetched from the server.
     * <p>
     * All the timestamp parameters should be passed in the string form in which the server stores
     * them. They will be converted into, and stored as, milliseconds since Epoch.
     *
     * @param keyID the objectId of the key used to fetch the tour
     * @param tourID the objectId of the tour itself
     * @param tourName the tour's title
     * @param expiryDate when the tour expires
     * @param hasMedia whether or not the user opted to download the tour with video
     */
    public void putRow(String keyID, String tourID, String keyName, String tourName,
                       String expiryDate, boolean hasMedia, int version) {
        open(true);

        int video = (hasMedia ? 1 : 0);
        long expiryLong;

        try {
            expiryLong = convertStampToMillis(expiryDate);
        } catch (ParseException e) {
            e.printStackTrace();
            // is there a better way to handle this?
            expiryLong = 100;
        }

        ContentValues values = new ContentValues();

        values.put(TourList.COL_KEY_ID, keyID);
        values.put(TourList.COL_KEY_NAME, keyName);
        values.put(TourList.COL_TOUR_ID, tourID);
        values.put(TourList.COL_TOUR_NAME, tourName);
        values.put(TourList.COL_DATE_EXPIRES_ON, expiryLong);
        values.put(TourList.COL_HAS_MEDIA, video);
        values.put(TourList.COL_VERSION, version);

        db.insert(TourList.TABLE_NAME, null, values);
    }

    /**
     * Fetches all columns for a single tour.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @return a {@link Cursor} at position -1
     */
    public Cursor getRow(String keyID) {
        open(false);

        String[] whereArgs = {keyID};

        return db.query(TourList.TABLE_NAME, null, WHERE_KEY_ID, whereArgs, null, null, null);
    }

    /**
     * Update a tour entry in the db. The new `tourName` and `version` will come from the server,
     * and `hasMedia` depends on whether or not the user decided to update with/without media.
     *
     * @param keyID the objectId of the key used to fetch the tour
     * @param tourName the tour's title
     * @param hasMedia whether or not the user opted to download the tour with video
     * @param version the current version of the tour
     */
    public void updateRow(String keyID, String tourName, boolean hasMedia, int version) {
        open(true);

        int video = (hasMedia ? 1 : 0);
        String[] whereArgs = {keyID};

        ContentValues values = new ContentValues();
        values.put(TourList.COL_TOUR_NAME, tourName);
        values.put(TourList.COL_HAS_MEDIA, video);
        values.put(TourList.COL_VERSION, version);

        db.update(TourList.TABLE_NAME, values, WHERE_KEY_ID, whereArgs);
    }

    /**
     * Changes a tour's expiry date.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @param expiry the new expiry date
     */
    public void updateTourExpiry(String keyID, long expiry) {
        open(true);

        ContentValues values = new ContentValues();
        values.put(TourList.COL_DATE_EXPIRES_ON, expiry);

        String[] whereArgs = {keyID};

        db.update(TourList.TABLE_NAME, values, WHERE_KEY_ID, whereArgs);

    }

    /**
     * Deletes a row in the db corresponding to a particular tour key.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     */
    public void deleteTour(String keyID) {
        open(true);

        String[] whereArgs = {keyID};
        int count = db.delete(TourList.TABLE_NAME, WHERE_KEY_ID, whereArgs);
        Log.d(TAG, "Deleted " + count + " row");
    }

    /**
     * Fetches the information required to find out if a tour needs updating.
     *
     * @return an array where each row is of the form [(String) keyID, (String) tourID,
     * (int) version, (long) expiry date]
     */
    public Object[][] getTourUpdateInfo() {
        open(false);

        String[] cols = {
                TourList.COL_KEY_ID, TourList.COL_TOUR_ID,
                TourList.COL_VERSION, TourList.COL_DATE_EXPIRES_ON
        };
        Cursor c = db.query(
                TourList.TABLE_NAME, cols,
                null, null, null, null, null
        );

        Object[][] keys = new Object[c.getCount()][4];
        int i = 0;

        while (c.moveToNext()) {
            keys[i][0] = c.getString(0);
            keys[i][1] = c.getString(1);
            keys[i][2] = c.getInt(2);
            keys[i][3] = c.getLong(3);
            ++i;
        }

        c.close();

        return keys;
    }

    /**
     * Finds the tours which have expired and returns their keys. These tours should be deleted.
     *
     * @return a String array of tour keys which have expired.
     */
    public String[] getExpiredTours() {
        open(false);

        String[] cols = {TourList.COL_KEY_ID};
        String where = TourList.COL_DATE_EXPIRES_ON + " < ?";
        String[] whereArgs = {String.valueOf(Calendar.getInstance().getTimeInMillis())};

        // fetch all keys where the current date is greater than the tour's expiry date
        Cursor c = db.query(
                TourList.TABLE_NAME, cols,
                where, whereArgs,
                null, null, null
        );

        int count = c.getCount();

        // return expired keys
            String[] toDelete = new String[count];

            while (c.moveToNext()) {
                toDelete[--count] = c.getString(0);
            }

        c.close();

        c.close();
        return toDelete;
    }

    /**
     * Gets a tour's expiry date.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @return the (long) expiry date for the specified tour
     */
    public long getExpiryDate(String keyID) {
        open(false);

        String[] cols = {TourList.COL_DATE_EXPIRES_ON};
        String[] whereArgs = {keyID};

        Cursor c = db.query(TourList.TABLE_NAME, cols, WHERE_KEY_ID, whereArgs, null, null, null);
        c.moveToFirst();

        long expiry = c.getLong(0);
        c.close();

        return expiry;
    }

    /**
     * Checks if there are any tours in the database.
     *
     * @return true if the table is empty
     */
    public boolean dbIsEmpty() {
        open(false);

        Cursor c = db.rawQuery(SQL_ROW_COUNT, null);
        c.moveToFirst();
        int count = c.getInt(0);
        c.close();

        return count == 0;
    }

    /**
     * Deletes all rows in the table.
     */
    public void clearTable() {
        open(true);

        db.delete(TourList.TABLE_NAME, null, null);
    }

    /**
     * Checks if a key has already been used (i.e. there is a row for it in the db). Used when the
     * user enters a key, to prevent downloading duplicate tours.
     *
     * @param keyName the key that was first entered by the user to download the tour
     * @return true if the tour exists
     */
    public boolean doesTourKeyNameExist(String keyName) {
        open(false);

        String[] cols = {TourList.COL_KEY_ID};
        String where = TourList.COL_KEY_NAME + " = ?";
        String[] whereArgs = {keyName};

        Cursor c = db.query(
                TourList.TABLE_NAME, cols,
                where, whereArgs,
                null, null, null
        );

        boolean exists = c.getCount() > 0;
        c.close();

        return exists;
    }

    /**
     * Checks if a tour was downloaded with or without images and video.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @return false if the tour was downloaded without media
     */
    public boolean doesTourHaveMedia(String keyID) {
        open(false);

        String[] cols = {TourList.COL_HAS_MEDIA};
        String[] whereArgs = {keyID};

        Cursor c = db.query(TourList.TABLE_NAME, cols, WHERE_KEY_ID, whereArgs, null, null, null);
        c.moveToFirst();

        boolean hasMedia = c.getInt(0) == 1;
        c.close();

        return hasMedia;
    }

    /**
     * Changes the `hasUpdate` attribute on a tour. Used to trigger updates in SummaryActivity.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @param hasUpdate true if there is a new version of the tour on the server
     */
    public void flagTourUpdate(String keyID, boolean hasUpdate) {
        open(true);

        ContentValues values = new ContentValues();
        int update = hasUpdate ? 1 : 0;
        values.put(TourList.COL_HAS_UPDATE, update);

        String[] whereArgs = {keyID};

        db.update(TourList.TABLE_NAME, values, WHERE_KEY_ID, whereArgs);
    }

    /**
     * Checks if the specified tour has an update available.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @return true if the `hasUpdate` attribute is set to true
     */
    public boolean doesTourHaveUpdate(String keyID) {
        open(false);

        String[] whereArgs = {keyID};
        String[] cols = {TourList.COL_HAS_UPDATE};

        Cursor c = db.query(TourList.TABLE_NAME, cols, WHERE_KEY_ID, whereArgs, null, null, null);
        c.moveToFirst();

        boolean hasUpdate = c.getInt(0) == 1;
        c.close();

        return hasUpdate;
    }

    /**
     * Takes a timestamp and converts it into milliseconds since Epoch.
     *
     * @param timestamp a timestamp formatted like yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     * @return millisecond representation of the provided timestamp
     * @throws ParseException if the timestamp doesn't match {@link #SERVER_TIME_FORMAT}
     */
    public static long convertStampToMillis(String timestamp) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(SERVER_TIME_FORMAT);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date = df.parse(timestamp);
        long toReturn = date.getTime();

        return toReturn;
    }

    /**
     * Returns a number of question marks separated by commas. Needed for SQL queries that have
     * "WHERE x IN (...)" (e.g. `SQL_DELETE_TOURS`). The API can't handle dynamic placeholders so
     * we have to do it ourselves.
     * <p>
     * Don't pass 0 to this as it will return a single question mark anyway.
     *
     * @param count the number of placeholders to generate
     * @return a String of Android SQL placeholders
     */
    private String getPlaceHolders(int count) {
        StringBuilder sb = new StringBuilder("?");
        if (count > 1) {
            for (int i = 0; i < count; i++) {
                sb.append(", ?");
            }
        }

        return sb.toString();
    }
}
