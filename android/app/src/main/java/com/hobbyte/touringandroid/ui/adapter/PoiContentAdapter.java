package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.FrameworkSampleSource;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.SampleSource;
import com.google.android.exoplayer.TrackRenderer;
import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.ui.fragment.POIFragment;
import com.hobbyte.touringandroid.ui.util.ImageCache;

import java.io.File;
import java.io.IOException;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link ArrayAdapter} that parses a POI JSON file to create the elements contained therein. A
 * POI can have any combination of the following item types:
 * <ul>
 *     <li>Header text</li><li>Body text</li>
 *     <li>An image with description</li><li>A video with description</li>
 * </ul>
 * <p>
 * Much of the code relating to image loading and caching was inspired by the Android
 * <a href="https://developer.android.com/training/displaying-bitmaps/index.html">
 * training guides</a>.
 */
public class PoiContentAdapter extends ArrayAdapter<ListViewItem> {
    private static final String TAG = "PoiContentAdapter";

    public static final int HEADER = 0;
    public static final int BODY = 1;
    public static final int IMAGE = 2;
    public static final int VIDEO = 3;
    public static final int QUIZ = 4;

    private static Pattern namePattern;
    private static final String FILE_NAME_PATTERN = "https?:\\/\\/[-\\w\\.\\/]*\\/(.+\\.(jpe?g|png|mp4))";
    private ListViewItem[] items;
    private Bitmap loadingBitmap;

    private String keyID;

    private TextureView textureView;
    private MediaPlayer player;
    private AudioManager audio;

    private ImageButton play;
    private ImageButton replay;
    private ImageButton mute;
    private ImageButton max;
    private SeekBar volume;

    private String filePath;

    public PoiContentAdapter(Context context, ListViewItem[] content, String keyID) {
        super(context, 0, content);
        this.keyID = keyID;
        items = content;
        namePattern = Pattern.compile(FILE_NAME_PATTERN);

        // load a low-resolution placeholder that will initially be used in place of actual images
        // and will be replaced as soon as a requested image has loaded
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        loadingBitmap = BitmapFactory.decodeResource(App.context.getResources(), R.drawable.poi_image_placeholder);
    }

    /**
     * Returns the number of different POI element types. (Needed or else things break).
     */
    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
        public int getItemViewType(int position) {
        return items[position].getType();
    }

    /**
     * Inflates a certain view depending on the type of ListViewItem (header, body, image, or video).
     *
     * @param position Position of item in the ItemList
     * @param view View
     * @param parent ParentView
     * @return the inflated View
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ListViewItem listViewItem = items[position];
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
            } else if(listViewItemType == VIDEO) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_video, parent, false);
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
                filePath = getContext().getFilesDir() + "/" + String.format("%s/video/%s", keyID, filename);
                File file = new File(filePath);
                if(!file.exists()) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
                    contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                    contentView.setText("This contains a video." + "\n" + "Download this tour with Media to see this Video!" + "\n");
                    contentView.setGravity(Gravity.CENTER_HORIZONTAL);
                } else {
                    textureView = (TextureView) view.findViewById(R.id.poiContentVideoView);

                    DisplayMetrics metrics = App.context.getResources().getDisplayMetrics();
                    int height = metrics.heightPixels / 2;
                    int width = metrics.widthPixels;
                    textureView.setMinimumHeight(height);
                    textureView.setMinimumWidth(width);

                    play = (ImageButton) view.findViewById(R.id.playButton);
                    replay = (ImageButton) view.findViewById(R.id.replayButtoon);
                    mute = (ImageButton) view.findViewById(R.id.muteButton);
                    max = (ImageButton) view.findViewById(R.id.maxVolButton);
                    volume = (SeekBar) view.findViewById(R.id.volumeControl);
                    audio = (AudioManager) App.context.getSystemService(Context.AUDIO_SERVICE);

                    play.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(player.isPlaying()) {
                                player.pause();
                                play.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
                            } else {
                                player.start();
                                play.setImageResource(R.mipmap.ic_pause_white_36dp);
                            }
                        }
                    });

                    replay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            player.stop();
                            player.reset();
                            try {
                                player.setDataSource(filePath);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                player.prepareAsync();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            }
                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.seekTo(0);
                                    play.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
                                }
                            });
                        }
                    });
                    mute.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            player.setVolume(0.0f, 0.0f);
                            volume.setProgress(0);
                        }
                    });
                    max.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            player.setVolume(1.0f, 1.0f);
                            volume.setProgress(audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
                        }
                    });

                    int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                    int currVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                    volume.setMax(maxVolume);
                    volume.setProgress(currVolume);
                    volume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            audio.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    });

                    textureView.setSurfaceTextureListener(videoListener);
                    TextView videoDesc = (TextView) view.findViewById(R.id.poiContentVideoDesc);
                    videoDesc.setText(listViewItem.getText());
                }
                return view;
            case HEADER:
                // TODO
                if(view.findViewById(R.id.poiContentTextView) == null) {
                    view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
                }
                contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText(listViewItem.getText() + "\n");
                if(listViewItem.getText().length() == 0) {
                    return new View(getContext());
                }
                return view;
            case BODY:
                // TODO
                contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText(listViewItem.getText() + "\n");
                return view;
            default:
                contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText("Something went wrong\n");
                return view;
        }
    }

    private TextureView.SurfaceTextureListener videoListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
            Surface s = new Surface(surface);
            try {
                player = new MediaPlayer();
                player.setDataSource(filePath);
                player.setSurface(s);
                player.prepareAsync();
                player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                player.setLooping(false);
                player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        //Do nothing
                    }
                });
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        play.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
                        mp.reset();
                        try {
                            mp.setDataSource(filePath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            mp.prepareAsync();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mediaPlayer) {
                                mediaPlayer.seekTo(0);
                            }
                        });
                    }
                });
                player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(final MediaPlayer mp) {
                        mp.seekTo(0);
                    }
                });

                player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        //Do nothing
                    }
                });
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };

    /**
     * First checks that the requested image hasn't already been loaded and saved in the cache. If
     * this is not the case, then start an {@link AsyncTask} to load the image, if one hasn't
     * already been triggered.
     *
     * @param filename the name of the file as it appears on disk
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

            // then calculate the factor by which we can sub-sample the actual image, and load the
            // sub-sampled bitmap
            options.inSampleSize = calculateSampleSize(options);
            options.inJustDecodeBounds = false;

            Bitmap sampledBM = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

            // if the sampled image is over a certain height, use half the screen height
            int height = POIFragment.SCREEN_HEIGHT / 2;
            boolean squishPhoto = sampledBM.getHeight() > height;

            if (squishPhoto) {
                return Bitmap.createScaledBitmap(sampledBM, POIFragment.SCREEN_WIDTH, height, true);
            } else {
                return sampledBM;
            }
        }

        /**
         * Calculates the sample size value to use when loading the image, as a power of two (as
         * directed).
         */
        private int calculateSampleSize(BitmapFactory.Options options) {
            final int imgHeight = options.outHeight;
            final int imgWidth = options.outWidth;
            int sampleSize = 1;

            int height = POIFragment.SCREEN_HEIGHT / 2;

            if (imgHeight > height || imgWidth > POIFragment.SCREEN_WIDTH) {
                while ((imgHeight / sampleSize > height) &&
                        (imgWidth / sampleSize > POIFragment.SCREEN_WIDTH)) {
                    sampleSize *= 2;
                }
            }

            return sampleSize;
        }
    }
}