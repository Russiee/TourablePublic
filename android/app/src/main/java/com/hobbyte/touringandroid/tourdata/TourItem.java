package com.hobbyte.touringandroid.tourdata;

import android.os.Parcelable;

/**
 * Base class for {@link SubSection} and {@link PointOfInterest}. The reason for this is so that
 * we can create a {@link com.hobbyte.touringandroid.ui.fragment.SectionFragment} with both
 * SubSections and POIs in the ListView, using a single Adapter.
 */
public abstract class TourItem implements Parcelable {

    public static final int TYPE_SUBSECTION = 0;
    public static final int TYPE_POI = 1;

    public abstract String getTitle();

    public abstract int getType();
}
