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
public class SubSectionTest {

    private SubSection section;

    @Mock private SubSection subsection;
    @Mock private PointOfInterest poi1;
    @Mock private PointOfInterest poi2;

    @Before
    public void setup() {
        // imitate construction and initialisation as it should happen in TourBuilder
        section = new SubSection(null, "Title", "Description", "abc123", 1);
        section.addItem(subsection);
        section.addItem(poi1);
        section.addItem(poi2);
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void badIndexTest() {
        SubSection notRealSection = section.getSubSection(1);
    }

    @Test
    public void getCorrectPOI() {
        PointOfInterest poi = section.getPOI(0);
        assertEquals(poi1, poi);
    }

    @Test
    public void correctParcelable() {
        Parcel p = Parcel.obtain();
        section.writeToParcel(p, 0);
        p.setDataPosition(0);

        SubSection test = SubSection.CREATOR.createFromParcel(p);
        assertEquals(section, test);
    }
}
