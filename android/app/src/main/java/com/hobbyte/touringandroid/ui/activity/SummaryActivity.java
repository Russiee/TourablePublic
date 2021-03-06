package com.hobbyte.touringandroid.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.io.DownloadTourTask;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * This activity has two forms. In the first it downloads a tour, displaying progress. In the
 * second, it shows some basic information about the tour: it's description, if it's up to date or
 * has an update available, and how much time is left until it expires.
 */
public class SummaryActivity extends AppCompatActivity {
    private static final String TAG = "SummaryActivity";

    public static final String KEY_ID = "keyID";
    public static final String TOUR_ID = "tourID";
    public static final String KEY_NAME = "keyName";
    public static final String EXPIRY_TIME_STRING = "expiryTimeString";
    public static final String DOWNLOAD = "download";
    public static final String MEDIA = "media";
    public static final String UPDATING = "updating";

    private static ProgressHandler handler;

    private LinearLayout updateTour;
    private ImageButton updateButton;
    private TextView updateText;

    private String keyID;
    private String tourID;
    private String keyName;
    private Boolean withMedia;
    private String expiryTimeString;
    private Boolean updating;

    private int durationMinutes;
    private int durationHours;
    private int durationDays;

    private JSONObject tourJSON;

    private Toolbar toolbar;
    private TextView txtDescription;

    private RelativeLayout downloadLayout;
    private RelativeLayout tourCard;
    private RelativeLayout buttonLayout;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Log.i(TAG, "Creating SummaryActivity");

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);
        tourID = intent.getStringExtra(TOUR_ID);
        keyName = intent.getStringExtra(KEY_NAME);

        // this will be present if the tour is being downloaded for the first time, in which
        // case the expiry date will be retrieved from the key JSON
        expiryTimeString = intent.getStringExtra(EXPIRY_TIME_STRING);

        // tells the app if the tour is to be downloaded with/without media
        withMedia = intent.getBooleanExtra(MEDIA, false);

        // true if the tour is going to be downloaded or updated
        Boolean doDownload = intent.getBooleanExtra(DOWNLOAD, false);

        // true if the tour is going to be updated, as opposed to downloaded for the first time
        updating = intent.getBooleanExtra(UPDATING, false);

        downloadLayout = (RelativeLayout) findViewById(R.id.downloadLayout);
        tourCard = (RelativeLayout) findViewById(R.id.tourCard);
        buttonLayout = (RelativeLayout) findViewById(R.id.buttonLayout);
        downloadButton = (Button) findViewById(R.id.downloadButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtDescription = (TextView) findViewById(R.id.txtTourDescription);


        if (doDownload) {
            handler = new ProgressHandler(this);
            findViewById(R.id.downloadLayout).setVisibility(View.VISIBLE);
            executeDownload();
        } else {
            parseDate();
            displaySummary();
        }

    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(getApplicationContext()).close();

        super.onPause();
    }

    /**
     * Starts the TourActivity for this tour.
     */
    public void openTourActivity(View v) {
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.INTENT_KEY_ID, keyID);
        startActivity(intent);
        this.finish();
    }

    /**
     * Display the description and information regarding the corresponding tour
     */
    private void displayTourInfo() {
        tourJSON = FileManager.getJSON(keyID, FileManager.TOUR_JSON);
        TextView timeTourTakes = (TextView) findViewById(R.id.txtEstimatedTime);

        try {
            int timeForTour = tourJSON.getInt("estimatedTime");
            int tourHours = timeForTour / 60;
            int tourMinutes = timeForTour % 60;
            toolbar.setTitle(tourJSON.getString("title"));
            txtDescription.setText(tourJSON.getString("description"));
            timeTourTakes.setText(String.format("Estimated time for tour: %d hours, %d minutes", tourHours, tourMinutes));
        } catch (JSONException e) {
            e.printStackTrace();
            toolbar.setTitle("Error");
            txtDescription.setText("Error");
        }
    }

    /**
     * Checks for updates to the Tour, if no updates displays message that the version is the current version
     * Otherwise displays button to allow for updating
     */
    private void displayVersionAndUpdate() {

        updateButton = (ImageButton) findViewById(R.id.updateTourButton);
        updateText = (TextView) findViewById(R.id.updateTourText);
        updateTour = (LinearLayout) findViewById(R.id.updateTour);

        if (TourDBManager.getInstance(getApplicationContext()).doesTourHaveUpdate(keyID)) {
            displayUpdateOption();
        }
    }

    /**
     * Displays the button options for updating the tour
     */
    private void displayUpdateOption() {
        TextView version = (TextView) findViewById(R.id.txtVersion);
        version.setText(getApplicationContext().getString(
                R.string.summary_activity_new_version_is_available));

        updateText.setVisibility(View.VISIBLE);
        updateText.setTextColor(ContextCompat.getColor(App.context, R.color.colorPrimaryLight));
        updateText.setClickable(true);
        updateButton.setVisibility(View.INVISIBLE);
        updateTour.setTag("ToUpdate");
    }

    /**
     * Displays the tours expiry in Days or Hours and Minutes
     */
    private void displayExpiry() {

        TextView txtExpiry = (TextView) findViewById(R.id.txtExpiry);

        StringBuilder sb = new StringBuilder(getString(R.string.expires_in));
        sb.append(" "); //because android string resources strip tailing and leading spaces

        //number of days, days or day
        if (durationDays > 1) {
            sb.append(durationDays);
            sb.append(" ");
            sb.append(getString(R.string.expires_in_days));
        } else if (durationDays == 1) {
            sb.append(durationDays);
            sb.append(" ");
            sb.append(getString(R.string.expires_in_day));
        } else {

            //if there are no days left, then set text to red and display hours & minutes
            txtExpiry.setTextColor(ContextCompat.getColor(App.context, R.color.red));

            //number of hours, hours or hour
            if (durationHours > 1) {
                sb.append(durationHours);
                sb.append(" ");
                sb.append(getString(R.string.expires_in_hours));
            } else if (durationHours == 1) {
                sb.append(durationHours);
                sb.append(getString(R.string.space));
                sb.append(getString(R.string.expires_in_hour));
            }

            //if there are minutes to add
            if (durationMinutes != 0) {
                //check number of hours if we hav minutes, because we don't want to add
                // 'and' ifn there are no hours to add to
                if (durationHours >= 1) {
                    sb.append(" and ");
                }

                //number of minutes, minutes or minute
                if (durationMinutes > 1) {
                    sb.append(durationMinutes);
                    sb.append(" ");
                    sb.append(getString(R.string.expires_in_minutes));
                } else {
                    sb.append(durationMinutes);
                    sb.append(" ");
                    sb.append(getString(R.string.expires_in_minute));
                }
            }
        }
        txtExpiry.setText(sb.toString());
    }

    /**
     * Start downloading the tour
     */
    public void executeDownload() {
        new FetchTourJSON().execute();
    }

    /**
     * Downloads the JSON for a tour object on the server. It is used to get the tour title and
     * description in this activity, plus in TourActivity.
     */
    private class FetchTourJSON extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            tourJSON = ServerAPI.getJSON(tourID, ServerAPI.TOUR);
            if (tourJSON != null) {
                FileManager.saveJSON(getApplicationContext(), tourJSON, keyID, FileManager.TOUR_JSON);
                return true;
            } else {
                Log.e(TAG, "tourJSON was null");
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean tourJSONExists) {
            if (tourJSONExists) {
                displayTourInfo();
                DownloadTourTask dThread = new DownloadTourTask(handler, keyID, tourID, withMedia);
                dThread.start();
            } else {
                Log.e(TAG, "TourJSON not fetched from server");
                onTourDownloadFailedActions();
            }
        }
    }

    /**
     * Handles messages from a non-UI thread. In this case, it is used from a DownLoadTourThread to
     * notify the activity when progress is made with downloading tour media, and when the download
     * is finished.
     * <p/>
     * The use of static and a WeakReference are for preventing memory leaks (see
     * <a href="https://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ">
     * here</a>.
     */
    private static class ProgressHandler extends Handler {
        private final WeakReference<SummaryActivity> activity;

        public ProgressHandler(SummaryActivity a) {
            super(Looper.getMainLooper());
            activity = new WeakReference<>(a);
        }

        @Override
        public void handleMessage(final Message msg) {
            final SummaryActivity dActivity = activity.get();

            if (dActivity != null) {
                switch (msg.getData().getInt(DownloadTourTask.STATE)) {
                    case DownloadTourTask.STATE_DOWNLOADING:
                        dActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dActivity.updateProgress(
                                        msg.getData().getFloat(DownloadTourTask.PROGRESS)
                                );
                            }
                        });
                        break;
                    case DownloadTourTask.STATE_FINISHED:
                        dActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dActivity.onDownloadFinished();
                            }
                        });
                        break;
                }
            }
        }
    }

    private void onTourDownloadFailedActions() {

        CharSequence text = "Error downloading tour";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(this, text, duration);
        toast.show();

        this.finish();
    }

    /**
     * Creates an entry in the local db for a newly downloaded tour, or update it if there was an
     * update available.
     */
    private void addTourToDB() {
        TourDBManager dbHelper = TourDBManager.getInstance(getApplicationContext());

        String name = toolbar.getTitle().toString();
        int version = 0;

        try {
            name = tourJSON.getString("title");
            version = tourJSON.getInt("version");
        } catch (JSONException je) {
            je.printStackTrace();
        }

        if (updating) {
            // put updated values in the db
            dbHelper.updateRow(keyID, name, withMedia, version);

            // unflag this tour as having an update available
            dbHelper.flagTourUpdate(keyID, false);
        } else {
            // make a new entry for this tour
            dbHelper.putRow(keyID, tourID, keyName, name, expiryTimeString, withMedia, version);
        }

    }

    /**
     * Once download is finished adds tour to database and returns to StartActivity.
     */
    private void onDownloadFinished() {
        addTourToDB();
        this.finish();
    }

    /**
     * Displays the summary of downloaded tour. This includes its description, current version, if
     * an update is available, and the expiry date.
     */
    private void displaySummary() {
        downloadLayout.setVisibility(View.GONE);
        tourCard.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);

        displayTourInfo();
        displayVersionAndUpdate();
        displayExpiry();

        updateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    updating = true;
                    showDownloadDialog();
            }
        });
    }

    private void updateProgress(float progress) {
        progress = progress * 100;

        downloadButton.setText(String.format("%s%.1f%%",
                        getString(R.string.download_activity_label),
                        progress)
        );
    }


    /**
     * Displays the download dialog allowing for the tour to be updated if an update is available,
     * restarts the Activity upon selecting an option and initiates download
     */
    private void showDownloadDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.dialog_download, null);

        Button noMedia = (Button) view.findViewById(R.id.download_without_media);
        noMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, SummaryActivity.class);
                intent.putExtra(SummaryActivity.KEY_ID, keyID);
                intent.putExtra(SummaryActivity.TOUR_ID, tourID);

                intent.putExtra(SummaryActivity.DOWNLOAD, true);
                intent.putExtra(SummaryActivity.MEDIA, false);
                intent.putExtra(SummaryActivity.UPDATING, updating);
                startActivity(intent);
            }
        });

        Button media = (Button) view.findViewById(R.id.download_with_media);
        media.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SummaryActivity.this, SummaryActivity.class);
                intent.putExtra(SummaryActivity.KEY_ID, keyID);
                intent.putExtra(SummaryActivity.TOUR_ID, tourID);

                intent.putExtra(SummaryActivity.DOWNLOAD, true);
                intent.putExtra(SummaryActivity.MEDIA, true);
                intent.putExtra(SummaryActivity.UPDATING, updating);
                startActivity(intent);
            }
        });

        builder.setView(view);
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
     * Parses the currently set date (in Long) to an appropriate X Days - X Hours - X Minutes format
     */
    private void parseDate() {
        long expiryDate = TourDBManager.getInstance(getApplicationContext()).getExpiryDate(keyID);
        long remaining = expiryDate - Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTimeInMillis();

        durationMinutes = (int) (remaining / (60 * 1000) % 60);
        durationHours = (int) (remaining / (60 * 60 * 1000) % 24);
        durationDays = (int) (remaining / (24 * 60 * 60 * 1000));
    }

}