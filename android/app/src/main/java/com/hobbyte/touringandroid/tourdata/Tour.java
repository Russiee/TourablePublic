package com.hobbyte.touringandroid.tourdata;

import android.util.Log;

/**
 * Contains the structure of a tour. The root {@link SubSection} holds references to all other
 * {@link SubSection}s and {@link PointOfInterest}s. It is essentially a Tree structure.
 */
public class Tour {
    private static final String TAG = "Tour";

    private final SubSection root;
    private SubSection currentSection;

    public Tour(SubSection root) {
        this.root = root;
        currentSection = root;
    }

    public SubSection getRoot() {
        return root;
    }

    public SubSection getCurrentSection() {
        return currentSection;
    }

    public void printTour(TourItem item, int depth) {
        if (item.getType() == TourItem.TYPE_SUBSECTION) {
            SubSection s = (SubSection) item;

            Log.d(TAG, String.format("%s %s", steps(depth), s));

            for (TourItem t : s.getContents()) {
                printTour(t, depth + 1);
            }
        } else {
            PointOfInterest p = (PointOfInterest) item;
            Log.d(TAG, String.format("%s %s", steps(depth), p));
        }
    }

    private String steps(int depth) {
        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < depth; ++i) {
            sb.append("-");
        }

        return sb.toString();
    }

}