package com.hobbyte.touringandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Nikita on 15/02/2016.
 */
public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private Context context;

    public LoadImageFromURL(ImageView iv, Context context) {
        imageView = iv;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String urlDisplay = params[0];
        Bitmap bm = null;
        try {
            InputStream is = new java.net.URL(urlDisplay).openStream();
            bm = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bm;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int height = metrics.heightPixels / 2;
        int width = metrics.widthPixels;
        result = Bitmap.createScaledBitmap(result, height, width, true);
        imageView.setImageBitmap(result);
    }
}
