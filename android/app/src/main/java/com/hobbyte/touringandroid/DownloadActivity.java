package com.hobbyte.touringandroid;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DownloadActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        setDownloadSizeLabels();

        Button downloadImagesButton = (Button) findViewById(R.id.imageOptionButton);
        Button downloadVideoButton = (Button) findViewById(R.id.videoOptionButton);

        downloadImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO api call for images here
            }
        });

        downloadVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO api call for video here
            }
        });
    }

    private void setDownloadSizeLabels() {

        TextView imageDownloadSizeLabel = (TextView) findViewById(R.id.imageDownloadSizeLabel);
        TextView videoDownloadSizeLabel = (TextView) findViewById(R.id.videoDownloadSizeLabel);
    }

}
