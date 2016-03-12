package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.io.DownloadTourTask;
import com.hobbyte.touringandroid.io.ImageLoadingTask;
import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.internet.LoadImageFromURL;
import com.hobbyte.touringandroid.R;

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
    public static final int QUIZ = 4;

    private static Pattern namePattern;
    private static final String FILE_NAME_PATTERN = "https?:\\/\\/[-\\w\\.\\/]*\\/(.+\\.(jpe?g|png|mp4))";
    private ListViewItem[] items;

    private String keyID;

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
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
            }
        }

        switch (listViewItemType) {
            case IMAGE:
                ImageView imageView = (ImageView) view.findViewById(R.id.poiContentImageView);

                if (filename != null) {
                    final ImageLoadingTask task = new ImageLoadingTask(imageView);
                    task.execute(filename, keyID);
                }
                return view;

            case VIDEO:
                contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText("A video should go here\n");
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
}