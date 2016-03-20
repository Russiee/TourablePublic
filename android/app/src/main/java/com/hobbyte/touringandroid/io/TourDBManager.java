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

    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "TourData.db";

    public static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    private static TourDBManager tdbmInstance;
    private SQLiteDatabase db;

    /*=============================================
        SQL QUERY STRINGS
     =============================================*/

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TourList.TABLE_NAME + " (" +
                    TourList.COL_KEY_ID + " TEXT PRIMARY KEY," +
                    TourList.COL_TOUR_ID + " TEXT NOT NULL," +
                    TourList.COL_TOUR_NAME + " TEXT NOT NULL," +
                    TourList.COL_DATE_EXPIRES_ON + " NUMERIC," +
                    TourList.COL_DATE_LAST_ACCESSED + " NUMERIC," +
                    TourList.COL_HAS_MEDIA + " INTEGER DEFAULT 0," +
                    TourList.COL_VERSION + " NUMERIC NOT NULL" +
            ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TourList.TABLE_NAME;

    private static final String SQL_ROW_COUNT =
            "SELECT COUNT(" + TourList.COL_TOUR_ID + ") FROM " + TourList.TABLE_NAME;

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
     * Fetches every row in the table, ordered by when tours were last access by the user.
     *
     * @return a {@link Cursor} at position -1
     */
    public Cursor getTourDisplayInfo() {
        open(false);

        String[] cols = {
                TourList.COL_KEY_ID, TourList.COL_TOUR_ID,
                TourList.COL_TOUR_NAME, TourList.COL_DATE_EXPIRES_ON
        };
        String orderBy = TourList.COL_DATE_LAST_ACCESSED + " DESC";

        return db.query(TourList.TABLE_NAME, cols, null, null, null, null, orderBy);
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
    public void putRow(String keyID, String tourID, String tourName,
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
        values.put(TourList.COL_TOUR_ID, tourID);
        values.put(TourList.COL_TOUR_NAME, tourName);
        values.put(TourList.COL_DATE_EXPIRES_ON, expiryLong);
        values.put(
                TourList.COL_DATE_LAST_ACCESSED,
                Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()
        );
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

        String where = TourList.COL_KEY_ID + " = ?";
        String[] whereArgs = {keyID};

        return db.query(TourList.TABLE_NAME, null, where, whereArgs, null, null, null);
    }

    /**
     * Update a tour entry in the db. All fields, with the exception of `db` and `hasMedia`, will
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
     * @param version the current version of the tour
     */
    public void updateRow(String keyID, String tourID, String tourName,
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

        String where = TourList.COL_TOUR_ID + " = ?";
        String[] whereArgs = {keyID};

        ContentValues values = new ContentValues();

        values.put(TourList.COL_TOUR_ID, tourID);
        values.put(TourList.COL_TOUR_NAME, tourName);
        values.put(TourList.COL_DATE_EXPIRES_ON, expiryLong);
        values.put(
                TourList.COL_DATE_LAST_ACCESSED,
                Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()
        );
        values.put(TourList.COL_HAS_MEDIA, video);
        values.put(TourList.COL_VERSION, version);

        db.update(TourList.TABLE_NAME, values, where, whereArgs);
    }

    /**
     * Deletes a row in the db corresponding to a particular tour key.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     */
    public void deleteTour(String keyID) {
        open(true);

        String where = TourList.COL_KEY_ID + " = ?";
        String[] whereArgs = {keyID};
        int count = db.delete(TourList.TABLE_NAME, where, whereArgs);
        Log.d(TAG, "Deleted " + count + " row");
    }

    /**
     * Deletes a number of rows corresponding to the provided key IDs. Use when removing expired
     * tours.
     *
     * @param keyIDs the ID of a tour key (not the tour ID itself)
     */
    public void deleteTours(String[] keyIDs) {
        open(true);

        String where = String.format(
                TourList.COL_KEY_ID + " IN (%s)",
                getPlaceHolders(keyIDs.length)
        );

        int count = db.delete(TourList.TABLE_NAME, where, keyIDs);
        Log.d(TAG, "Deleted " + count + " row(s)");
    }

    /**
     * Sets the `lastAccessedOn` field of a tour to be the current time. Should be used when the
     * user selects a tour from the StartActivity.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     */
    public void updateAccessedTime(String keyID) {
        open(true);

        ContentValues values = new ContentValues();

        values.put(
                TourList.COL_DATE_LAST_ACCESSED,
                Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis()
        );

        String where = TourList.COL_KEY_ID + " = ?";
        String[] whereArgs = {keyID};

        db.update(TourList.TABLE_NAME, values, where, whereArgs);
    }

    /**
     * Fetches the information required to find out if a tour needs updating.
     *
     * @return an array where each row is of the form [(String) keyID, (String) tourID,
     * (long) updatedAt]
     */
    public Object[][] getTourUpdateInfo() {
        open(false);

        // TODO: change the return array and second column when tour version numbers are implemented
        String[] cols = {TourList.COL_KEY_ID, TourList.COL_TOUR_ID, TourList.COL_VERSION};
        Cursor c = db.query(
                TourList.TABLE_NAME, cols,
                null, null, null, null, null
        );

        Object[][] keys = new Object[c.getCount()][3];
        int i = 0;

        while (c.moveToNext()) {
            keys[i][0] = c.getString(0);
            keys[i][1] = c.getString(1);
            keys[i][2] = c.getInt(2);
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
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @return true if the tour exists
     */
    public boolean doesTourExist(String keyID) {
        open(false);

        String[] cols = {TourList.COL_KEY_ID};
        String where = TourList.COL_KEY_ID + " = ?";
        String[] whereArgs = {keyID};

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
     * Checks if a tour was downloaded with both images and video, or images only.
     *
     * @param keyID the ID of a tour key (not the tour ID itself)
     * @return false if the tour was downloaded with images only
     */
    public boolean doesTourHaveVideo(String keyID) {
        open(false);

        String[] cols = {TourList.COL_HAS_MEDIA};
        String where = TourList.COL_KEY_ID + " = ?";
        String[] whereArgs = {keyID};

        Cursor c = db.query(
                TourList.TABLE_NAME, cols,
                where, whereArgs,
                null, null, null
        );

        c.moveToFirst();
        boolean hasMedia = c.getInt(0) == 1;

        c.close();
        return hasMedia;
    }

    /**
     * Takes one or more timestamps and converts them into milliseconds since Epoch.
     *
     * @param timestamp one or more timestamps
     * @return millisecond representations of the provided timestamps
     * @throws ParseException if the timestamps don't match the dateFormat
     */
    public static long convertStampToMillis(String timestamp) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        long toReturn;

        if(timestamp.contains("T")) {
            Date date = df.parse(timestamp);
            toReturn = date.getTime();
        } else {
            toReturn = Long.valueOf(timestamp);
        }

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
