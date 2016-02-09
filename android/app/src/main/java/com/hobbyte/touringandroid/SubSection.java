package com.hobbyte.touringandroid;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nikita on 08/02/2016.
 */
public class SubSection implements Serializable {

    //Todo: Implement method for adding subsections as arraylist instead of POIs
    //Serializable implemented for easy of transfer via Intents
    private ArrayList<PointOfInterest> listOfPOI;
    private String name;
    private boolean hasPOI; //To check whether this has subsections nested within or if it ends with a POI

    /**
     * Create subsection with arraylist of points of interest contained within and its name
     * @param name of subsection
     * @param poi PointsOfInterest contained within subsection
     */
    public SubSection(String name, PointOfInterest... poi) {
        this.name = name;
        this.hasPOI = true;
        listOfPOI = new ArrayList<PointOfInterest>();
        for(PointOfInterest p: poi) {
            listOfPOI.add(p);
        }
    }

    /**
     * Very rough guideline for checking if subsection is created
     */
    public SubSection() {
        this.name = "Temp Subsection Name";
        this.hasPOI = true;
        listOfPOI = new ArrayList<PointOfInterest>();
        listOfPOI.add(new PointOfInterest());
    }

    /**
     * Add a point of interest to the subsection
     * @param poi to be added
     */
    public void addPointOfInterest(PointOfInterest poi) {
        listOfPOI.add(poi);
    }

    /**
     * Return arraylist of points of interest
     * @return
     */
    public ArrayList<PointOfInterest> getPOIs() {
        return listOfPOI;
    }

    /**
     * Return a specific point of interest based on index
     * @param index of point of interest to be returned
     * @return point of interest
     */
    public PointOfInterest getPointOfInterest(int index) {
        return listOfPOI.get(index);
    }

    public String toString() {
        return name;
    }

    /**
     * Check whether Subsection has nested subsections within or if it contains POI (So is a final end)
     * @return
     */
    public boolean isHasPOI() {
        return hasPOI;
    }
}
