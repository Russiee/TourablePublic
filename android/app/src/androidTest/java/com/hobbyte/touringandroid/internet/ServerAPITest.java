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

    /**
     * Checks that the ServerAPI call to CheckKeyValidity is correct
     *
     * @throws Exception
     */
    @Test
    public void checkKeyValidity_valid() throws Exception {

        //values to test
        String testKey = "KCL-1010";
        String expectedTourID = "cjWRKDygIZ";
        String expectedClassName = "Tour";
        JSONObject keyJSON;

        //use valid key
        keyJSON = ServerAPI.checkKeyValidity(testKey);
        assertNotNull(keyJSON);

        //get data from json
        String tourID = keyJSON.getJSONObject("tour").getString("objectId");
        String className = keyJSON.getJSONObject("tour").getString("className");

        //check it's correct
        assertEquals(tourID, expectedTourID);
        assertEquals(className, expectedClassName);

    }

    /**
     * Checks that the ServerAPI call to CheckKeyValidity returns null for an invalid key
     * @throws Exception
     */
    @Test
    public void checkKeyValidity_invalid() throws Exception {

        //values to test
        String testKey = "not-a-key-test";
        JSONObject keyJSON;

        //performs call
        keyJSON = ServerAPI.checkKeyValidity(testKey);

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
        String testKey = "KCL-TEST-EXPIRED";
        JSONObject keyJSON;

        //perform call
        keyJSON = ServerAPI.checkKeyValidity(testKey);

        //check
        assertEquals(keyJSON, null);
    }


    /**
     * Check that the ServerAPI call gets the correct value
     * @throws Exception
     */
    @Test
    public void checkGetJSON_valid_bundle() throws Exception {

        //value to test
        String id = "cjWRKDygIZ";

        //perform call
        JSONObject bundleJSON = ServerAPI.getJSON(id, ServerAPI.BUNDLE);

        //we should get a response back
        assert bundleJSON != null;

        //bundle is the only response to contain this array
        JSONArray sections = bundleJSON.getJSONArray("sections");
        assert sections != null;

        String sectionsString = sections.toString();
        //only poi objects have post and therefore only the bundle
        //there is the possibility for a tour to simply have no poi's
        //but that is a case that the cms should enforce
        assertTrue(sectionsString.contains("post"));

        //both the bundle and the tour contain the description tag, but the keyJSON doesn't
        assert bundleJSON.getString("description") != null;

    }

    /**
     * Check that the ServerAPI call gets the correct value
     * @throws Exception
     */
    @Test
    public void checkGetJSON_valid_tour() throws Exception {

        //value to test
        String id = "cjWRKDygIZ";

        //perform action
        JSONObject tourJSON = ServerAPI.getJSON(id, ServerAPI.TOUR);

        //should get a result
        assert tourJSON != null;

        //only poi objects have this array and therefore only the bundle
        //therefore we assertNull
        assertFalse(tourJSON.has("sections"));

        //both the bundle and the tour contain the description tag, but the keyJSON doesn't
        assert tourJSON.getString("description") != null;

    }

    /**
     * Check that the ServerAPI call fails cleanly
     * @throws Exception
     */
    @Test
    public void checkGetJSON_invalid() throws Exception {

        //value to test
        String id = "not-an-id";

        //perform action
        JSONObject tourJSON = ServerAPI.getJSON(id, ServerAPI.TOUR);

        //should get null back
        assertEquals(tourJSON, null);
    }



}
