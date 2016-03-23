package com.hobbyte.touringandroid.tourdata;

import java.util.ArrayList;

/**
 * Container for an individual part of a POI (text, image, etc) that gets used in the UI.
 */
public class ListViewItem {

    private String text;
    private String url;
    private int type;
    private ArrayList<String> option;
    private int solution;

    /**
     * Create an item for a certain custom listView
     *
     * @param text     The body text or the main text of the type of content
     * @param type     Type of content, Header, Body, Image, Video or Quiz
     * @param url      The url of a video or image if one exists
     * @param option   The options available for a quiz if one exists
     * @param solution The solution to the quiz if one exists
     */
    public ListViewItem(String text, int type, String url, ArrayList<String> option, int solution) {
        this.text = text;
        this.type = type;
        this.url = url;
        this.option = option;
        this.solution = solution;
    }

    /**
     * returns the main body of text of a pointofinterest
     *
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * The type of content - Header, Body, Image, Video or Quiz
     *
     * @return Type of content of this item
     */
    public int getType() {
        return type;
    }

    /**
     * Return the url of the listviewitem if one exists
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * The options available for a quiz if such exist
     *
     * @return the options for a quiz
     */
    public ArrayList<String> getOption() {
        return option;
    }

    /**
     * Returns solution of the quiz
     *
     * @return Solutions to the quiz
     */
    public int getSolution() {
        return solution;
    }
}
