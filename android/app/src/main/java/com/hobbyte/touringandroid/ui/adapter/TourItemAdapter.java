package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.tourdata.TourItem;

import java.util.ArrayList;

/**
 * Created by max on 29/02/16.
 */
public class TourItemAdapter extends ArrayAdapter<TourItem> {
    private static final String TAG = "TourItemAdapter";

    public TourItemAdapter(Context context, ArrayList<TourItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        TourItem selected = getItem(position);

        Log.d(TAG, "getView() on position " + position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.subsections, parent, false);
        }

        TextView subsectionView = (TextView) view.findViewById(R.id.SubSectionTextView);

        if (selected.getType() == TourItem.TYPE_SUBSECTION) {
            subsectionView.setText(String.format("S: %s", selected.getTitle()));
        } else {
            subsectionView.setText(String.format("P: %s", selected.getTitle()));
        }
        return view;
    }
}
