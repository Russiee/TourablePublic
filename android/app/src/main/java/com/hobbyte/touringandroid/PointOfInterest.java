package com.hobbyte.touringandroid;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nikita on 08/02/2016.
 */
public class PointOfInterest implements Serializable{

    //Implements serializable for easy of transfer in intents
    private String name;
    private ArrayList<String> information;

    /**
     * Create a Point Of Interest based on it's name and specific information strings already prepared
     * @param name
     * @param information
     */
    public PointOfInterest(String name, String... information) {
        this.information = new ArrayList<String>();
        this.name = name;
        for(String s: information) {
            this.information.add(s);
        }
    }

    /**
     * Creates a very rough tester to see if POI implements correctly
     */
    public PointOfInterest() {
        this.name = "Temp POI Name";
        this.information = new ArrayList<String>();
        this.information.add("Lorem Ipsum Bla Bla Bla Bla");
    }

    /**
     * Add extra Information to the Point of Interest if needed
     * @param extraInfo Extra Information to be added
     */
    public void addInformation(String extraInfo) {
        this.information.add(extraInfo);
    }

    /**
     * Returns a string with information line by line.
     * @return string containing information line by line
     */
    public String toString() {
        String returnString = "";
        for(String s: this.information) {
            returnString += s + "\n";
        }
        return returnString;
    }
}
