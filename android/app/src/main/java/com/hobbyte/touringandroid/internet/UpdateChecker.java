package com.hobbyte.touringandroid.internet;

import android.content.Context;
import android.util.Log;

import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Queries the server to see if there are any tours which need updating. This can mean a change in
 * version number, which means a change in tour content, and/or a change in key expiry date.
 */
public class UpdateChecker extends Thread {
    private static final String TAG = "UpdateChecker";

    private Context context;

    public UpdateChecker(Context context) {
        this.context = context;
    }

    @Override
    public void run() {

        //if no internet, don't run
        if (!ServerAPI.checkConnection()) return;

        int updateCount = 0;
        TourDBManager dbHelper = TourDBManager.getInstance(context);

        // has keyID, tourID, version, and expiry date
        Object[][] keys = dbHelper.getTourUpdateInfo();

        for (Object[] row : keys) {
            String keyID = (String) row[0];
            String tourID = (String) row[1];


            // first check if version number has changed
            JSONObject tour = ServerAPI.getJSON(tourID, ServerAPI.TOUR);

            if (tour == null) continue;

            try {
                int version = tour.getInt("version");

                // if the version number has changed, flag it so that the update is picked
                // up in SummaryActivity
                if (version > (int) row[2]) {
                    dbHelper.flagTourUpdate(keyID, true);
                    ++updateCount;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // then check if expiry date has changed
            JSONObject key = ServerAPI.getJSON(keyID, ServerAPI.KEY);

            if (key == null) continue;

            try {
                long currentExpiry = (long) row[3];
                long newExpiry = TourDBManager.convertStampToMillis(key.getString("expiry"));

                if (newExpiry != currentExpiry) {
                    dbHelper.updateTourExpiry(keyID, newExpiry);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.d(TAG, String.format("There are %d tours to update", updateCount));
    }
}
