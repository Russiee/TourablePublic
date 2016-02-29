package com.hobbyte.touringandroid.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hobbyte.touringandroid.R;
import com.hobbyte.touringandroid.tourdata.SubSection;

/**
 * Adapter which tells a ListView how SubSections should be displayed in the app.
 */
public class SubSectionAdapter extends ArrayAdapter<SubSection> {

    public SubSectionAdapter(Context context, SubSection[] subsections) {
        super(context, 0, subsections);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        SubSection selected = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.subsections, parent, false);
        }
        TextView subsectionView = (TextView) view.findViewById(R.id.SubSectionTextView);
        subsectionView.setText(selected.getTitle());
        return view;
    }
}