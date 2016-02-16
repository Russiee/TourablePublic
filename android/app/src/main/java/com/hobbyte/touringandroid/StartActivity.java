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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StartActivity extends Activity {
    private static final String TAG = "StartActivity";

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

        SharedPreferences prefs = this.getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        boolean isFreshInstall = prefs.getBoolean(getString(R.string.prefs_is_new_install), true);

        // make the directory for tour media and set the "first install" flag to false
        if (isFreshInstall) {
            FileManager.makeTourDir(this);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(getString(R.string.prefs_is_new_install), false);
            editor.commit();

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
        }

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

        KeyCheckTask k = new KeyCheckTask();
        k.execute(tourKey);
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
        // TODO: this queries the api for tour data but should be looking at the file system
        ArrayList<SubSection> subsectionList = new ArrayList<SubSection>();

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                tour = ServerAPI.allocateTourSections(tourId);
            }
        });

        t.start();

        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        subsectionList = tour.getSubSections();

        // temporary measure
        if (subsectionList != null) {
            Intent intent = new Intent(this, TourActivity.class);
            intent.putExtra(TourActivity.EXTRA_MESSAGE_SUB, subsectionList);
            startActivity(intent);
        } else {
            Log.w(TAG, "Tour subsectionlist was null!");
        }
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
            c.moveToPosition(-1);

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
                final String tourId = c.getString(c.getColumnIndex(TourDBContract.TourList.COL_TOUR_ID));

                tourName.setText(name);
                expiryDate.setText(expiryText);
                layout.addView(tourItem);

                tourItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToTour(tourId);
                    }
                });
            }

            c.close();

        }

        db.close();

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

            String tourId = ServerAPI.checkKeyValidity(key);

            // if a valid tourId is returned, store it in SharedPreferences so that the key can
            // be used elsewhere in the app
            if (tourId != null) {
                SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                        getString(R.string.preference_file_key),
                        Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(getString(R.string.prefs_current_tour), tourId);
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            if (isValid) {
                goToTourDownload();
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Invalid tour key", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM, 0, 20);
                toast.show();
                textKey.setText("");
            }
        }
    }
}
