package com.hobbyte.touringandroid.ui.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.UpdateChecker;
import com.hobbyte.touringandroid.io.FileManager;
import com.hobbyte.touringandroid.io.TourDBManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

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
     * First check to see if any tours have updates, then fetch all tours that have expired and
     * delete them.
     */
    private class InitialisationTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // TODO this doesn't check if key expiry dates have been updated
            UpdateChecker checker = new UpdateChecker(App.context);

            try {
                checker.join();
                // add a bit more time so if this process is really quick, it won't be
                // an "immediate" change to the next activity, which feels a bit jarring
                Thread.sleep(1000);
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
            goToStartActivity();
        }
    }
}
