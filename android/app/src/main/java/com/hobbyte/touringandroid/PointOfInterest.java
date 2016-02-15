package com.hobbyte.touringandroid;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Nikita on 08/02/2016.
 */
public class PointOfInterest implements Serializable{

    //Implements serializable for easy of transfer in intents
    private String name;
    private String description;
    private String header;
    private String body;
    private String url;
    private String urlDescription;
    private ArrayList<String> content;

    /**
     * Create a Point Of Interest based on it's name, description and specific information strings already prepared
     * containing the format of:
     * Header
     * Body
     * URL
     * URL Description
     * @param name
     * @param description
     * @param header
     * @param body
     * @param url
     * @param urlDescription
     */
    public PointOfInterest(String name, String description, String header, String body, String url, String urlDescription) {
        this.description = description;
        this.name = name;
        this.header = header;
        this.body = body;
        this.url = url;
        this.urlDescription = urlDescription;
        content = new ArrayList<>();
        content.add(header);
        content.add(body);
        content.add(url);
        content.add(urlDescription);
    }

    /**
     * Creates a very rough tester to see if POI implements correctly
     */
    public PointOfInterest() {
        this.name = "Temp POI Name";
        this.description = "Temp POI Description";
        this.header = "Hello i'm a header";
        this.body = "Hello, this is body?";
        this.url = "http://i.imgur.com/Ucwvp1x.jpg";
        this.urlDescription = "#soReal";
    }

    /**
     * Returns a string with information line by line.
     * @return string containing information line by line
     */
    public String toString() {
        return header + "\n\n" + body + "\n\n" + url + "\n\n" + urlDescription;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getContent() {
        return content;
    }
}
