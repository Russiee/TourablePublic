package com.hobbyte.touringandroid.tourdata;

import java.util.ArrayList;

/**
 * Created by Nikita on 15/02/2016.
 */
public class ListViewItem {

    private String text;
    private String url;
    private int type;
    private ArrayList<String> option;
    private int solution;

    public ListViewItem(String text, int type, String url, ArrayList<String> option, int solution) {
        this.text = text;
        this.type = type;
        this.url = url;
        this.option = option;
        this.solution = solution;
    }

    public String getText() {
        return text;
    }

    public int getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

    public ArrayList<String> getOption() { return option; }

    public int getSolution() { return solution; }
    /*public void setText(String text) {
        this.text = text;
    }*/


    /*public void setType(int type) {
        this.type = type;
    }*/
}
