package com.hobbyte.touringandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nikita on 09/02/2016.
 */
public class SubSectionAdapter extends ArrayAdapter<SubSection> {

    public SubSectionAdapter(Context context, ArrayList<SubSection> subsections) {
        super(context, 0, subsections);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        SubSection selected = getItem(position);
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.subsections, parent, false);
        }
        TextView subsectionView = (TextView) view.findViewById(R.id.SubSectionTextView);
        subsectionView.setText(selected.toString());
        return view;
    }
}