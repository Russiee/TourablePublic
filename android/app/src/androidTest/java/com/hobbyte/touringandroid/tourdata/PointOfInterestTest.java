package com.hobbyte.touringandroid.tourdata;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * The {@link PointOfInterest} class has little to test, but we want to ensure that Parcelable was
 * implemented correctly.
 */
@RunWith(AndroidJUnit4.class)
public class PointOfInterestTest {

    private PointOfInterest poi;

    @Before
    public void setup() {
        SubSection parent = new SubSection(null, "paul", "this", "poop123", 0);
        poi = new PointOfInterest(parent, "POI Title", "poiabc123", -1);
        parent.addItem(poi);
    }

    @Test
    public void correctParcelable() {
        Parcel p = Parcel.obtain();
        poi.writeToParcel(p, 0);
        p.setDataPosition(0);

        PointOfInterest test = PointOfInterest.CREATOR.createFromParcel(p);
        assertEquals(poi, test);
    }
}
