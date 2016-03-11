package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.io.DownloadTourTask;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class DownloadActivity extends AppCompatActivity {
    private static final String TAG = "DownloadActivity";

    private static int IMAGES = 0;
    private static int VIDEO = 1;

    private ProgressBar progressBar;
    private TextView bottomTextView;

    private TextView imageOptionCaptionEditText;
    private TextView videoOptionCaptionEditText;
    private TextView imageOptionSizeEditText;
    private TextView videoOptionSizeEditText;

    private Button downloadImagesButton;
    private Button downloadVideoButton;

    private static ProgressHandler handler;

    private JSONObject tourJSON;
    private String keyID;
    private String tourID;
    private String expiresAt;
    private boolean hasVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );

        keyID = prefs.getString(getString(R.string.prefs_current_key), null);
        tourID = prefs.getString(getString(R.string.prefs_current_tour), null);
        expiresAt = prefs.getString(getString(R.string.prefs_current_expiry), null);

        new FetchTourJSON().execute();

        handler = new ProgressHandler(this);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
//        bottomTextView = (TextView) findViewById(R.id.bottomText);

        imageOptionCaptionEditText = (TextView) findViewById(R.id.imageOptionCaptionEditText);
        videoOptionCaptionEditText = (TextView) findViewById(R.id.videoOptionCaptionEditText);
        imageOptionSizeEditText = (TextView) findViewById(R.id.imageOptionSizeEditText);
        videoOptionSizeEditText = (TextView) findViewById(R.id.videoOptionSizeEditText);

        downloadImagesButton = (Button) findViewById(R.id.imageOptionButton);
        downloadVideoButton = (Button) findViewById(R.id.videoOptionButton);

        downloadImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTourTask dThread = new DownloadTourTask(handler, keyID, tourID, false);
                dThread.start();

                progressBar.setVisibility(View.VISIBLE);

                hasVideo = false;
                changeUiAfterSelection("");
            }
        });

        downloadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTourTask dThread = new DownloadTourTask(handler, keyID, tourID, true);
                dThread.start();

                hasVideo = true;
                changeUiAfterSelection(" & video");
            }
        });

        setDownloadSizeLabels();
    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(getApplicationContext()).close();

        super.onPause();
    }

    private void loadTourDescription() {
        TextView txtTitle = (TextView) findViewById(R.id.txtTourTitle);
        TextView txtDescription = (TextView) findViewById(R.id.txtTourDescription);

        try {
            txtTitle.setText(tourJSON.getString("title"));
            txtDescription.setText(tourJSON.getString("description"));
        } catch (JSONException e) {
            e.printStackTrace();
            txtTitle.setText("Error");
            txtDescription.setText("Error");
        }
    }

    /**
     * Sets the labels underneath each option to represent the amount of data they will use
     * Also deals with the situation when there is not enough storage space for the download
     */
    private void setDownloadSizeLabels() {

        long videoDownloadSizeBytes = 4234234; //TODO do server stuff instead
        long imageDownloadSizeBytes = 1024; //TODO do server stuff instead
        long freeSpaceInBytes = getFreeStorageSpaceInBytes();

        if (freeSpaceInBytes < imageDownloadSizeBytes) {

            downloadImagesButton.setEnabled(false);
            imageOptionCaptionEditText.setTextColor(ColorStateList.valueOf(Color.RED));
            imageOptionCaptionEditText.setText(getResources().getString(R.string.download_activity_not_enough_space));
            imageOptionSizeEditText.setTextColor(ColorStateList.valueOf(Color.RED));
        }
        if (freeSpaceInBytes < videoDownloadSizeBytes) {

            downloadVideoButton.setEnabled(false);
            videoOptionSizeEditText.setTextColor(ColorStateList.valueOf(Color.RED));
            videoOptionCaptionEditText.setTextColor(ColorStateList.valueOf(Color.RED));
            videoOptionCaptionEditText.setText(getResources().getString(R.string.download_activity_not_enough_space));
        }

        TextView imageDownloadSizeLabel = (TextView) findViewById(R.id.imageOptionSizeEditText);
        TextView videoDownloadSizeLabel = (TextView) findViewById(R.id.videoOptionSizeEditText);

        imageDownloadSizeLabel.setText(
                android.text.format.Formatter.formatFileSize(this, imageDownloadSizeBytes));
        videoDownloadSizeLabel.setText(
                android.text.format.Formatter.formatFileSize(this, videoDownloadSizeBytes));
    }

    /**
     * Get the amount of free space in the users external storage
     *
     * @return amount of free space, in bytes
     */
    private long getFreeStorageSpaceInBytes() {

        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        return statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
    }

    /**
     * Changes the ui when a button is clicked so no more ui input can take place
     *
     * @param stuff what we are downloading
     */
    private void changeUiAfterSelection(String stuff) {
        downloadImagesButton.setEnabled(false);
        downloadVideoButton.setEnabled(false);

        progressBar.setVisibility(View.VISIBLE);
//        bottomTextView.setText(getResources().getString(R.string.download_activity_label));
    }

    private void updateProgress(float progress) {
        progress = progress * 100;

        progressBar.setProgress((int) progress);

        /*bottomTextView.setText(String.format("%s%.1f%%",
                        getString(R.string.download_activity_label),
                        progress)
        );*/
    }

    private void onDownloadFinished() {
        addTourToDB();
        moveToTourActivity();
    }

    /**
     * Opens the tour activty witha  tour
     */
    protected void moveToTourActivity() {
        String title;
        try {
            title = tourJSON.getString("title");
        } catch (JSONException e) {
            e.printStackTrace();
            title = "Tour";
        }

        // clear shared prefs to avoid complications elsewhere
        SharedPreferences prefs = getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );

        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(getString(R.string.prefs_current_key));
        editor.remove(getString(R.string.prefs_current_tour));
        editor.remove(getString(R.string.prefs_current_expiry));
        editor.apply();

        // move to SummaryActivity
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.KEY_ID, keyID);
        intent.putExtra(SummaryActivity.TOUR_ID, tourID);
        startActivity(intent);
        this.finish();
    }

    /**
     * Creates a enw entry in the local db for a newly downloaded tour.
     */
    private void addTourToDB() {
        TourDBManager dbHelper = TourDBManager.getInstance(getApplicationContext());

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
                expiresAt, hasVideo
        );
    }

    /**
     * Downloads the JSON for a tour object on the server. It is used to get the tour title and
     * description in this activity, plus in TourActivity.
     */
    private class FetchTourJSON extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            tourJSON = ServerAPI.getJSON(tourID, ServerAPI.TOUR);
            FileManager.makeTourDirectories(getApplicationContext(), keyID);
            FileManager.saveJSON(getApplicationContext(), tourJSON, keyID, FileManager.TOUR_JSON);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadTourDescription();
        }
    }

    /**
     * Handles messages from a non-UI thread. In this case, it is used from a DownLoadTourThread to
     * notify the activity when progress is made with downloading tour media, and when the download
     * is finished.
     * <p>
     * The use of static and a WeakReference are for preventing memory leaks (see
     * <a href="https://groups.google.com/forum/#!msg/android-developers/1aPZXZG6kWk/lIYDavGYn5UJ">
     * here</a>.
     */
    private static class ProgressHandler extends Handler {
        private final WeakReference<DownloadActivity> activity;

        public ProgressHandler(DownloadActivity a) {
            super(Looper.getMainLooper());
            activity = new WeakReference<DownloadActivity>(a);
        }

        @Override
        public void handleMessage(final Message msg) {
            final DownloadActivity dActivity = activity.get();

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
}
