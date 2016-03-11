package com.hobbyte.touringandroid.tourdata;

/**
 * Created by Nikita on 15/02/2016.
 */
public class ListViewItem {

    private String text;
    private String url;
    private int type;

    public ListViewItem(String text, int type, String url) {
        this.text = text;
        this.type = type;
        this.url = url;
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
    /*public void setText(String text) {
        this.text = text;
    }*/


    /*public void setType(int type) {
        this.type = type;
    }*/
}
