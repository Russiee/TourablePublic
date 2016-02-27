package com.hobbyte.touringandroid.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.SaveTourJSON;
import com.hobbyte.touringandroid.internet.ServerAPI;
import com.hobbyte.touringandroid.io.BundleSaver;
import com.hobbyte.touringandroid.io.FileManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DownloadActivity extends Activity {
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

    private JSONObject tourJSON;
    private String keyID;
    private String tourID;
    private String expiresAt;

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

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bottomTextView = (TextView) findViewById(R.id.bottomText);

        imageOptionCaptionEditText = (TextView) findViewById(R.id.imageOptionCaptionEditText);
        videoOptionCaptionEditText = (TextView) findViewById(R.id.videoOptionCaptionEditText);
        imageOptionSizeEditText = (TextView) findViewById(R.id.imageOptionSizeEditText);
        videoOptionSizeEditText = (TextView) findViewById(R.id.videoOptionSizeEditText);

        downloadImagesButton = (Button) findViewById(R.id.imageOptionButton);
        downloadVideoButton = (Button) findViewById(R.id.videoOptionButton);

        downloadImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTourMediaClass k = new DownloadTourMediaClass();
                k.execute(IMAGES);
                changeUiAfterSelection("");
            }
        });

        downloadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadTourMediaClass k = new DownloadTourMediaClass();
                k.execute(VIDEO);
                changeUiAfterSelection(" & video");
            }
        });

        setDownloadSizeLabels();
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
        bottomTextView.setText(getResources().getString(R.string.download_activity_label) + stuff + "...");
    }

    /**
     * Opens the tour activty witha  tour
     */
    protected void moveToTourActivity() {

        //TODO need to pass tour data in here
        Intent intent = new Intent(this, SummaryActivity.class);
        intent.putExtra(SummaryActivity.KEY_ID, keyID);
        startActivity(intent);
    }

    /**
     * Asynchronously downloads the media of the tour
     */
    private class DownloadTourMediaClass extends AsyncTask<Integer, Void, Boolean> {

        private HashSet<String> mediaURLs;
        private String imageOnlyPattern = "https?:\\/\\/[\\w\\d\\.\\/]*\\.(jpe?g|png)";
        private String allMediaPattern = "https?:\\/\\/[\\w\\d\\.\\/]*\\.(jpe?g|png|mp4)";

        @Override
        protected Boolean doInBackground(Integer... params) {
            String bundleString = ServerAPI.getBundleString(tourID);

            // use pattern matcher to extract all media URLs
            Pattern p = null;

            if (params[0] == IMAGES) {
                p = Pattern.compile(imageOnlyPattern);
            } else if (params[0] == VIDEO) {
                p = Pattern.compile(allMediaPattern);
            }

            Matcher matcher = p.matcher(bundleString);
            mediaURLs = new HashSet<String>();

            while (matcher.find()) {
                mediaURLs.add(matcher.group());
            }

            // on a separate thread, save the bundle and POI JSON
            FileManager.makeTourDirectories(getApplicationContext(), keyID);
            BundleSaver bundleSaver = new BundleSaver(getApplicationContext(), bundleString, keyID);
            bundleSaver.start();

            float total = (float) mediaURLs.size();
            float count = 0.0f;

            // save media to device
            for (Iterator<String> i = mediaURLs.iterator(); i.hasNext(); ) {
                FileManager.saveImage(getApplicationContext(), keyID, i.next());
                Log.d(TAG, String.format("Loading %.2f%% complete", (++count / total)));
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {

            Log.i(TAG, "finished downloading");

            if (isValid) {
                moveToTourActivity(); //TODO uncomment this when it actually starts a tour
            }
            // removes activity from users stack so when they press back from a tour they go back
            // to the main menu
            //DownloadActivity.this.finish();
        }
    }

    private class FetchTourJSON extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            tourJSON = ServerAPI.getJSON(tourID, ServerAPI.TOUR);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            loadTourDescription();
        }
    }
}