package com.hobbyte.touringandroid.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hobbyte.touringandroid.helpers.TourDBContract.TourList;

import java.util.Calendar;

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

    /*=============================================
        SQL QUERY STRINGS
     =============================================*/

    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TourList.TABLE_NAME + " (" +
                    TourList.COL_TOUR_KEY + " TEXT PRIMARY KEY," +
                    TourList.COL_DATE_EXPIRES_ON + " NUMERIC," +
                    TourList.COL_DATE_LAST_ACCESSED + " NUMERIC" +
                    TourList.COL_HAS_VIDEO + "INTEGER DEFAULT 0" +
            ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TourList.TABLE_NAME;

    private static final String SQL_ROW_COUNT =
            "SELECT COUNT(" + TourList.COL_TOUR_KEY + ") FROM " + TourList.TABLE_NAME;

    private static final String SQL_GET_ALL =
            "SELECT * FROM " + TourList.TABLE_NAME;

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

    public void putRow(SQLiteDatabase db, String tourKey, long expiryDate, boolean hasVideo) {
        if (db.isReadOnly()) {
            Log.w(TAG, "Can't write to this DB!!!");
            return;
        }
        ContentValues values = new ContentValues();
        Calendar cal = Calendar.getInstance();
        int video = (hasVideo ? 1 : 0);

        values.put(TourList.COL_TOUR_KEY, tourKey);
        values.put(TourList.COL_DATE_EXPIRES_ON, expiryDate);
        values.put(TourList.COL_DATE_LAST_ACCESSED, cal.getTimeInMillis());
        values.put(TourList.COL_HAS_VIDEO, video);

        db.insert(TourList.TABLE_NAME, null, values);
    }

    public void deleteRow(SQLiteDatabase db, String tourKey) {
        String selection = TourList.COL_TOUR_KEY + " = ?";
        String[] args = {tourKey};
        db.delete(TourList.TABLE_NAME, selection, args);
    }

    public boolean dbIsEmpty(SQLiteDatabase db) {
        Cursor c = db.rawQuery(SQL_ROW_COUNT, null);
        c.moveToFirst();

        c.moveToFirst();
        int count = c.getInt(0);
        c.close();

        return count == 0;
    }
}
