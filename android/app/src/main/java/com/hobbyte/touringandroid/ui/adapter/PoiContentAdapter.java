package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.io.DownloadTourTask;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.internet.LoadImageFromURL;
import com.hobbyte.touringandroid.R;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nikita
 */
public class PoiContentAdapter extends ArrayAdapter<ListViewItem> {
    private static final String TAG = "PoiContentAdapter";

    public static final int HEADER = 0;
    public static final int BODY = 1;
    public static final int IMAGE = 2;
    public static final int VIDEO = 3;

    private static Pattern namePattern;
    private static final String FILE_NAME_PATTERN = "https?:\\/\\/[-\\w\\.\\/]*\\/(.+\\.(jpe?g|png|mp4))";

    private ListViewItem[] items;

    private String keyID;

    private TextureView textureView;
    private MediaPlayer player;

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
    }

    public PoiContentAdapter(Context context, ListViewItem[] content, String keyID) {
        super(context, 0, content);
        this.keyID = keyID;
        items = content;
        namePattern = Pattern.compile(FILE_NAME_PATTERN);
    }

    /**
     * Inflates a certain view depending on the type of ListViewItem (Normal text or Image URL)
     * @param position Position of item in the ItemList
     * @param view View
     * @param parent ParentView
     * @return the view in question
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
//                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_image, parent, false);
            } else if(listViewItemType == VIDEO) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_video, parent, false);
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
            }
        }

        switch (listViewItemType) {
            case IMAGE:
                /*contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText("An image should go here\n");
                return view; */

                ImageView imageView = (ImageView) view.findViewById(R.id.poiContentImageView);
                TextView textView = (TextView) view.findViewById(R.id.poiContentImageDesc);
                textView.setText(listViewItem.getText());

                if (filename != null) {
                    new LoadImageFromURL(imageView, App.context).execute(filename, keyID); //Load image in a separate thread
                }
                return view;

            case VIDEO:
                /*contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText("A video should go here\n");
                return view;*/
                final String filePath = getContext().getFilesDir() + "/" + String.format("%s/video/%s", keyID, filename);
                textureView = (TextureView) view.findViewById(R.id.poiContentVideoView);
                TextView videoDesc = (TextView) view.findViewById(R.id.poiContentVideoDesc);
                videoDesc.setText(listViewItem.getText());
                DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
                int height = metrics.heightPixels / 2;
                int width = metrics.widthPixels;
                textureView.setMinimumHeight(height);
                textureView.setMinimumWidth(width);

                textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                    @Override
                    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                        Surface s = new Surface(surface);
                        try {
                            if(player != null) {
                                player.reset();
                            } else {
                                player = new MediaPlayer();
                            }
                            player.setDataSource(filePath);
                            player.setSurface(s);
                            player.prepareAsync();
                            player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                                @Override
                                public void onBufferingUpdate(MediaPlayer mp, int percent) {
                                    //Do nothing
                                }
                            });
                            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    //Do nothing
                                }
                            });
                            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mp) {
                                    player.start();
                                }
                            });
                            player.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                                @Override
                                public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                                    //Do nothing
                                }
                            });
                            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            player.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
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
                        player.stop();
                        player.release();
                        return true;
                    }

                    @Override
                    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                    }
                });
                return view;
            case HEADER:
                // TODO
                contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText(listViewItem.getText() + "\n");
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

        /*if (listViewItemType == IMAGE) {
            ImageView imageView = (ImageView) view.findViewById(R.id.poiContentImageView);

            if (filename != null) {
                new LoadImageFromURL(imageView, App.context).execute(filename, keyID); //Load image in a separate thread
            }
            return view;
        } else {
            contentView = (TextView) view.findViewById(R.id.poiContentTextView);
            contentView.setText(listViewItem.getText() + "\n");
            return view;
        }*/
    }

    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int width = textureView.getWidth();
        int height = textureView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth, newHeight;
        if (height > (int) (width * aspectRatio)) {
            // limited by narrow width; restrict height
            newWidth = width;
            newHeight = (int) (width * aspectRatio);
        } else {
            // limited by short height; restrict width
            newWidth = (int) (height / aspectRatio);
            newHeight = height;
        }
        int x = (width - newWidth) / 2;
        int y = (height - newHeight) / 2;

        Matrix transform = new Matrix();
        textureView.getTransform(transform);
        transform.setScale((float) newWidth / width, (float) newHeight / height);
        transform.postTranslate(x, y);
        textureView.setTransform(transform);
    }


}