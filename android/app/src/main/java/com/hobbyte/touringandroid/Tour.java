package com.hobbyte.touringandroid;

import java.util.ArrayList;

/**
 * Created by Nikita on 08/02/2016.
 */
public class Tour {
    private String name;
    private String description;
    private ArrayList<SubSection> listOfSubSections;

    /**
     * Create a tour based on specific subsections already defined
     * @param name of tour
     * @param subsections to be added
     */
    public Tour(String name, String description, ArrayList<SubSection> subsections) {
        this.name = name;
        this.description = description;
        listOfSubSections = new ArrayList<SubSection>();
        for(SubSection s: subsections) {
            listOfSubSections.add(s);
        }
    }

    /**
     * Very very rough template again for checking if created
     */
    public Tour() {
        this.name = "Temp Tour Name";
        this.description = "Temporary Description";
        listOfSubSections = new ArrayList<SubSection>();
        listOfSubSections.add(new SubSection());
    }

    /**
     * Add subsections to tour if needed
     * @param sub subsections to be added
     */
    public void addSubSection(SubSection sub) {
        listOfSubSections.add(sub);
    }

    /**
     * Retrieve ArrayList of Subsections within this tour
     * @return ArrayList
     */
    public ArrayList<SubSection> getSubSections() {
        return listOfSubSections;
    }

    public String toString() {
        return name;
    }
}
