package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hobbyte.touringandroid.tourdata.ListViewItem;
import com.hobbyte.touringandroid.internet.LoadImageFromURL;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.ui.activity.StartActivity;

/**
 * @author Nikita
 */
public class PoiContentAdapter extends ArrayAdapter {

    public static final int TEXT = 0;
    public static final int IMG = 1;

    private ListViewItem[] items;

    private String keyID;

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return items[position].getType();
    }

    public PoiContentAdapter(Context context, ListViewItem[] content, String keyID) {
        super(context, 0, content);
        this.keyID = keyID;
        items = content;
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

        if (view == null) {
            if (listViewItemType == IMG) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_image, null);
            } else {
                view = LayoutInflater.from(getContext()).inflate(R.layout.poi_content, null);
            }
        }
            if (listViewItemType == IMG) {
                ImageView imageView = (ImageView) view.findViewById(R.id.poiContentImageView);
                System.out.println(keyID);
                new LoadImageFromURL(imageView, StartActivity.getContext()).execute(listViewItem.getText(), keyID); //Load image in a separate thread
                return view;
            } else {
                TextView contentView = (TextView) view.findViewById(R.id.poiContentTextView);
                contentView.setText(listViewItem.getText() + "\n");
                return view;
            }
        }
    }