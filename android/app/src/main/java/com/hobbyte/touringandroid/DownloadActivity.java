package com.hobbyte.touringandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadActivity extends Activity {

    private ProgressBar progressBar;
    private TextView bottomTextView;
    private Button downloadImagesButton;
    private Button downloadVideoButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        setDownloadSizeLabels();

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        bottomTextView = (TextView) findViewById(R.id.bottomText);

        downloadImagesButton = (Button) findViewById(R.id.imageOptionButton);
        downloadVideoButton = (Button) findViewById(R.id.videoOptionButton);
        downloadImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO api call for images here
                animateStuff("");
            }
        });
        downloadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO api call for video here
                animateStuff(" & video");
            }
        });
    }

    private void setDownloadSizeLabels() {

        TextView imageDownloadSizeLabel = (TextView) findViewById(R.id.imageDownloadSizeLabel);
        TextView videoDownloadSizeLabel = (TextView) findViewById(R.id.videoDownloadSizeLabel);

        imageDownloadSizeLabel.setText("100GB");
        videoDownloadSizeLabel.setText("10Kb");
    }

    private void animateStuff(String stuff) {
        downloadImagesButton.setEnabled(false);
        downloadVideoButton.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        bottomTextView.setText("Downloading images" + stuff + "...");
    }

}
