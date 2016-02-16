package com.hobbyte.touringandroid.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hobbyte.touringandroid.helpers.TourDBContract.TourList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Performs all required database operations.
 *
 * Largely taken from the Android
 * <a href="https://developer.android.com/training/basics/data-storage/databases.html">training guides.</a>
 */
public class TourDBManager extends SQLiteOpenHelper {
    private static final String TAG = "TourDBManager";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TourData.db";

    public static final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /*=============================================
        SQL QUERY STRINGS
     =============================================*/

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TourList.TABLE_NAME + " (" +
                    TourList.COL_KEY_ID + " TEXT PRIMARY KEY," +
                    TourList.COL_TOUR_ID + " TEXT NOT NULL," +
                    TourList.COL_TOUR_NAME + " TEXT NOT NULL," +
                    TourList.COL_DATE_CREATED + " NUMERIC NOT NULL," +
                    TourList.COL_DATE_UPDATED + " NUMERIC NOT NULL," +
                    TourList.COL_DATE_EXPIRES_ON + " NUMERIC," +
                    TourList.COL_DATE_LAST_ACCESSED + " NUMERIC," +
                    TourList.COL_HAS_VIDEO + " INTEGER DEFAULT 0" +
            ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TourList.TABLE_NAME;

    private static final String SQL_ROW_COUNT =
            "SELECT COUNT(" + TourList.COL_TOUR_ID + ") FROM " + TourList.TABLE_NAME;

    private static final String SQL_GET_TOURS =
            "SELECT * FROM " + TourList.TABLE_NAME +
                    " ORDER BY " + TourList.COL_DATE_LAST_ACCESSED + " DESC";

    /*=============================================
        DATABASE METHODS
     =============================================*/

    public TourDBManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates the DB using the script above. This method is automatically called the
     * first time the app uses either SQLiteOpenHelper.getReadableDatabase() or
     * SQLiteOpenHelper.getWritableDatabase().
     *
     * @param db an SQLite DB instance
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO
        Log.w(TAG, "A call to onUpgrade() was made.");
    }

    public Cursor getTours(SQLiteDatabase db) {
        Cursor c = db.rawQuery(SQL_GET_TOURS, null);
        c.moveToFirst();
        return c;
    }

    public void putRow(SQLiteDatabase db, String keyID, String tourID, String tourName,
                       String creationDate, String updateDate, String expiryDate, boolean hasVideo) {
        if (db.isReadOnly()) {
            Log.w(TAG, "Can't write to this DB!!!");
            return;
        }

        ContentValues values = new ContentValues();
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        long now = cal.getTimeInMillis();

        // default times in case there's an error below
        long tCreated = now;
        long tUpdated = now;
        long tExpires = now;

        SimpleDateFormat df = new SimpleDateFormat(dateFormat);
        try {
            Date dCreated = df.parse(creationDate);
            Date dUpdated = df.parse(updateDate);
            Date dExpires = df.parse(expiryDate);

            tCreated = dCreated.getTime();
            tUpdated = dUpdated.getTime();
            tExpires = dExpires.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.w(TAG, "Error when parsing date string!");
        }

        int video = (hasVideo ? 1 : 0);

        values.put(TourList.COL_KEY_ID, keyID);
        values.put(TourList.COL_TOUR_ID, tourID);
        values.put(TourList.COL_TOUR_NAME, tourName);
        values.put(TourList.COL_DATE_CREATED, tCreated);
        values.put(TourList.COL_DATE_UPDATED, tUpdated);
        values.put(TourList.COL_DATE_EXPIRES_ON, tExpires);
        values.put(TourList.COL_DATE_LAST_ACCESSED, cal.getTimeInMillis());
        values.put(TourList.COL_HAS_VIDEO, video);

        db.insert(TourList.TABLE_NAME, null, values);
    }

    /**
     * Deletes a row in the db corresponding to a particular tour key.
     * @param db an SQLite db instance
     * @param keyID the ID of a tour key (not the tour ID itself)
     */
    public void deleteRow(SQLiteDatabase db, String keyID) {
        String selection = TourList.COL_TOUR_ID + " = ?";
        String[] args = {keyID};
        db.delete(TourList.TABLE_NAME, selection, args);
    }

    /**
     * Checks if there are any tours in the database.
     * @param db an SQLite db instance
     * @return true if the table is empty
     */
    public boolean dbIsEmpty(SQLiteDatabase db) {
        Cursor c = db.rawQuery(SQL_ROW_COUNT, null);
        c.moveToFirst();

        int count = c.getInt(0);
        c.close();

        return count == 0;
    }
}
