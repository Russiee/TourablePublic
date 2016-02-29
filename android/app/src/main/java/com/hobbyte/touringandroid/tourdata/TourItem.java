package com.hobbyte.touringandroid.tourdata;

import android.os.Parcelable;

/**
 * Created by max on 29/02/16.
 */
public abstract class TourItem implements Parcelable {

    public static final int TYPE_SUBSECTION = 0;
    public static final int TYPE_POI = 1;


    public abstract String getTitle();

    public abstract int getType();
}
