package com.hobbyte.touringandroid.tourdata;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by max on 03/03/16.
 */
@RunWith(AndroidJUnit4.class)
public class PointOfInterestTest {

    private PointOfInterest poi;

    private SubSection parent;
//    @Mock private SubSection parent;

    @Before
    public void setup() {
        parent = new SubSection(null, "poop", "poop123", 0);
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