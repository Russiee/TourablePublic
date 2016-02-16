package com.hobbyte.touringandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends Activity {

    private ProgressBar progressBar;
    private TextView bottomTextView;
    private TextView downloadImageLabel;
    private TextView downloadVideoLabel;
    private Button downloadImagesButton;
    private Button downloadVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bottomTextView = (TextView) findViewById(R.id.bottomText);

        downloadImageLabel = (TextView) findViewById(R.id.imageDownloadSizeLabel);
        downloadVideoLabel = (TextView) findViewById(R.id.videoDownloadSizeLabel);

        downloadImagesButton = (Button) findViewById(R.id.imageOptionButton);
        downloadVideoButton = (Button) findViewById(R.id.videoOptionButton);

        downloadImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = "images";
                downloadTourMediaClass k = new downloadTourMediaClass();
                k.execute(param);
                changeUiAfterSelection("");
            }
        });
        downloadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String param = "video";
                downloadTourMediaClass k = new downloadTourMediaClass();
                k.execute(param);
                changeUiAfterSelection(" & video");
            }
        });

        setDownloadSizeLabels();
    }

    /**
     * Sets the labels underneath each option to represent the amount of data they will use
     * Also deals with the situation when there is not enough storage space for the download
     */
    private void setDownloadSizeLabels() {

        long videoDownloadSizeBytes = 9223372036854775807L; //TODO do server stuff instead
        long imageDownloadSizeBytes = 1024; //TODO do server stuff instead
        long freeSpaceInBytes = getFreeStorageSpaceInBytes();

        if (freeSpaceInBytes < imageDownloadSizeBytes) {

            downloadImagesButton.setEnabled(false);
            downloadImageLabel.setTextColor(ColorStateList.valueOf(Color.RED));
            downloadImageLabel.setText(
                    getResources().getString(R.string.download_activity_not_enough_space));
        }
        if (freeSpaceInBytes < videoDownloadSizeBytes) {

            downloadVideoButton.setEnabled(false);
            downloadVideoLabel.setTextColor(ColorStateList.valueOf(Color.RED));
            downloadVideoLabel.setText(
                    getResources().getString(R.string.download_activity_not_enough_space));
        }

        TextView imageDownloadSizeLabel = (TextView) findViewById(R.id.imageDownloadSizeLabel);
        TextView videoDownloadSizeLabel = (TextView) findViewById(R.id.videoDownloadSizeLabel);

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
     * Asynchronously downloads the media of the tour
     */
    private class downloadTourMediaClass extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {

            //when we want to load images only
            if (params.equals("images")) {

                //TODO make this work

            //when we want to load images and video
            } else if (params.equals("video")) {

                //TODO make this work
            } else return false;

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isValid) {

            //moveToTourActivity(); //TODO uncomment this when it actually starts a tour

            //removes activity from stack users stack so when they press back from a tour tehy go back to the main menu
            DownloadActivity.this.finish();
        }
    }

    /**
     * Opens the tour activty witha  tour
     */
    protected void moveToTourActivity() {

        //TODO need to pass tour data in here
        Intent intent = new Intent(this, TourActivity.class);
        startActivity(intent);
    }

}
