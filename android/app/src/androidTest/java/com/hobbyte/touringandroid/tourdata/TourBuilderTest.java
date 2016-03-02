package com.hobbyte.touringandroid.tourdata;

import android.os.Parcelable;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.assertNotNull;

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
    private Tour tour;

    @Before
    public void setup() throws JSONException {
        try {
            json = new JSONObject(TEST_JSON);
            TourBuilder builder = new TourBuilder(json);
            builder.run(); // intentionally not doing start()
            tour = builder.getTour();
        } catch (JSONException e) {
            throw e;
        }
    }

    @Test
    public void tourMadeProperly() {
        assertNotNull(tour);
    }


    private static final String rootID = "rootID123";
    private static final String rootTitle = "Ultimate Fake Tour";
    private static final String rootDesc = "Fake tours are the best";

    private static final String s1_ID = "section1_ID";
    private static final String s1_title = "hello";
    private static final String s1_desc = "I love testing";

    private static final String s2_ID = "section2_ID";
    private static final String s2_title = "Program testing can";
    private static final String s2_desc = "be used to";

    private static final String s3_ID = "section3_ID";
    private static final String s3_title = "show the presence";
    private static final String s3_desc = "of bugs but";

    private static final String s4_ID = "section4_ID";
    private static final String s4_title = "never to show";
    private static final String s4_desc = "their absence!";

    private static final String s5_ID = "section5_ID";
    private static final String s5_title = "S5 Title";
    private static final String s5_desc = "S5 Description";

    private static final String s6_ID = "section5_ID";
    private static final String s6_title = "Where do foo";
    private static final String s6_desc = "and bar";

    private static final String p1_ID = "poi1_ID";
    private static final String p1_title = "Come from?";

    private static final String p2_ID = "poi2_ID";
    private static final String p2_title = "poi2_title";

    private static final String p3_ID = "poi3_ID";
    private static final String p3_title = "poi3_title";

    private static final String p4_ID = "poi4_ID";
    private static final String p4_title = "poi4_title";

    private static final String p5_ID = "poi5_ID";
    private static final String p5_title = "poi5_title";

    private static final String p6_ID = "poi6_ID";
    private static final String p6_title = "poi6_title";

    private static final String p7_ID = "poi7_ID";
    private static final String p7_title = "poi7_title";

    private static final String p8_ID = "poi8_ID";
    private static final String p8_title = "poi8_title";

    private static final String p9_ID = "poi9_ID";
    private static final String p9_title = "poi9_title";

    private static final String p10_ID = "poi10_ID";
    private static final String p10_title = "poi10_title";

    private static final String p11_ID = "poi11_ID";
    private static final String p11_title = "poi11_title";

    public static final String TEST_JSON =
            "{" +
                    "\"root\": \"" + rootID + "\"}," +
                    "\"" + rootID + "\":{" +
                        "\"title\":\"" + rootTitle + "\"," +
                        "\"description\":\"" + rootDesc + "\"," +
                        "\"subsections\":[\"" + s1_ID + "\",\"" + s2_ID + "\",\"" + s3_ID + "\"]" +
                    "}, \"" + s1_ID + "\":{" +
                        "\"title\":\"" + s1_title + "\"," +
                        "\"description\":\"" + s1_desc + "\"," +
                        "\"subsections\":[\"" + s4_ID + "\",\"" + s5_ID + "\"]" +
                    "}, \"" + s2_ID + "\":{" +
                        "\"title\":\"" + s2_title + "\"," +
                        "\"description\":\"" + s2_desc + "\"," +
                        "\"pois\":[{" +
                            "\"objectId\":\"" + p1_ID +"\"," +
                            "\"title\":\"" + p1_title + "\"" +
                        "},{" +
                            "\"objectId\":\"" + p2_ID +"\"," +
                            "\"title\":\"" + p2_title +"\"" +
                        "}]" +
                    "}, \"" + s3_ID + "\":{" +
                        "\"title\":\"" + s3_title + "\"," +
                        "\"description\":\"" + s3_desc + "\"," +
                        "\"pois\":[{" +
                            "\"objectId\":\"" + p3_ID +"\"," +
                            "\"title\":\"" + p3_title + "\"" +
                        "},{" +
                            "\"objectId\":\"" + p4_ID +"\"," +
                            "\"title\":\"" + p4_title +"\"" +
                        "},{" +
                            "\"objectId\":\"" + p5_ID +"\"," +
                            "\"title\":\"" + p5_title +"\"" +
                            "}]" +
                    "}, \"" + s4_ID + "\":{" +
                        "\"title\":\"" + s4_title + "\"," +
                        "\"description\":\"" + s4_desc + "\"," +
                        "\"pois\":[{" +
                            "\"objectId\":\"" + p6_ID +"\"," +
                            "\"title\":\"" + p6_title + "\"" +
                        "},{" +
                            "\"objectId\":\"" + p7_ID +"\"," +
                            "\"title\":\"" + p7_title +"\"" +
                        "}]," +
                        "\"subsections\":[" +
                            "\"" + s6_ID + "\"" +
                        "]" +
                    "}, \"" + s5_ID + "\":{" +
                        "\"title\":\"" + s5_title + "\"," +
                        "\"description\":\"" + s5_desc + "\"," +
                        "\"pois\":[{" +
                            "\"objectId\":\"" + p8_ID +"\"," +
                            "\"title\":\"" + p8_title + "\"" +
                        "},{" +
                            "\"objectId\":\"" + p9_ID +"\"," +
                            "\"title\":\"" + p9_title +"\"" +
                        "}]" +
                    "}, \"" + s6_ID + "\":{" +
                        "\"title\":\"" + s6_title + "\"," +
                        "\"description\":\"" + s6_desc + "\"," +
                        "\"pois\":[{" +
                            "\"objectId\":\"" + p10_ID +"\"," +
                            "\"title\":\"" + p10_title + "\"" +
                        "},{" +
                            "\"objectId\":\"" + p11_ID +"\"," +
                            "\"title\":\"" + p11_title +"\"" +
                        "}]" +
                    "}}";



}
