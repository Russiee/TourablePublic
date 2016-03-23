package com.hobbyte.touringandroid.tourdata;

/**
 * Contains the structure of a tour. The root {@link SubSection} holds references to all other
 * {@link SubSection}s and {@link PointOfInterest}s. It is essentially a Tree structure.
 */
public class Tour {

    private final SubSection root;

    public Tour(SubSection root) {
        this.root = root;
    }

    public SubSection getRoot() {
        return root;
    }
}