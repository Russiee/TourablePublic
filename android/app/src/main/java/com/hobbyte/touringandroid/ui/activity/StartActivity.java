package com.hobbyte.touringandroid.ui.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

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

    // used when downloading a tour for the first time
    private String tourID;
    private String keyID;
    private String keyName;
    private String expiryTimeString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //add toolbar to the screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarStart);
        toolbar.setTitle(getString(R.string.start_activity_your_tours));

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
        Resources res = getResources();

        if (dbHelper.dbIsEmpty()) {
            // no tours saved, so show the empty text
            View noToursText = getLayoutInflater().inflate(R.layout.text_no_tours, layout, false);
            layout.addView(noToursText);
            layout.getRootView().setBackgroundColor(res.getColor(R.color.colorPrimary));

            addTourButton.setBackgroundColor(res.getColor(R.color.colorEmptyStateButton));
            addTourButton.setTextColor(res.getColor(R.color.colorWhiteText));
            addTourButton.setText(getString(R.string.start_activity_add_tour));

            findViewById(R.id.toolbarStart).setVisibility(View.GONE);

        } else {

            addTourButton.setBackgroundColor(res.getColor(R.color.buttonGrey));
            addTourButton.setTextColor(res.getColor(R.color.colorDarkText));
            addTourButton.setText(getString(R.string.start_activity_add_new_tour));

            findViewById(R.id.toolbarStart).setVisibility(View.VISIBLE);

            layout.getRootView().setBackgroundColor(res.getColor(R.color.colorLightBackground));

            // fetches a cursor at position -1
            Cursor c = dbHelper.getTourDisplayInfo();

            Log.d(TAG, DatabaseUtils.dumpCursorToString(c)); // TODO remove this at some point

            while (c.moveToNext()) {
                // for each tour, add an item with tour name and expiry date
                View tourItem = getLayoutInflater().inflate(R.layout.text_tour_item, layout, false);

                TextView tourName = (TextView) tourItem.findViewById(R.id.textTourName);
                LinearLayout tour = (LinearLayout) tourItem.findViewById(R.id.tourItem);

                final String keyID = c.getString(0);
                final String tourID = c.getString(1);
                String name = c.getString(2);

                tour.setTag(keyID);
                tourName.setText(name);

                layout.addView(tourItem);

                tour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToTour(keyID, tourID, false, false);
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
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                EditText tourText = (EditText) view.findViewById(R.id.textKey);
                String key = tourText.getText().toString().trim();

                // remove whitespace and slashes to prevent errors further on
                key = key.replaceAll("[\\s\\/]+", "");
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
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();

        Resources res = getResources();
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(res.getColor(R.color.colorAccent));
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(res.getColor(R.color.colorAccent));
    }

    /**
     * Checks if the provided tour key is valid. If so, continue to the next activity, otherwise
     * inform the user that the key was invalid.
     */
    public void checkTourKey(String tourKey) {
        // current valid key: KCL-1010
        if (tourKey.length() < 1) return;

        if (TourDBManager.getInstance(getApplicationContext()).doesTourKeyNameExist(tourKey)) {
            showToast(getString(R.string.msg_tour_exists));
        } else if (ServerAPI.checkConnection()) {
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
    private void goToTour(String keyID, String tourID, boolean downloadNeeded, boolean withMedia) {
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.KEY_ID, keyID);
        intent.putExtra(SummaryActivity.TOUR_ID, tourID);
        intent.putExtra(SummaryActivity.KEY_NAME, keyName);
        intent.putExtra(SummaryActivity.DOWNLOAD, downloadNeeded);
        intent.putExtra(SummaryActivity.MEDIA, withMedia);

        if (expiryTimeString != null) {
            intent.putExtra(SummaryActivity.EXPIRY_TIME_STRING, expiryTimeString);
        }

        expiryTimeString = null;
        keyName = null;

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

                goToTour(keyID, tourID, true, false);
                dialog.cancel();
            }
        });

        Button media = (Button) view.findViewById(R.id.download_with_media);
        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goToTour(keyID, tourID, true, true);
                dialog.cancel();
            }
        });

        dialog.show();
        dialog.getButton(Dialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorAccent));
    }

    /**
     * Asynchronously checks the server to see if a key which the user entered is a real, valid key.
     * If the key is valid, take the user to the next activity.
     */
    private class KeyCheckTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            //key to check from parameters
            String key = params[0];

            //use key to query server
            JSONObject keyJSON = ServerAPI.checkKeyValidity(key);

            // if the server returns JSON, extract needed details
            if (keyJSON != null) {
                keyName = key;

                try {

                    //check if key has expired
                    //return false if it has
                    long newExpiry = TourDBManager.convertStampToMillis(keyJSON.getString("expiry"));
                    if (newExpiry < System.currentTimeMillis()) {
                        Log.d(TAG, String.format("Key Expired: keyExpiry: %d, current time %d",
                                newExpiry, System.currentTimeMillis()));
                        return false;
                    }

                    //get tour info
                    tourID = keyJSON.getJSONObject("tour").getString("objectId");
                    keyID = keyJSON.getString("objectId");
                    expiryTimeString = keyJSON.getString("expiry");

                    //make file structure for tour and save the keyJSON in there
                    FileManager.makeTourDirectories(keyID);
                    FileManager.saveJSON(keyJSON, keyID, FileManager.KEY_JSON);
                    Log.i(TAG, "KeyJSON saved");

                } catch (ParseException e) {
                    e.printStackTrace();
                    return false;
                } catch (JSONException e) {
                    e.printStackTrace();
                    tourID = null;
                    keyID = null;
                    expiryTimeString = null;
                    return false;
                }

                //key is valid
                return true;
            }

            //server api returned null json
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {
            if (isValid) {
                showDownloadDialog();
            } else {
                showToast(getString(R.string.msg_invalid_key));
            }
        }
    }
}
