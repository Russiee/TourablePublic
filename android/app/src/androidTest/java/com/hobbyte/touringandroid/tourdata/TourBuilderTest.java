package com.hobbyte.touringandroid.tourdata;

import android.os.Parcelable;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import static com.hobbyte.touringandroid.FakeJSON.*;

/**
 * Checks both that {@link TourBuilder} creates a {@link Tour} object, and that {@link SubSection}
 * and {@link PointOfInterest} objects in a Tour are made with the correct hierarchy.
 * <p>
 * Must be an instrumented unit test because SubSection and PointOfInterest implement
 * {@link Parcelable}.
 * <p>
 * Apologies for the wall of text.
 */
@RunWith(AndroidJUnit4.class)
public class TourBuilderTest {

    private JSONObject json;
    public Tour tour;
    SubSection root;

    @Before
    public void setup() throws JSONException {
        json = new JSONObject(BUNDLE_JSON);
        TourBuilder builder = new TourBuilder(json);
        builder.run(); // intentionally not doing start()
        tour = builder.getTour();
        root = tour.getRoot();
    }

    @Test
    public void tourMadeProperly() {
        assertNotNull(tour);
    }

    @Test
    public void rootFieldsTest() {
        assertEquals(rootID, root.getObjectID());
        assertEquals(rootTitle, root.getTitle());
        assertEquals(3, root.getContents().size());
    }

    @Test
    public void correctContentsSize() {
        SubSection s1 = root.getSubSection(0);
        SubSection s2 = root.getSubSection(1);
        SubSection s3 = root.getSubSection(2);

        assertEquals(2, s1.getContents().size());
        assertEquals(2, s2.getContents().size());
        assertEquals(3, s3.getContents().size());
    }

    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void badIndexTest() {
        // retrieves Section 4, which had one subsection and two POIs
        SubSection section4 = root.getSubSection(0).getSubSection(0);
        SubSection actuallyAPOI = section4.getSubSection(1);
    }

    @Test
    public void poisMadeCorrectly() {
        SubSection section3 = root.getSubSection(2);
        PointOfInterest poi3 = section3.getPOI(0);
        PointOfInterest poi4 = section3.getPOI(1);
        PointOfInterest poi5 = section3.getPOI(2);

        // basic field checking
        assertEquals(p4_title, poi4.getTitle());
        assertEquals(p5_ID, poi5.getObjectID());

        // make sure correct indexes were given to POI constructors
        assertEquals(poi4, poi3.getNextPOI());
        assertEquals(poi5, poi4.getNextPOI());
        assertNull(poi5.getNextPOI());
    }

    @Test
    public void hasCorrectParent() {
        SubSection section1 = root.getSubSection(0);
        SubSection section3 = root.getSubSection(2);
        PointOfInterest poi3 = section3.getPOI(0);

        assertEquals(section3, poi3.getParent());
        assertEquals(root, section1.getParent());
        assertEquals(root, section3.getParent());
    }
}
