package com.hobbyte.touringandroid.tourdata;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Nikita
 */
public class SubSection implements Serializable {

    //Todo: Implement method for adding subsections as arraylist instead of POIs
    //Serializable implemented for easy of transfer via Intents
    private ArrayList<PointOfInterest> listOfPOI;
    private ArrayList<SubSection> listOfSub;
    private String name;
    private String description;
    private boolean hasPOI; //To check whether this has subsections nested within or if it ends with a POI

    /**
     * Create subsection with arraylist of points of interest contained within and its name
     * @param name of subsection
     * @param poi PointsOfInterest contained within subsection
     */
    public SubSection(String name, String description, ArrayList<PointOfInterest> poi,
                      ArrayList<SubSection> sub) {
        this.name = name;
        this.description = description;
        this.hasPOI = poi.size() > 0;
        this.listOfPOI = poi;
        this.listOfSub = sub;
    }

    /**
     * Very rough guideline for checking if subsection is created
     */
    public SubSection() {
        this.name = "Temp Subsection Name";
        this.description = "Temp Subsection Description";
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

    public ArrayList<SubSection> getListOfSub() { return listOfSub; }
}
