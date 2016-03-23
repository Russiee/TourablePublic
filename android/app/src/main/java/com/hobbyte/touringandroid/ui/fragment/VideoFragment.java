package com.hobbyte.touringandroid.ui.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.hobbyte.touringandroid.R;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String PARAM_VIDEO = "video";
    private static final String PARAM_URL = "url";

    // TODO: Rename and change types of parameters
    private String filePath;
    private String url;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param filePath Path of the video to play.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String filePath, String url) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_VIDEO, filePath);
        args.putString(PARAM_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            filePath = getArguments().getString(PARAM_VIDEO);
            url = getArguments().getString(PARAM_URL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_video, container, false);
        File file = new File(filePath);
        VideoView vid = (VideoView) v.findViewById(R.id.videoView);
        MediaController controller = new MediaController(getActivity());
        vid.setKeepScreenOn(true);
        if (!file.exists()) {
            Uri uri = Uri.parse(url);
            vid.setVideoURI(uri);
        } else {
            vid.setVideoPath(filePath);
        }
        vid.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });
        vid.setMediaController(controller);
        vid.requestFocus();

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

}
