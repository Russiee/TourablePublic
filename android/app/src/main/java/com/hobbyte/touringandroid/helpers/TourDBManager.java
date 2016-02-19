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
        // TODO - figure out what exactly should go here
        Log.w(TAG, "A call to onUpgrade() was made.");
    }

    /**
     * Fetches every row in the table, ordered by when tours were last access by the user.
     *
     * @param db an SQLite DB instance
     * @return a Cursor pointing to the first row of the table
     */
    public Cursor getTours(SQLiteDatabase db) {
        Cursor c = db.rawQuery(SQL_GET_TOURS, null);
        c.moveToFirst();
        return c;
    }

    /**
     * Insert a tour entry into the db. All fields, with the exception of `db` and `hasVideo`, will
     * be extracted from JSON that was fetched from the server.
     * <p>
     * All the timestamp parameters should be passed in the string form in which the server stores
     * them. They will be converted into, and stored as, milliseconds since Epoch.
     *
     * @param db an SQLite DB instance
     * @param keyID the objectId of the key used to fetch the tour
     * @param tourID the objectId of the tour itself
     * @param tourName the tour's title
     * @param creationDate when the tour was created
     * @param updateDate when the tour was last updated
     * @param expiryDate when the tour expires
     * @param hasVideo whether or not the user opted to download the tour with video
     */
    public void putRow(SQLiteDatabase db, String keyID, String tourID, String tourName,
                       String creationDate, String updateDate, String expiryDate, boolean hasVideo) {
        if (db.isReadOnly()) {
            Log.w(TAG, "Can't write to this DB!!!");
            return;
        }

        int video = (hasVideo ? 1 : 0);
        long[] datetimes;

        try {
            datetimes = convertStampToMillis(dateFormat, creationDate, updateDate, expiryDate);
        } catch (ParseException e) {
            // is there a better way to handle this?
            datetimes = new long[] {1, 1, 1};
        }

        ContentValues values = new ContentValues();

        values.put(TourList.COL_KEY_ID, keyID);
        values.put(TourList.COL_TOUR_ID, tourID);
        values.put(TourList.COL_TOUR_NAME, tourName);
        values.put(TourList.COL_DATE_CREATED, datetimes[0]);
        values.put(TourList.COL_DATE_UPDATED, datetimes[1]);
        values.put(TourList.COL_DATE_EXPIRES_ON, datetimes[2]);
        values.put(TourList.COL_DATE_LAST_ACCESSED, Calendar.getInstance().getTimeInMillis());
        values.put(TourList.COL_HAS_VIDEO, video);

        db.insert(TourList.TABLE_NAME, null, values);
    }

    /**
     * Deletes a row in the db corresponding to a particular tour key.
     *
     * @param db an SQLite db instance
     * @param keyID the ID of a tour key (not the tour ID itself)
     */
    public void deleteRow(SQLiteDatabase db, String keyID) {
        String selection = TourList.COL_TOUR_ID + " = ?";
        String[] args = {keyID};
        db.delete(TourList.TABLE_NAME, selection, args);
    }

    /**
     * Sets the `lastAccessedOn` field of a tour to be the current time. Should be used when the
     * user selects a tour from the StartActivity.
     *
     * @param db an SQLite db instance
     * @param keyID the ID of a tour key (not the tour ID itself)
     */
    public void updateAccessedTime(SQLiteDatabase db, String keyID) {
        ContentValues values = new ContentValues();
        values.put(TourList.COL_DATE_LAST_ACCESSED, Calendar.getInstance().getTimeInMillis());

        String where = TourList.COL_KEY_ID + " = ?";
        String[] whereArgs = {keyID};

        db.update(TourList.TABLE_NAME, values, where, whereArgs);
    }

    /**
     * Checks if there are any tours in the database.
     *
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

    /**
     * Takes one or more timestamps and converts them into milliseconds since Epoch.
     *
     * @param dateFormat a String representing the format/pattern of the timestamp
     * @param timeArgs one or more timestamps
     * @return millisecond representations of the provided timestamps
     * @throws ParseException if the timestamps don't match the dateFormat
     */
    public long[] convertStampToMillis(String dateFormat, String... timeArgs) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(dateFormat);

        long[] toReturn = new long[timeArgs.length];

        for (int i = 0; i < timeArgs.length; i++) {
            Date date = df.parse(dateFormat);
            toReturn[i] = date.getTime();
        }

        return toReturn;
    }
}
