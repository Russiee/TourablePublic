package com.hobbyte.touringandroid.internet;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Jonathan
 */
@RunWith(AndroidJUnit4.class)
public class ServerAPITest {

    private static final String GOOD_KEY_CODE = "KCL-1010";
    private static final String BAD_KEY_CODE = "not-a-key";
    private static final String EXPIRED_KEY_CODE = "KCL-TEST-EXPIRED";
    private static final String GOOD_TOUR_ID = "cjWRKDygIZ";
    private static final String BAD_TOUR_ID = "not-an-id";

    /**
     * Checks that the ServerAPI call to CheckKeyValidity is correct
     *
     * @throws Exception
     */
    @Test
    public void checkKeyValidity_valid() throws Exception {

        //values to test
        String expectedClassName = "Tour";
        JSONObject keyJSON;

        //use valid key
        keyJSON = ServerAPI.checkKeyValidity(GOOD_KEY_CODE);
        assertNotNull(keyJSON);

        //get data from json
        String tourID = keyJSON.getJSONObject("tour").getString("objectId");
        String className = keyJSON.getJSONObject("tour").getString("className");

        //check it's correct
        assertEquals(GOOD_TOUR_ID, tourID);
        assertEquals(expectedClassName, className);
    }

    /**
     * Checks that the ServerAPI call to CheckKeyValidity returns null for an invalid key
     * @throws Exception
     */
    @Test
    public void checkKeyValidity_invalid() throws Exception {

        //values to test
        JSONObject keyJSON;

        //performs call
        keyJSON = ServerAPI.checkKeyValidity(BAD_KEY_CODE);

        //check
        assertEquals(keyJSON, null);
    }

    /**
     * Checks that the ServerAPI call to CheckKeyValidity returns null for an expired key
     * @throws Exception
     */
    @Test
    public void checkKeyValidity_expired() throws Exception {

        //values to test
        JSONObject keyJSON;

        //perform call
        keyJSON = ServerAPI.checkKeyValidity(EXPIRED_KEY_CODE);

        //check
        assertEquals(keyJSON, null);
    }


    /**
     * Check that the ServerAPI call gets the correct value
     * @throws Exception
     */
    @Test
    public void checkGetJSON_valid_bundle() throws Exception {

        //perform call
        JSONObject bundleJSON = ServerAPI.getJSON(GOOD_TOUR_ID, ServerAPI.BUNDLE);

        //we should get a response back
        assertNotNull(bundleJSON);

        //bundle is the only response to contain this array
        JSONArray sections = bundleJSON.getJSONArray("sections");
        assertNotNull(sections);

        String sectionsString = sections.toString();
        //only poi objects have post and therefore only the bundle
        //there is the possibility for a tour to simply have no poi's
        //but that is a case that the cms should enforce
        assertTrue(sectionsString.contains("post"));

        //both the bundle and the tour contain the description tag, but the keyJSON doesn't
        assertNotNull(bundleJSON.getString("description"));
    }

    /**
     * Check that the ServerAPI call gets the correct value
     * @throws Exception
     */
    @Test
    public void checkGetJSON_valid_tour() throws Exception {

        //perform action
        JSONObject tourJSON = ServerAPI.getJSON(GOOD_TOUR_ID, ServerAPI.TOUR);

        //should get a result
        assertNotNull(tourJSON);

        //only poi objects have this array and therefore only the bundle
        //therefore we assertNull
        assertFalse(tourJSON.has("sections"));

        //both the bundle and the tour contain the description tag, but the keyJSON doesn't
        assertNotNull(tourJSON.getString("description"));
    }

    /**
     * Check that the ServerAPI call fails cleanly
     * @throws Exception
     */
    @Test
    public void checkGetJSON_invalid() throws Exception {

        //perform action
        JSONObject tourJSON = ServerAPI.getJSON(BAD_TOUR_ID, ServerAPI.TOUR);

        //should get null back
        assertEquals(tourJSON, null);
    }



}
