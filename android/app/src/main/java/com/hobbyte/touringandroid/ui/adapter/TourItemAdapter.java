package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hobbyte.touringandroid.App;
import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.tourdata.TourItem;

import java.util.ArrayList;

/**
 * Adapter for providing both SubSections and POIs to a ListView in a SectionFragment.
 */
public class TourItemAdapter extends ArrayAdapter<TourItem> {
    private static final String TAG = "TourItemAdapter";

    public TourItemAdapter(Context context, ArrayList<TourItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        TourItem selected = getItem(position);

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.subsections, parent, false);
        }

        TextView subsectionView = (TextView) view.findViewById(R.id.SubSectionTextView);
        TextView separator = (TextView) view.findViewById(R.id.separator);

        if (selected.getType() == TourItem.TYPE_SUBSECTION) {
            if(position == 0) {
                separator.setVisibility(View.VISIBLE);
                separator.setText(App.context.getString(R.string.tour_activity_subsection));
            }
            subsectionView.setText(selected.getTitle());
        } else {
            if(position == 0 || (position != 0) && getItem(position-1).getType() == TourItem.TYPE_SUBSECTION) {
                separator.setVisibility(View.VISIBLE);
                separator.setText(App.context.getString(R.string.tour_activity_poi));
            }
            subsectionView.setText(selected.getTitle());
        }
        return view;
    }
}
