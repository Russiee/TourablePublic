package com.hobbyte.touringandroid.tourdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Backend representation of a POI. It has fields for its title, objectId (from the JSON), and its
 * parent SubSection. This class implements Parcelable so that PointOfInterest instances can be
 * passed from TourActivity to a SectionFragment instance.
 */
public class PointOfInterest extends TourItem implements Parcelable {

    private final String title;
    private final SubSection parent;

    /** The index of the following POI in the parent's list of POIs. */
    private final int nextIndex;

    public PointOfInterest(SubSection parent, String title, String objectID, int nextIndex) {
        super(objectID);
        this.title = title;
        this.nextIndex = nextIndex;
        this.parent = parent;
    }

    /**
     * Get the SubSection's title, as described in the JSON.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get this SubSection's objectId, as described in the JSON.
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * Get this object's parent section.
     */
    public SubSection getParent() {
        return parent;
    }

    /**
     * Used when casting {@link TourItem} objects.
     */
    public int getType() {
        return TourItem.TYPE_POI;
    }

    /**
     * Used in a {@link com.hobbyte.touringandroid.ui.fragment.POIFragment} to navigate to the next
     * point of interest. {@link #nextIndex} will be -1 if this is the final POI in the section.
     */
    public PointOfInterest getNextPOI() {
        if (nextIndex == -1) return null;

        return parent.getPOI(nextIndex);
    }

    // currently used for debugging purposes
    @Override
    public String toString() {
        return String.format("P: %s (%d)", title, nextIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointOfInterest)) return false;

        PointOfInterest poi = (PointOfInterest) o;

        if (!objectID.equals(poi.objectID) || !title.equals(poi.title) ||
                nextIndex != poi.nextIndex || !parent.equals(poi.parent)) {
            return false;
        }

        return true;
    }

    /* ======================================================
    *          STUFF FOR PARCELABLE
    *  ======================================================*/

    public PointOfInterest(Parcel in) {
        super(in.readString());
        title = in.readString();
        nextIndex = in.readInt();
        parent = (SubSection) in.readValue(SubSection.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectID);
        dest.writeString(title);
        dest.writeInt(nextIndex);
        dest.writeValue(parent);
    }

    public static final Parcelable.Creator<PointOfInterest> CREATOR
            = new Parcelable.Creator<PointOfInterest>() {
        @Override
        public PointOfInterest createFromParcel(Parcel in) {
            return new PointOfInterest(in);
        }

        @Override
        public PointOfInterest[] newArray(int size) {
            return new PointOfInterest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
