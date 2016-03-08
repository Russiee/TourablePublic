package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.internet.UpdateChecker;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBContract;
import com.hobbyte.touringandroid.io.TourDBManager;
import com.hobbyte.touringandroid.ui.BackAwareEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

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

    //for animations
    private static boolean FADE_IN = true;
    private static boolean FADE_OUT = false;
    private boolean inputPhase = false;

    private LinearLayout keyEntryLayout;
    private LinearLayout previousToursLayout;
    private BackAwareEditText textKey;
    private Button submitButton;

    private String keyID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

//        FileManager.removeTour(getApplicationContext(), "APd4HhtBm9");
//        new UpdateChecker(getApplicationContext()).start();

        //get references for animations
        keyEntryLayout = (LinearLayout) findViewById(R.id.keyEntryLayout);
        previousToursLayout = (LinearLayout) findViewById(R.id.previousToursLayout);
        textKey = (BackAwareEditText) findViewById(R.id.textEnterTour);
        textKey.setCallBackClass(this);


        // make the FAB do something
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
    protected void onPause() {
        // need this so if user switches app and come back, the input fields do not show
        hideInput();

        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(this).close();

        super.onPause();
    }

    /**
     * If the user has tours saved to the device, show their names and expiry information.
     */
    private void loadPreviousTours() {
        TourDBManager dbHelper = TourDBManager.getInstance(this);

        LinearLayout layout = (LinearLayout) findViewById(R.id.previousToursLayout);

        if (dbHelper.dbIsEmpty()) {
            // no tours saved, so show the empty text
            View noToursText = getLayoutInflater().inflate(R.layout.text_no_tours, layout, false);
            layout.addView(noToursText);
        } else {
            // fetches a cursor at position -1
            Cursor c = dbHelper.getTours();

            Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

            DateFormat df = DateFormat.getDateInstance();

            while (c.moveToNext()) {
                // for each tour, add an item with tour name and expiry date
                View tourItem = getLayoutInflater().inflate(R.layout.text_tour_item, layout, false);

                TextView tourName = (TextView) tourItem.findViewById(R.id.textTourName);
                TextView expiryDate = (TextView) tourItem.findViewById(R.id.textTourExpiry);
                RelativeLayout tour = (RelativeLayout) tourItem.findViewById(R.id.tourItem);
                ImageView delete = (ImageView) tourItem.findViewById(R.id.deleteImage);

                String name = c.getString(c.getColumnIndex(TourDBContract.TourList.COL_TOUR_NAME));
                long expiryTime = c.getLong(c.getColumnIndex(TourDBContract.TourList.COL_DATE_EXPIRES_ON));
                String expiryText = df.format(new Date(expiryTime));
                final String keyID = c.getString(c.getColumnIndex(TourDBContract.TourList.COL_KEY_ID));
                final String tourID = c.getString(c.getColumnIndex(TourDBContract.TourList.COL_TOUR_ID));
                tour.setTag(keyID);
                Log.d(TAG, String.format("k: %s t: %s", keyID, tourID));
                tourName.setText(name);
                expiryDate.setText(String.format("Expires %s", expiryText));
                layout.addView(tourItem);

                tour.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToTour(keyID, tourID);
                    }
                });

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new AlertDialog.Builder(new ContextThemeWrapper(StartActivity.this, R.style.dialogTheme)).setTitle("Delete tour").setMessage("Are you sure you want to delete this tour?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int temp) {
                                        FileManager.removeTour(App.context, keyID);
                                        finish();
                                        startActivity(getIntent());
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface d, int temp) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert).show();
                    }
                });

                tour.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        System.out.println(v.getTag().toString());
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
     * Hides the input box, shows the existing tours table
     */
    public void hideInput() {
        if (inputPhase) {
            fade(keyEntryLayout, FADE_OUT);
            fade(previousToursLayout, FADE_IN);
            textKey.clearFocus();
            inputPhase = false;
        }
    }

    /**
     * Shows input box, keyboard and hides the existing tours table
     */
    private void showInput() {
        inputPhase = true;
        //layout.xml defines the layout as invisible. Otherwise it shows when the app is loaded.
        keyEntryLayout.setVisibility(View.VISIBLE);

        fade(keyEntryLayout, FADE_IN);
        fade(previousToursLayout, FADE_OUT);

        showKeyboard();
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
     *
     * @param v the submit button
     */
    public void checkTourKey(View v) {
        // current valid key: KCL-1010
        if (textKey.length() < 3)
            return; // TODO ask if there's a minimum Key length. Otherwise do 0

        String tourKey = textKey.getText().toString();

        // check if key has already been used
        boolean exists = TourDBManager.getInstance(this).doesTourExist(tourKey);

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
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }

    /**
     * Moves the app to the {@link SummaryActivity}, ready to start the tour
     *
     * @param keyID tour to start
     */
    private void goToTour(String keyID, String tourID) {
        TourDBManager.getInstance(this).updateAccessedTime(keyID);
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.KEY_ID, keyID);
        intent.putExtra(SummaryActivity.TOUR_ID, tourID);
        startActivity(intent);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Delete");
        keyID = v.getTag().toString();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        FileManager.removeTour(this, keyID);
        this.recreate();
        return true;
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

        @Override
        protected void onPreExecute() {
            submitButton = (Button) findViewById(R.id.buttonSubmitKey);
            submitButton.setEnabled(false);
            textKey.setEnabled(false);
        }

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
            textKey.setText("");
            submitButton.setEnabled(true);
            textKey.setEnabled(true);

            if (isValid) {
                goToTourDownload();
            } else {
                showToast(getString(R.string.msg_invalid_key));
            }
        }
    }
}
