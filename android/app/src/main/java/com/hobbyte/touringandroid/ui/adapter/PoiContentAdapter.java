package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
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

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.io.DownloadTourTask;
import com.hobbyte.touringandroid.io.ImageLoadingTask;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.internet.LoadImageFromURL;
import com.hobbyte.touringandroid.R;

import java.io.IOException;
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
    public static final int QUIZ = 4;

    private static Pattern namePattern;
    private static final String FILE_NAME_PATTERN = "https?:\\/\\/[-\\w\\.\\/]*\\/(.+\\.(jpe?g|png|mp4))";
    private ListViewItem[] items;

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
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
    }

    /**
     * Inflates a certain view depending on the type of ListViewItem (Normal text or Image URL)
     *
     * @param position Position of item in the ItemList
     * @param view     View
     * @param parent   ParentView
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

                if (filename != null) {
                    final ImageLoadingTask task = new ImageLoadingTask(imageView);
                    task.execute(filename, keyID);
                }
                return view;

            case VIDEO:
                filePath = getContext().getFilesDir() + "/" + String.format("%s/video/%s", keyID, filename);
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

                textureView.setSurfaceTextureListener(videoListener);
                TextView videoDesc = (TextView) view.findViewById(R.id.poiContentVideoDesc);
                videoDesc.setText(listViewItem.getText());
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
                    public void onPrepared(final MediaPlayer mp) {
                        play.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mp.isPlaying()) {
                                    mp.pause();
                                    play.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
                                } else {
                                    mp.start();
                                    play.setImageResource(R.mipmap.ic_pause_white_36dp);
                                }
                            }
                        });

                        replay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mp.isPlaying()) {
                                    play.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
                                    mp.pause();
                                    mp.seekTo(0);
                                } else {
                                    play.setImageResource(R.mipmap.ic_play_arrow_white_36dp);
                                    mp.seekTo(1000);
                                }
                            }
                        });
                        mute.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mp.setVolume(0.0f, 0.0f);
                            }
                        });
                        max.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mp.setVolume(1.0f, 1.0f);
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
            player.stop();
            player.release();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }
    };
}