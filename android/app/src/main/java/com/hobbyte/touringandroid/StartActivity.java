package com.hobbyte.touringandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.hobbyte.touringandroid.helpers.BackAwareEditText;
import com.hobbyte.touringandroid.helpers.FileManager;
import com.hobbyte.touringandroid.helpers.TourDBContract;
import com.hobbyte.touringandroid.helpers.TourDBManager;
import com.hobbyte.touringandroid.internet.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";

    public static Context CONTEXT;

    private static boolean FADE_IN = true;
    private static boolean FADE_OUT = false;
    private LinearLayout keyEntryLayout;
    private LinearLayout previousToursLayout;
    private BackAwareEditText textKey;
    private Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        CONTEXT = getApplicationContext();

        SharedPreferences prefs = this.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        boolean isFreshInstall = prefs.getBoolean(getString(R.string.prefs_is_new_install), true);

        // make the directory for tour media and set the "first install" flag to false
        /*if (isFreshInstall) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getString(R.string.prefs_is_new_install), false);
            editor.apply();

            // this is a temporary measure to get a previous tour
            TourDBManager dbHelper = new TourDBManager(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            dbHelper.putRow(db,
                    "49L6FrRwe4",
                    "DnPRFaSYEk",
                    "Ultimate Flat Tour",
                    "2016-02-12T15:51:17.125Z",
                    "2016-02-12T15:51:17.125Z",
                    "2016-02-25T00:39:31.000Z",
                    false);
            db.close();
        }*/

        /*TourDBManager dbHelper = new TourDBManager(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.putRow(db,
                "49L6FrRwe4",
                "DnPRFaSYEk",
                "Ultimate Flat Tour",
                "2016-02-12T15:51:17.125Z",
                "2016-02-12T15:51:17.125Z",
                "2016-02-25T00:39:31.000Z",
                false);
        db.close();*/

//        TourDBManager dbHelper = new TourDBManager(this);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        dbHelper.deleteTour(db, "49L6FrRwe4"); db.close();

        //get references for animations
        keyEntryLayout = (LinearLayout) findViewById(R.id.keyEntryLayout);
        previousToursLayout = (LinearLayout) findViewById(R.id.previousToursLayout);
        textKey = (BackAwareEditText) findViewById(R.id.textEnterTour);
        textKey.setCallBackClass(this);

        //make the FAB do something
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInput();
            }
        });

        loadPreviousTours();
        new PoopTask().execute("http://i.imgur.com/sJJ06P6.jpg");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart() called");
    }

    @Override
    protected void onPause() {
        //need this so if user switches app and come back, the input fields do not show
        hideInput();
        super.onPause();
    }

    public static Context getContext() {
        return CONTEXT;
    }

    /**
     * Shows input box, keyboard and hides the existing tours table
     */
    private void showInput() {
        //layout.xml defines the layout as invisible. Otherwise it shows when the app is loaded.
        keyEntryLayout.setVisibility(View.VISIBLE);

        fade(keyEntryLayout, FADE_IN);
        fade(previousToursLayout, FADE_OUT);

        showKeyboard();
    }

    /**
     * Hides the input box, shows the existing tours table
     */
    public void hideInput() {
        fade(keyEntryLayout, FADE_OUT);
        fade(previousToursLayout, FADE_IN);
        textKey.clearFocus();
    }

    /**
     * Shows the keyboard
     */
    private void showKeyboard() {

        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.showSoftInput(textKey, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Fades a view from opaque to hidden or visa versa
     *
     * @param view   view to fade
     * @param fadeIn true if transparent to opaque, false if opaque to transparent
     */
    private void fade(View view, boolean fadeIn) {
        //current alpha --> opaque or transparent
        AlphaAnimation a = new AlphaAnimation(view.getAlpha(), fadeIn ? 1.0f : 0.0f);
        a.setDuration(400); //system 'medium' animation time
        a.setFillAfter(true); //so alpha sticks
        view.startAnimation(a); //run
    }

    /**
     * Checks if the provided tour key is valid. If so, continue to the next activity, otherwise
     * inform the user that the key was invalid.
     * @param v the submit button
     */
    public void checkTourKey(View v) {
        // current valid key: KCL-1010

        String tourKey = textKey.getText().toString();

        // check if key has already been used
        TourDBManager dbHelper = new TourDBManager(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        boolean exists = dbHelper.doesTourExist(db, tourKey);
        db.close();

        if (exists) {
            showToast(getString(R.string.msg_tour_exists));
            textKey.setText("");
        } else if (ServerAPI.checkConnection(this)) {
            // only check the key if we have an internet connection
            KeyCheckTask k = new KeyCheckTask();
            k.execute(tourKey);
        } else {
            showToast(getString(R.string.msg_no_internet));
            textKey.setText("");
        }
    }

    /**
     * Called from KeyCheckTask when a valid key is entered. It takes the user to an Activity with
     * download options for the tour media.
     */
    private void goToTourDownload() {
        // TODO: this currently goes to a new tour activity, but needs to go to the download screen

        String tourKey = textKey.getText().toString();
        textKey.setText("");

        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }

    private void goToTour(final String tourId) {
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.KEY_ID, tourId);
        startActivity(intent);
    }

    /**
     * If the user has tours saved to the device, show their names and expiry information.
     */
    private void loadPreviousTours() {
        TourDBManager dbHelper = new TourDBManager(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        LinearLayout layout = (LinearLayout) findViewById(R.id.previousToursLayout);

        if (dbHelper.dbIsEmpty(db)) {
            // no tours saved, so show the empty text
            View noToursText = getLayoutInflater().inflate(R.layout.text_no_tours, layout, false);
            layout.addView(noToursText);
        } else {
            // fetches a cursor pointing at the first row in the db
            Cursor c = dbHelper.getTours(db);

            Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

            DateFormat df = DateFormat.getDateInstance();

            while (c.moveToNext()) {
                // for each tour, add an item with tour name and expiry date
                View tourItem = getLayoutInflater().inflate(R.layout.text_tour_item, layout, false);

                TextView tourName = (TextView) tourItem.findViewById(R.id.textTourName);
                TextView expiryDate = (TextView) tourItem.findViewById(R.id.textTourExpiry);

                String name = c.getString(c.getColumnIndex(TourDBContract.TourList.COL_TOUR_NAME));
                long expiryTime = c.getLong(c.getColumnIndex(TourDBContract.TourList.COL_DATE_EXPIRES_ON));
                String expiryText = df.format(new Date(expiryTime));
                final String keyID = c.getString(c.getColumnIndex(TourDBContract.TourList.COL_TOUR_ID)); // TODO: this should be changed to COL_KEY_ID

                tourName.setText(name);
                expiryDate.setText(expiryText);
                layout.addView(tourItem);

                tourItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToTour(keyID);
                    }
                });
            }

            c.close();

        }

        db.close();

    }

    /**
     * Shows a Toast message at the bottom of the screen.
     *
     * @param message the message to show
     */
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 20);
        toast.show();
    }

    /**
     * Asynchronously checks the server to see if a key which the user entered is a real, valid key.
     * If the key is valid, take the user to the next activity.
     */
    private class KeyCheckTask extends AsyncTask<String, Void, Boolean> {
        private static final String TAG = "KeyCheckTask";

        @Override
        protected Boolean doInBackground(String... params) {
            String key = params[0];

            JSONObject keyJSON = ServerAPI.checkKeyValidity(key);

            // if the server returns JSON, extract needed details
            if (keyJSON != null) {
                String tourID;
                String keyID;
                String keyExpiryDate;

                try {
                    tourID = keyJSON.getJSONObject("tour").getString("objectId");
                    keyID = keyJSON.getString("objectId");
                    keyExpiryDate = keyJSON.getString("expiresAt");
                } catch (JSONException e) {
                    e.printStackTrace();
                    tourID = null;
                    keyID = null;
                    keyExpiryDate = null;
                }

                // check if key has already been used
                TourDBManager dbHelper = new TourDBManager(getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                boolean exists = dbHelper.doesTourExist(db, keyID);
                db.close();

                if (exists) {
                    return null;
                }

                SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE);

                /*
                The JSON returned above is purely for the key. It contains several key pieces of
                information:
                    - keyID used to create a folder for storing tour media
                    - tourID used to fetch the tour bundle
                    - expiresAt needs to be stored in the local DB
                All other info comes from the tour/bundle json, which will be saved on the device.
                 */
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.prefs_current_tour), tourID);
                editor.putString(getString(R.string.prefs_current_key), keyID);
                editor.putString(getString(R.string.prefs_current_expiry), keyExpiryDate);
                editor.apply();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            if (isValid == null) {
                showToast(getString(R.string.msg_tour_exists));
            } else if (isValid) {
                goToTourDownload();
            } else {
                showToast(getString(R.string.msg_invalid_key));
            }
            textKey.setText("");
        }
    }

    private class PoopTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            FileManager.saveImage(getApplicationContext(), "poop", params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d(TAG, "saved the image");
        }
    }
}
