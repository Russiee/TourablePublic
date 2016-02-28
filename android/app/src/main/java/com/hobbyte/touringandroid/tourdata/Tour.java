package com.hobbyte.touringandroid.tourdata;

/**
 * @author Nikita
 *
 * Holds all data about a tour
 */
public class Tour {

    private final SubSection root;
    private SubSection currentSection;

    public Tour(SubSection root) {
        this.root = root;
        currentSection = root;
    }

    public SubSection getRoot() {
        return root;
    }

    public SubSection getCurrentSection() {
        return currentSection;
    }
}