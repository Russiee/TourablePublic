package com.hobbyte.touringandroid.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.io.DeleteTourTask;
import com.hobbyte.touringandroid.io.DownloadTourTask;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Set;

public class SummaryActivity extends AppCompatActivity {

    private static final String TAG = "SummaryActivity";
    public static final String KEY_ID = "keyID";
    public static final String TOUR_ID = "tourID";

    private Button openButton;
    private Button updateButton;

    private ProgressBar progressBar;
    private TextView progressText;

    private String keyID;
    private String tourID;
    private String title;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        Intent intent = getIntent();
        keyID = intent.getStringExtra(KEY_ID);
        tourID = intent.getStringExtra(TOUR_ID);

        Log.d(TAG, String.format("k: %s t: %s", keyID, tourID));

        SharedPreferences prefs = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
        Set<String> toUpdate = prefs.getStringSet(getString(R.string.prefs_tours_to_update), null);

        if (true) {
//        if (toUpdate != null && toUpdate.contains(keyID)) {
            updateButton = (Button) findViewById(R.id.buttonUpdateTour);
            updateButton.setVisibility(View.VISIBLE);
        }

        handler = new ProgressHandler(this);

        loadTourDescription();
    }

    @Override
    protected void onPause() {
        // leaving a database instance open across activities is BAD!!
        TourDBManager.getInstance(this).close();

        super.onPause();
    }

    public void openTourActivity(View v) {
        Intent intent = new Intent(this, TourActivity.class);
        intent.putExtra(TourActivity.INTENT_KEY_ID, keyID);
        intent.putExtra(TourActivity.INTENT_TITLE, title);
        startActivity(intent);
    }

    private void loadTourDescription() {
        JSONObject tourJSON = FileManager.getJSON(getApplicationContext(), keyID, FileManager.TOUR_JSON);

        TextView txtTitle = (TextView) findViewById(R.id.txtTourTitle);
        TextView txtDescription = (TextView) findViewById(R.id.txtTourDescription);

        try {
            title = tourJSON.getString("title");
            txtTitle.setText(title);
            txtDescription.setText(tourJSON.getString("description"));
        } catch (Exception e) {
            e.printStackTrace();
            txtTitle.setText("Error");
            txtDescription.setText("Error");
        }
    }

    public void doTourUpdate(View v) {
        openButton = (Button) findViewById(R.id.buttonStartTour);
        openButton.setEnabled(false);
        updateButton.setEnabled(false);

        new UpdateTask().execute();
    }

    private void updateProgress(float progress) {
        progress = progress * 100;
        progressBar.setProgress((int) progress);
        progressText.setText(String.valueOf(Math.round(progress)) + "%");
//        Log.d(TAG, String.format("Progress: %.2f", progress));
    }

    private void onDownloadFinished() {
        // re-enable buttons. Hide update button?
        openButton.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(this, StartActivity.class);
            startActivity(intent);
    }

    private class UpdateTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // first remove all existing files
            DeleteTourTask deleteTask = new DeleteTourTask(getApplicationContext(), keyID);
            deleteTask.start();

            // need to wait until all files have been deleted before remaking them! Otherwise errors
            try {
                deleteTask.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // then redownload the tour JSON and remake the folders
            JSONObject tourJSON = ServerAPI.getJSON(tourID, ServerAPI.TOUR);
            FileManager.makeTourDirectories(getApplicationContext(), keyID);
            FileManager.saveJSON(getApplicationContext(), tourJSON, keyID, FileManager.TOUR_JSON);
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            // set up the progress bar
            progressBar = (ProgressBar) findViewById(R.id.updateProgressBar);
            progressText = (TextView) findViewById(R.id.bottomText);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setMax(100);

            // start the download task
            boolean hasVideo = TourDBManager.getInstance(getApplicationContext()).doesTourHaveVideo(keyID);
            DownloadTourTask task = new DownloadTourTask(handler, keyID, tourID, hasVideo);
            task.start();
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
        private final WeakReference<SummaryActivity> activity;

        public ProgressHandler(SummaryActivity a) {
            super(Looper.getMainLooper());
            activity = new WeakReference<SummaryActivity>(a);
        }

        @Override
        public void handleMessage(final Message msg) {
            final SummaryActivity sActivity = activity.get();

            if (sActivity != null) {
                switch (msg.getData().getInt(DownloadTourTask.STATE)) {
                    case DownloadTourTask.STATE_DOWNLOADING:
                        sActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sActivity.updateProgress(msg.getData().getFloat(DownloadTourTask.PROGRESS));
                            }
                        });
                        break;
                    case DownloadTourTask.STATE_FINISHED:
                        sActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sActivity.onDownloadFinished();
                            }
                        });
                        break;
                }
            }
        }
    }
}
