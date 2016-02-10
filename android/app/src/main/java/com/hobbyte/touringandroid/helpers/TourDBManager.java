package com.hobbyte.touringandroid.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hobbyte.touringandroid.helpers.TourDBContract.TourList;
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
                    TourList.COL_TOUR_KEY + "TEXT PRIMARY KEY," +
                    TourList.COL_DATE_EXPIRES_ON + " NUMERIC," +
                    TourList.COL_DATE_LAST_ACCESSED + " NUMERIC" +
                    TourList.COL_HAS_VIDEO + "INTEGER DEFAULT 0" +
            ")";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TourList.TABLE_NAME;

    private static final String SQL_ROW_COUNT =
            "SELECT COUNT(*) FROM " + TourList.TABLE_NAME;

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

    public boolean dbIsEmpty(SQLiteDatabase db) {
        Log.d(TAG, "SQL: empty db?");

        Cursor c = db.rawQuery(SQL_ROW_COUNT, new String[] {});
        int count = c.getCount();
        c.close();

        return count == 0;
    }
}
