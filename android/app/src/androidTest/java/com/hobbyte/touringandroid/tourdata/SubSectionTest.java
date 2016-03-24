package com.hobbyte.touringandroid.tourdata;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Tests for the SubSection class. Main thing is to make sure that Parcelable was implemented
 * properly and POI indexing is correct.
 */
@RunWith(AndroidJUnit4.class)
public class SubSectionTest {

    private SubSection section;
    private PointOfInterest poi1;

    @Before
    public void setup() {
        // imitate construction and initialisation as it should happen in TourBuilder
        section = new SubSection(null, "Title", "Description", "abc123", 1);

        SubSection subsection = new SubSection(section, "mark", "test", "def456", 0);
        poi1 = new PointOfInterest(section, "john", "hello2016", 1);
        PointOfInterest poi2 = new PointOfInterest(section, "luke", "sdfrrbr", -1);

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
