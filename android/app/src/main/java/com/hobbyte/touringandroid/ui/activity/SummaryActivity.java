package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.io.DownloadTourTask;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Set;

public class SummaryActivity extends AppCompatActivity {

    public static final String KEY_ID = "keyID";
    public static final String TOUR_ID = "tourID";
    public static final String DOWNLOAD = "download";
    public static final String MEDIA = "media";
    private static final String TAG = "SummaryActivity";

    private static ProgressHandler handler;

    private LinearLayout updateTour;
    private ImageButton updateButton;
    private TextView updateText;

    private String keyID;
    private String tourID;
    private Boolean withMedia;
    private Boolean doDownload;

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

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);
        tourID = intent.getStringExtra(TOUR_ID);
        doDownload = intent.getBooleanExtra(DOWNLOAD, false);
        withMedia = intent.getBooleanExtra(MEDIA, false);

        Log.d(TAG, String.format("k: %s t: %s", keyID, tourID));
        downloadLayout = (RelativeLayout) findViewById(R.id.downloadLayout);
        tourCard = (RelativeLayout) findViewById(R.id.tourCard);
        buttonLayout = (RelativeLayout) findViewById(R.id.buttonLayout);
        downloadButton = (Button) findViewById(R.id.downloadButton);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtDescription = (TextView) findViewById(R.id.txtTourDescription);



        if(doDownload) {
            findViewById(R.id.downloadLayout).setVisibility(View.VISIBLE);
            executeDownload();
        } else {
            displaySummary();
        }

        handler = new ProgressHandler(this);

    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(getApplicationContext()).close();

        super.onPause();
    }


    public void openTourActivity() {
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.INTENT_KEY_ID, keyID);
        startActivity(intent);
        this.finish();
    }

    private void displayTourInfo() {
        TextView timeTourTakes = (TextView) findViewById(R.id.txtEstimatedTime);

        try {
            tourJSON = FileManager.getJSON(getApplicationContext(), keyID, FileManager.TOUR_JSON);
            toolbar.setTitle(tourJSON.getString("title"));
            txtDescription.setText(tourJSON.getString("description"));
            timeTourTakes.setText("Estimated time: 1 hour 30 minutes");
        } catch (JSONException e) {
            e.printStackTrace();
            toolbar.setTitle("Error");
            txtDescription.setText("Error");
        }
    }

    private void displayVersionAndUpdate() {

        updateButton = (ImageButton) findViewById(R.id.updateTourButton);
        updateText = (TextView) findViewById(R.id.updateTourText);
        updateTour = (LinearLayout) findViewById(R.id.updateTour);

        Context context = getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                context.MODE_PRIVATE);

        Set<String> updateSet = prefs.getStringSet(context.getString(R.string.prefs_tours_to_update), null);

        if (updateSet != null) {
            for (String s : updateSet) {
                if (s.equals(keyID)) {
                    displayUpdateOption();
                    break;
                }
            }
        } else {
            updateButton.setImageResource(R.mipmap.ic_check_black_24dp);
            updateButton.setColorFilter(Color.parseColor("#00ff0f"));
            updateTour.setTag("Updated");
            updateText.setVisibility(View.GONE);
        }
    }

    private void displayUpdateOption() {

        TextView version = (TextView) findViewById(R.id.txtVersion);
        version.setText(getApplicationContext().getString(
                R.string.summary_activity_new_version_is_available));

        updateText.setVisibility(View.VISIBLE);
        updateText.setTextColor(getResources().getColor(R.color.colorPrimaryLight));
        updateButton.setImageResource(R.mipmap.ic_get_app_black_24dp);
        updateButton.setColorFilter(getResources().getColor(R.color.colorPrimaryLight));
        updateTour.setTag("ToUpdate");
    }

    private void displayExpiry() {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        String expiryText = prefs.getString(App.context.getString(R.string.prefs_current_expiry), null);
        TextView txtExpiry = (TextView) findViewById(R.id.txtExpiry);
        txtExpiry.setText("Expires on: " + expiryText);
        //TODO implement this
    }

    public void executeDownload() {
        new FetchTourJSON().execute();
    }

    /**
     * Downloads the JSON for a tour object on the server. It is used to get the tour title and
     * description in this activity, plus in TourActivity.
     */
    private class FetchTourJSON extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            tourJSON = ServerAPI.getJSON(tourID, ServerAPI.TOUR);
            System.out.println("Is it saving JSON?");
            FileManager.saveJSON(getApplicationContext(), tourJSON, keyID, FileManager.TOUR_JSON);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            displayTourInfo();
            DownloadTourTask dThread = new DownloadTourTask(handler, keyID, tourID, withMedia);
            dThread.start();
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
            activity = new WeakReference<SummaryActivity>(a);
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

    /**
     * Creates a enw entry in the local db for a newly downloaded tour.
     */
    private void addTourToDB() {
        TourDBManager dbHelper = TourDBManager.getInstance(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        String expiresAt = prefs.getString(getString(R.string.prefs_current_expiry), null);

        String name = "empty";
        String createdAt = "";
        String updatedAt = "";

        try {
            name = tourJSON.getString("title");
            createdAt = tourJSON.getString("createdAt");
            updatedAt = tourJSON.getString("updatedAt");
        } catch (JSONException je) {
            je.printStackTrace();
        }

        dbHelper.putRow(
                keyID, tourID,
                name, createdAt, updatedAt,
                expiresAt, withMedia
        );
    }

    private void onDownloadFinished() {
        addTourToDB();
        displaySummary();
    }

    private void displaySummary() {
        downloadLayout.setVisibility(View.GONE);
        tourCard.setVisibility(View.VISIBLE);
        buttonLayout.setVisibility(View.VISIBLE);

        displayTourInfo();
        displayVersionAndUpdate();
        displayExpiry();

        (findViewById(R.id.buttonStartTour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTourActivity();
            }
        });

        (findViewById(R.id.updateTour)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getTag().equals("ToUpdate")) {
                    showDownloadDialog();
                } else if (v.getTag().equals("Updated")) {
                    //Do nothing
                }
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

}
