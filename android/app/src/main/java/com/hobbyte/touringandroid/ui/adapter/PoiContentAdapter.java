package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.internet.LoadImageFromURL;
import com.hobbyte.touringandroid.tourdata.ListViewItem;

/**
 * @author Nikita
 */
public class PoiContentAdapter extends ArrayAdapter<ListViewItem> {
    private static final String TAG = "PoiContentAdapter";

    public static final int HEADER = 0;
    public static final int BODY = 1;
    public static final int IMG = 2;
    public static final int VIDEO = 3;
    public static final int QUIZ = 4;

    private ListViewItem[] items;

    private String keyID;

    public PoiContentAdapter(Context context, ListViewItem[] content, String keyID) {
        super(context, 0, content);
        this.keyID = keyID;
        items = content;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
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

        if (view == null) {

            TextView contentView;

            switch (listViewItemType) {

                case HEADER:

                    view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
                    contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                    contentView.setTypeface(null, Typeface.BOLD);
                    contentView.setText(listViewItem.getText() + "\n");

                    return view;
                case BODY:

                    view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
                    contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                    contentView.setText(listViewItem.getText() + "\n");

                    return view;
                case IMG:

                    view = LayoutInflater.from(getContext()).inflate(R.layout.poi_image, parent, false);
                    ImageView imageView = (ImageView) view.findViewById(R.id.poiContentImageView);
                    new LoadImageFromURL(imageView, App.context).execute(listViewItem.getText(), keyID); //Load image in a separate thread

                    return view;
                case VIDEO:

                    //break;
                case QUIZ:

                    //break;
                default:
                    Log.e(TAG, "Unknown type");
                    view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, parent, false);
                    contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                    contentView.setText("Error");

                    return view;
            }
        }

        return view;
    }
}