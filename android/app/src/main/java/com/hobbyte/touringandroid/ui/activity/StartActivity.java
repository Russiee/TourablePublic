package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.internet.UpdateChecker;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jonathan
 * @author Max
 * @author Nikita
 *         <p/>
 *         The opening actiivty of the app.
 *         Displays previously downloaded tours and provides functionality to add new tours.
 */
public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";

    private String tourID;
    private String keyID;

    private String expiryTimeString;
    private long expiryTimeLong;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new UpdateChecker(getApplicationContext()).start();

        //add toolbar to the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarStart);
        ((TextView) toolbar.findViewById(R.id.toolbar_title))
                .setText(getString(R.string.start_activity_your_tours));
        setSupportActionBar(toolbar);

        //assign action the the addTourButton
        Button addTourButton = (Button) findViewById(R.id.addTourButton);
        addTourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInput();
            }
        });


        loadPreviousTours();
    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(getApplicationContext()).close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LinearLayout layout = (LinearLayout) findViewById(R.id.previousToursLayout);
        layout.removeAllViews();

        loadPreviousTours();
    }

    /**
     * Creates a context menu for deleting on LongPress
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete");
        keyID = v.getTag().toString();
    }

    /**
     * Executes deletion and recreates activity on selecting delete
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        FileManager.removeTour(getApplicationContext(), keyID);
        this.recreate();
        return true;
    }

    /**
     * If the user has tours saved to the device, show their names and expiry information.
     */
    private void loadPreviousTours() {
        TourDBManager dbHelper = TourDBManager.getInstance(getApplicationContext());

        LinearLayout layout = (LinearLayout) findViewById(R.id.previousToursLayout);
        Button addTourButton = (Button) findViewById(R.id.addTourButton);

        if (dbHelper.dbIsEmpty()) {
            // no tours saved, so show the empty text
            View noToursText = getLayoutInflater().inflate(R.layout.text_no_tours, layout, false);
            layout.addView(noToursText);
            layout.getRootView().setBackgroundColor(Color.parseColor("#162b49"));

            addTourButton.setBackgroundColor(Color.parseColor("#37435B"));
            addTourButton.setTextColor(Color.parseColor("#D2DBEC"));
            addTourButton.setText(getString(R.string.start_activity_add_tour));

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.hide();

        } else {

            Resources res = getResources();

            addTourButton.setBackgroundColor(res.getColor(R.color.buttonGrey));
            addTourButton.setTextColor(res.getColor(R.color.colorDarkText));
            addTourButton.setText(getString(R.string.start_activity_add_new_tour));

            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) actionBar.show();

            layout.getRootView().setBackgroundColor(res.getColor(R.color.colorLightBackground));

            // fetches a cursor at position -1
            Cursor c = dbHelper.getTourDisplayInfo();

            Log.d(TAG, DatabaseUtils.dumpCursorToString(c)); // TODO remove this at some point

            while (c.moveToNext()) {
                // for each tour, add an item with tour name and expiry date
                View tourItem = getLayoutInflater().inflate(R.layout.text_tour_item, layout, false);

                TextView tourName = (TextView) tourItem.findViewById(R.id.textTourName);
                LinearLayout tour = (LinearLayout) tourItem.findViewById(R.id.tourItem);

                keyID = c.getString(0);
                tourID = c.getString(1);
                String name = c.getString(2);
                expiryTimeLong = c.getLong(3);

                tour.setTag(keyID);
                tourName.setText(name);

                layout.addView(tourItem);

                tour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToTour(false, false);
                    }
                });

                tour.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        registerForContextMenu(v);
                        openContextMenu(v);
                        return true;
                    }
                });
            }

            c.close();
        }
    }

    /**
     * Shows input box, keyboard and hides the existing tours table
     */
    private void showInput() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();

        final View view = inflater.inflate(R.layout.add_tour_dialog, null);
        builder.setView(view);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText tourText = (EditText) view.findViewById(R.id.textKey);
                String key = tourText.getText().toString();
                checkTourKey(key);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Checks if the provided tour key is valid. If so, continue to the next activity, otherwise
     * inform the user that the key was invalid.
     */
    public void checkTourKey(String tourKey) {

        if (ServerAPI.checkConnection(this)) {
            // only check the key if we have an internet connection
            KeyCheckTask k = new KeyCheckTask();
            k.execute(tourKey);
        } else {
            showToast(getString(R.string.msg_no_internet));
        }
    }

    /**
     * Moves the app to the {@link SummaryActivity}, ready to start the tour
     */
    private void goToTour(boolean downloadNeeded, boolean withMedia) {

        TourDBManager.getInstance(getApplicationContext()).updateAccessedTime(keyID);
        Intent intent = new Intent(StartActivity.this, SummaryActivity.class);

        intent.putExtra(SummaryActivity.KEY_ID, keyID);
        intent.putExtra(SummaryActivity.TOUR_ID, tourID);
        intent.putExtra(SummaryActivity.DOWNLOAD, downloadNeeded);
        intent.putExtra(SummaryActivity.MEDIA, withMedia);

        if (expiryTimeString != null) {
            intent.putExtra(SummaryActivity.EXPIRY_TIME_STRING, expiryTimeString);
        } else {
            intent.putExtra(SummaryActivity.EXPIRY_TIME_LONG, expiryTimeLong);
        }

        startActivity(intent);
    }


    /**
     * Shows a Toast message at the bottom of the screen.
     *
     * @param message the message to show
     */
    private void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Displays a dialog displaying download options (With/Without Media) and opens a new intent accordingly
     */
    private void showDownloadDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_download, null);

        builder.setView(view);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = builder.create();

        Button noMedia = (Button) view.findViewById(R.id.download_without_media);
        noMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToTour(true, false);
                dialog.cancel();
            }
        });

        Button media = (Button) view.findViewById(R.id.download_with_media);
        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToTour(true, true);
                dialog.cancel();
            }
        });

        dialog.show();
    }

    /**
     * Asynchronously checks the server to see if a key which the user entered is a real, valid key.
     * If the key is valid, take the user to the next activity.
     */
    private class KeyCheckTask extends AsyncTask<String, Void, Boolean> {

        private boolean exists;

        @Override
        protected Boolean doInBackground(String... params) {
            String key = params[0];

            JSONObject keyJSON = ServerAPI.checkKeyValidity(key);

            // if the server returns JSON, extract needed details
            if (keyJSON != null) {

                try {
                    tourID = keyJSON.getJSONObject("tour").getString("objectId");
                    keyID = keyJSON.getString("objectId");
                    exists = TourDBManager.getInstance(getApplicationContext()).doesTourExist(keyID);
                    if (exists) {
                        return false;
                    }

                    expiryTimeString = keyJSON.getString("expiry");

                    FileManager.makeTourDirectories(keyID);
                    FileManager.saveJSON(keyJSON, keyID, FileManager.KEY_JSON);
                    Log.i(TAG, "KeyJSON saved");

                } catch (JSONException e) {
                    e.printStackTrace();
                    tourID = null;
                    keyID = null;
                    expiryTimeString = null;
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
                editor.apply();
                return true;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {

            if (isValid) {
                showDownloadDialog();
            } else if (exists) {
                showToast(getString(R.string.msg_tour_exists));
            } else {
                showToast(getString(R.string.msg_invalid_key));
            }
        }
    }
}
