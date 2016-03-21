package com.hobbyte.touringandroid.internet;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Queries the server to see if there are any tours which need updating. This can mean a change in
 * version number, which means a change in tour content, and/or a change in key expiry date.
 */
public class UpdateChecker extends Thread {
    private static final String TAG = "UpdateChecker";

    private Context context;
    private Set<String> toUpdate = new HashSet<>();

    public UpdateChecker(Context context) {
        this.context = context;
    }

    @Override
    public void run() {
        TourDBManager dbHelper = TourDBManager.getInstance(context);

        // has keyID, tourID, version, and expiry date
        Object[][] keys = dbHelper.getTourUpdateInfo();

        for (Object[] row : keys) {
            String keyID = (String) row[0];
            String tourID = (String) row[1];


            // first check if version number has changed
            JSONObject tour = ServerAPI.getJSON(tourID, ServerAPI.TOUR);

            try {
                int version = tour.getInt("version");

                if (version > (int) row[2]) {
                    toUpdate.add(keyID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            // then check if expiry date has changed
            JSONObject key = ServerAPI.getJSON(keyID, ServerAPI.KEY);

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

        // flag tours as having an update in the db. This will be picked up in SummaryActivity
        if (toUpdate.size() > 0) {
            for (String key : toUpdate) {
                dbHelper.flagTourUpdate(key, true);
            }
        }

        Log.d(TAG, String.format("There are %d tours to update", toUpdate.size()));
    }
}
