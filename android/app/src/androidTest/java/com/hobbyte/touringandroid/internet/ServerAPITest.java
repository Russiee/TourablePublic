package com.hobbyte.touringandroid.internet;

import android.support.test.runner.AndroidJUnit4;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        //use valid key
        keyJSON = ServerAPI.checkKeyValidity(testKey);
        assertEquals(null, keyJSON);
    }

    @Test
    public void checkKeyValidity_expired() throws Exception {

        //values to test
        String testKey = "KCL-TEST-EXPIRED";
        JSONObject keyJSON;

        //use valid key
        keyJSON = ServerAPI.checkKeyValidity(testKey);
        assertEquals(null, keyJSON);
    }




}
