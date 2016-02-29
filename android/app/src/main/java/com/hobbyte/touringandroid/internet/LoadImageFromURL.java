package com.hobbyte.touringandroid.internet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;

/**
 * @author Nikita
 */
public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "LoadImageFromURL";

    private ImageView imageView;
    private Context context;

    public LoadImageFromURL(ImageView iv, Context context) {
        imageView = iv;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imgPath = params[0];
        String keyID = params[1];
        Bitmap bm = null;
        File file = new File(context.getApplicationContext().getFilesDir(), String.format("%s/image/%s", keyID, imgPath));

        Log.d(TAG, "Looking for " + imgPath);

        if(file.exists()) {
            bm = BitmapFactory.decodeFile(file.getAbsolutePath());
            Log.d(TAG, "DOES EXIST!");
        }
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        DisplayMetrics metrics = context.getApplicationContext().getResources().getDisplayMetrics();
        int height = metrics.heightPixels / 2;
        int width = metrics.widthPixels;
        result = Bitmap.createScaledBitmap(result, width, height, true);
        imageView.setImageBitmap(result);
    }
}
