package com.hobbyte.touringandroid.tourdata;

/**
 * Created by Nikita on 08/02/2016.
 */
public class PointOfInterest {

    private final String title;
    private final String objectID;
    private final SubSection parent;

    public PointOfInterest(SubSection parent, String title, String objectID) {
        this.parent = parent;
        this.title = title;
        this.objectID = objectID;
    }

    public String getTitle() {
        return title;
    }

    public String getObjectID() {
        return objectID;
    }

    public SubSection getParent() {
        return parent;
    }
}
