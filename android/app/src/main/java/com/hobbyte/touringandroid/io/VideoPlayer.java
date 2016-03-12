package com.hobbyte.touringandroid.io;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;

import java.io.IOException;

/**
 * Created by Nikita on 12/03/2016.
 */
public class VideoPlayer {

    private View view;
    private String filepath;

    private TextureView textureView;
    private AudioManager audio;



    private MediaPlayer player;

    public VideoPlayer(View view, String filepath) {
        this.view = view;
        this.filepath = filepath;

    }




}
