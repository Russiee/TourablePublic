package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.UpdateChecker;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private boolean updateFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView logo = (TextView) findViewById(R.id.header_text);
        Typeface font = Typeface.createFromAsset(getAssets(),  "fonts/Pacifico.ttf");
        logo.setTypeface(font);

        new InitialisationTask().execute();
    }

    /**
     * Leaves the splash screen with a quick crossfade into StartActivity.
     */
    private void goToStartActivity() {
        Intent intent = new Intent(this, StartActivity.class);
        SplashActivity.this.startActivity(intent);
        SplashActivity.this.finish();

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    /**
     * Checks if the activity has finished updating/deleting tours. Used in testing.
     */
    public boolean isFinished() {
        return updateFinished;
    }

    /**
     * First check to see if any tours have updates, then fetch all tours that have expired and
     * delete them.
     */
    private class InitialisationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            long appStartTime = System.currentTimeMillis();

            // this checks for changes to expiry date and version number
            UpdateChecker checker = new UpdateChecker(App.context);

            try {
                checker.start();
                checker.join();

                Log.d(TAG, String.format("Checking for updates took: %d millis", System.currentTimeMillis() - appStartTime));

                // makes it last at least one second, even if update takes less.
                long timeToSleepFor = 1000 - (System.currentTimeMillis() - appStartTime);

                if (timeToSleepFor > 0) {
                    Thread.sleep(timeToSleepFor);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            String[] expired = TourDBManager.getInstance(App.context).getExpiredTours();

            for (String keyID : expired) {
                FileManager.removeTour(App.context, keyID);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            TourDBManager.getInstance(App.context).close();
            updateFinished = true;
            goToStartActivity();
        }
    }
}
