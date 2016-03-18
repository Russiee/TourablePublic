package com.hobbyte.touringandroid.internet;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.TourDBManager;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * Queries the server to see if there are any tours which need updating.
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
        Object[][] keys = TourDBManager.getInstance(context).getTourUpdateInfo();

        for (Object[] row : keys) {
            String keyID = (String) row[0];
            String tourID = (String) row[1];

            JSONObject tour = ServerAPI.getJSON(tourID, ServerAPI.TOUR);

            try {
                // TODO: change this when tour version numbers are implemented
                String version = tour.getString("updatedAt");

                if (TourDBManager.convertStampToMillis(version)[0] > (long) row[2]) {
                    toUpdate.add(keyID);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (toUpdate.size() > 0) {
            SharedPreferences prefs = context.getSharedPreferences(
                    context.getString(R.string.preference_file_key),
                    Context.MODE_PRIVATE
            );

            SharedPreferences.Editor editor = prefs.edit();
            editor.putStringSet(context.getString(R.string.prefs_tours_to_update), toUpdate);
            editor.apply();
        }

        Log.d(TAG, String.format("There are %d tours to update", toUpdate.size()));
    }
}
