package com.hobbyte.touringandroid.ui.adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.io.VideoCacheListener;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.ui.fragment.POIFragment;
import com.hobbyte.touringandroid.ui.fragment.VideoFragment;
import com.hobbyte.touringandroid.ui.util.ImageCache;
import com.hobbyte.touringandroid.ui.util.Quiz;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link ArrayAdapter} that parses a POI JSON file to create the elements contained therein. A
 * POI can have any combination of the following item types:
 * <ul>
 * <li>Header text</li><li>Body text</li>
 * <li>An image with description</li><li>A video with description</li>
 * </ul>
 * <p/>
 * Much of the code relating to image loading and caching was inspired by the Android
 * <a href="https://developer.android.com/training/displaying-bitmaps/index.html">
 * training guides</a>.
 */
public class PoiContentAdapter extends ArrayAdapter<ListViewItem> {
    public static final int HEADER = 0;
    public static final int BODY = 1;
    public static final int IMAGE = 2;
    public static final int VIDEO = 3;
    public static final int QUIZ = 4;

    private static final String TAG = "PoiContentAdapter";
    private static final String FILE_NAME_PATTERN = "https?:\\/\\/[-\\w\\.\\/]*\\/(.+\\.(jpe?g|png|mp4))";

    private static Pattern namePattern;
    POIFragment fragment;
    private ListViewItem[] items;
    private Bitmap loadingBitmap;
    private String keyID;
    // needed to prevent ListView recycling form making many duplicate quizzes
    private Quiz quiz;

    public PoiContentAdapter(Context context, ListViewItem[] content, String keyID, POIFragment fragment) {
        super(context, 0, content);
        this.keyID = keyID;
        items = content;
        namePattern = Pattern.compile(FILE_NAME_PATTERN);
        this.fragment = fragment;

        // load a low-resolution placeholder that will initially be used in place of actual images
        // and will be replaced as soon as a requested image has loaded
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        loadingBitmap = BitmapFactory.decodeResource(App.context.getResources(), R.drawable.poi_image_placeholder);
    }

    /**
     * Checks whether there is already an ImageLoadingTask associated with this ImageView. If there
     * is, then there's no need to start loading the image again.
     */
    private static boolean taskNotAlreadyRunning(ImageView imageView) {
        final ImageLoadingTask task = getImageLoadingTask(imageView);

        if (task != null) {
            Log.d(TAG, "already loading image");
        }
        return task == null;
    }

    /**
     * Gets the ImageLoadingTask associated with the provided ImageView.
     *
     * @return null if the ImageView is null or it does not have an ASyncDrawable
     */
    private static ImageLoadingTask getImageLoadingTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();

            if (drawable instanceof ASyncDrawable) {
                final ASyncDrawable aSyncDrawable = (ASyncDrawable) imageView.getDrawable();
                return aSyncDrawable.getImageLoadingTask();
            }
        }

        return null;
    }

    /**
     * Returns the number of different POI element types. (Needed or else things break).
     */
    @Override
    public int getViewTypeCount() {
        return 5;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
    }

    /**
     * Inflates a certain view depending on the type of ListViewItem (header, body, image, or video).
     *
     * @param position Position of item in the ItemList
     * @param view     View
     * @param parent   ParentView
     * @return the inflated View
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final ListViewItem listViewItem = items[position];
        int listViewItemType = getItemViewType(position);
        String filename = null;

        TextView contentView;

        if (listViewItem.getUrl() != null) {
            Matcher m = namePattern.matcher(listViewItem.getUrl());
            if (m.matches()) {
                filename = m.group(1);
            }
        }

        if (view == null) {
            if (listViewItemType == IMAGE) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_image, parent, false);
            } else if (listViewItemType == VIDEO) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_video, parent, false);
            } else if (listViewItemType == HEADER) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_header, parent, false);
            } else if (listViewItemType == QUIZ) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_quiz, parent, false);
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
            }
        }

        switch (listViewItemType) {

            case IMAGE:

                ImageView imageView = (ImageView) view.findViewById(R.id.poiContentImageView);
                TextView textView = (TextView) view.findViewById(R.id.poiContentImageDesc);
                textView.setText(listViewItem.getText());

                if (filename != null && taskNotAlreadyRunning(imageView)) {
                    loadImageFromDiskOrCache(filename, imageView);
                }

                return view;

            case VIDEO:
                //generate default references for thumbnail and file path
                ImageView thumbnail = (ImageView) view.findViewById(R.id.imagePlayIcon);
                String savedVideoFilePath = getContext().getFilesDir() + "/" + String.format("%s/video/%s", keyID, filename);

                File file = new File(savedVideoFilePath);

                if (file.exists()) {
                    //if file exists then create a thumbnail from it

                    ///get dimensions of the view that we want to show the image in
                    int[] dimens = fragment.getLayoutViewDimensions();
                    int viewWidth = dimens[0];
                    int viewHeight = dimens[1];

                    //get dimensions of video without loading it
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();//
                    retriever.setDataSource(file.getPath());
                    Bitmap bmp = retriever.getFrameAtTime();
                    int videoHeight = bmp.getHeight();
                    int videoWidth = bmp.getWidth();

                    //scale video to fit width
                    int newWidth = viewWidth;
                    int newHeight = (int) (((float) videoHeight) * (((float) viewWidth) / ((float) videoWidth)));

                    //if video is taller than the screen, scale to the height of the fragment view instead
                    if (newHeight > viewHeight) {
                        newHeight = viewHeight;
                        newWidth = (int) (((float) videoWidth) * (((float) viewHeight) / ((float) videoHeight)));
                    }

                    //make square thumbnail out of it
                    thumbnail = (ImageView) view.findViewById(R.id.poiContentVideoView);
                    Bitmap bMap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                    bMap = ThumbnailUtils.extractThumbnail(bMap, newWidth, newHeight);

                    thumbnail.setImageBitmap(bMap);

                } else {
                    //change file path to use the caching facility
                    HttpProxyCacheServer proxy = App.getProxy();
                    savedVideoFilePath = proxy.getProxyUrl(listViewItem.getUrl());

                    //this isn't strictly necessary until we want to move the cached file to
                    // permanent video folder, but it provides debugging info until it's implemented
                    proxy.registerCacheListener(new VideoCacheListener(), savedVideoFilePath);
                }

                //create final URL's so they can be accessed within the anon' inner class
                final String filePath = savedVideoFilePath;
                final String url = listViewItem.getUrl();

                //make the video show when the user clicks
                thumbnail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //create the video view
                        VideoFragment video = VideoFragment.newInstance(filePath, url);

                        //get fragment manager
                        Activity activity = ((App) getContext().getApplicationContext()).getCurrentActivity();
                        FragmentManager manager = activity.getFragmentManager();

                        //generate fragment transaction using fragment manager
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(R.id.fragmentContainer, video);
                        transaction.addToBackStack("video");

                        //display the video view
                        transaction.commit();

                        //set toolbar title to be something other than the poi title
                        ActionBar actionBar = ((AppCompatActivity) activity).getSupportActionBar();
                        if (actionBar != null) {
                            actionBar.setTitle("Video Player");
                        }

                        fullScreenRotation();
                    }
                });

                //set description
                TextView videoDesc = (TextView) view.findViewById(R.id.poiContentVideoDesc);
                videoDesc.setText(listViewItem.getText());

                return view;

            case HEADER:

                // TODO
                if (view.findViewById(R.id.poiHeaderTextView) == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.poi_header, parent, false);
                }

                contentView = (TextView) view.findViewById(R.id.poiHeaderTextView);
                contentView.setText(listViewItem.getText());

                if (listViewItem.getText().length() == 0) {
                    return new View(getContext());
                }

                return view;

            case BODY:

                // TODO
                contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText(listViewItem.getText());

                return view;

            case QUIZ:

                contentView = (TextView) view.findViewById(R.id.quizTitle);
                contentView.setText("Quiz: " + listViewItem.getText());
                if (quiz == null) {
                    quiz = new Quiz(listViewItem.getOption(), listViewItem.getSolution(), view);
                }

                return view;

            default:

                contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText("Something went wrong\n");
                Log.e(TAG, "poiContentAdapter could not find use type: " + listViewItem.getType());

                return view;

        }
    }

    /**
     * Sets the screen to be fullscreen and to allow screen roataion
     * The opposite of {@Link TourActivity} private method portraitShowUI()
     */
    private void fullScreenRotation() {
        //get activity
        Activity a = ((App) getContext().getApplicationContext()).getCurrentActivity();

        //allow rotation
        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        View decorView = a.getWindow().getDecorView();

        //Hide the status bar
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        //hide toolbar
        a.findViewById(R.id.toolbar).setVisibility(View.GONE);
    }

    /**
     * First checks that the requested image hasn't already been loaded and saved in the cache. If
     * this is not the case, then start an {@link AsyncTask} to load the image, if one hasn't
     * already been triggered.
     *
     * @param filename  the name of the file as it appears on disk
     * @param imageView the {@link ImageView} that the image will be loaded into
     */
    public void loadImageFromDiskOrCache(String filename, ImageView imageView) {
        ImageCache cache = ImageCache.getInstance();
        Bitmap cachedBitmap = cache.getBitmap(filename);

        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
        } else if (taskNotAlreadyRunning(imageView)) {
            final ImageLoadingTask task = new ImageLoadingTask(imageView);

            // will display the placeholder image immediately, but will be overwritten when the
            // ImageLoadingTask has finished executing
            final ASyncDrawable aSyncDrawable = new ASyncDrawable(App.context.getResources(), loadingBitmap, task);
            imageView.setImageDrawable(aSyncDrawable);

            task.execute(filename, keyID);
        }
    }

    /**
     * Dedicated {@link Drawable} subclass to store a reference back to an ImageLoadingTask and
     * provide a placeholder image until the task has finished.
     */
    private static class ASyncDrawable extends BitmapDrawable {
        private final WeakReference<ImageLoadingTask> imageTaskReference;

        public ASyncDrawable(Resources res, Bitmap bitmap, ImageLoadingTask task) {
            super(res, bitmap);
            imageTaskReference = new WeakReference<>(task);
        }

        public ImageLoadingTask getImageLoadingTask() {
            return imageTaskReference.get();
        }
    }

    /**
     * ASynchronously loads a saved image file into a {@link Bitmap}, which in turn gets placed in
     * an {@link ImageView}. The file is sampled, which saves a considerable amount of memory when
     * creating a Bitmap. The loaded image will be cached for quick access if its parent View is
     * scrolled off screen.
     */
    private class ImageLoadingTask extends AsyncTask<String, Void, Bitmap> {
        private static final String TAG = "ImageLoadingTask";

        private final WeakReference<ImageView> weakImageView;

        public ImageLoadingTask(ImageView imageView) {
            // use weak reference to facilitate garbage collection
            weakImageView = new WeakReference<>(imageView);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String imgPath = params[0];
            String keyID = params[1];
            File file = new File(App.context.getFilesDir(), String.format("%s/image/%s", keyID, imgPath));

            if (file.exists()) {
                Bitmap bitmap = getSampledBitmap(file);

                float size = (float) bitmap.getByteCount() / (1024.0f * 1024.0f);
                Log.i(TAG, String.format("About to add %.2f MB bitmap to cache", size));

                ImageCache.getInstance().addBitmap(imgPath, bitmap);
                return bitmap;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            ImageView imageView = getAttachedImageView();

            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

        private ImageView getAttachedImageView() {
            final ImageView imageView = weakImageView.get();
            final ImageLoadingTask task = getImageLoadingTask(imageView);

            if (this == task) {
                return imageView;
            }

            return null;
        }

        /**
         * Loading an image with large pixel dimensions in full is a waste of memory if it will be
         * placed in a much smaller {@link ImageView}. This loads a sub-sampled version of the image
         * which will still look good on the user's device.
         *
         * @param file the image to load
         * @return a potentially sub-sampled Bitmap, depending on the image size and the device size
         */
        private Bitmap getSampledBitmap(File file) {
            // first decode the image to get the raw pixel dimensions, without loading the image
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            ///get dimensions of the view that we want to show the image in
            int[] dimens = fragment.getLayoutViewDimensions();
            int viewWidth = dimens[0];
            int viewHeight = dimens[1];

            //get the resolution of the image and the fragment view
            int mpFile = options.outHeight * options.outWidth;
            int mpView = viewHeight * viewWidth;

            //calculate the factor by which we can sub-sample the saved image
            options.inSampleSize = (int) (mpFile / (1.41 * mpView));

            //now load file, but only load the size we need, not the whole thing
            options.inJustDecodeBounds = false;
            Bitmap sampledBM = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            //scale image to fit width
            int newWidth = viewWidth;
            int newHeight = (int) (((float) sampledBM.getHeight()) * (((float) viewWidth) / ((float) sampledBM.getWidth())));

            //if image is taller than the screen, scale to the height of the fragment view instead
            if (newHeight > viewHeight) {
                newHeight = viewHeight;
                newWidth = (int) (((float) sampledBM.getWidth()) * (((float) viewHeight) / ((float) sampledBM.getHeight())));
            }

            //return the bitmap for the screen
            return Bitmap.createScaledBitmap(sampledBM, newWidth, newHeight, true);
        }
    }
}