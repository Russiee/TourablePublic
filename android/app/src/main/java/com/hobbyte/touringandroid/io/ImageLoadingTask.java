package com.hobbyte.touringandroid.io;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;

import com.hobbyte.touringandroid.App;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * ASynchronously loads a saved image file into a {@link Bitmap}, which in turn gets placed in an
 * {@link ImageView}. The file is sampled to save memory.
 * <p>
 * Largely inspired by the Android <a href="https://developer.android.com/training/displaying-bitmaps/load-bitmap.html">
 * training guides</a>.
 */
public class ImageLoadingTask extends AsyncTask<String, Void, Bitmap> {
    private static final String TAG = "ImageLoadingTask";

    private final WeakReference<ImageView> weakImageView;

    private int width;
    private int height;

    public ImageLoadingTask(ImageView imageView) {
        weakImageView = new WeakReference<>(imageView);

        DisplayMetrics metrics = App.context.getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        height = metrics.heightPixels / 2;
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String imgPath = params[0];
        String keyID = params[1];
        File file = new File(App.context.getFilesDir(), String.format("%s/image/%s", keyID, imgPath));

        if (file.exists()) {
            return getSampledBitmap(file);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (weakImageView != null && bitmap != null) {
            final ImageView imageView = weakImageView.get();

            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap getSampledBitmap(File file) {
        // first decode the image to get the raw pixel dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // then calculate the factor by which we can sub-sample the actual image, and load the
        // sub-sampled bitmap
        options.inSampleSize = calculateSampleSize(options);
        options.inJustDecodeBounds = false;

        Bitmap sampledBM = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        int scaleHeight = (sampledBM.getHeight() > height) ? height : sampledBM.getHeight();

        // sampling the file isn't creating the dimensions we want, so have to scale it as well
        return Bitmap.createScaledBitmap(
                BitmapFactory.decodeFile(file.getAbsolutePath(), options), width, scaleHeight, true);

        // if we don't want to mess with the image aspect ratio, use this
//        return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
    }

    private int calculateSampleSize(BitmapFactory.Options options) {
        final int imgHeight = options.outHeight;
        final int imgWidth = options.outWidth;
        int sampleSize = 1;

        if (imgHeight > height || imgWidth > width) {
            while ((imgHeight / sampleSize > height) && (imgWidth / sampleSize > width)) {
                sampleSize *= 2;
            }
        }

        return sampleSize;
    }
}
