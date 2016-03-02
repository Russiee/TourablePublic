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
    private final int nextIndex;
    private final SubSection parent;

    public PointOfInterest(SubSection parent, String title, String objectID, int nextIndex) {
        super(objectID);
        this.title = title;
        this.nextIndex = nextIndex;
        this.parent = parent;
    }

    public PointOfInterest(Parcel in) {
        super(in.readString());
        title = in.readString();
        nextIndex = in.readInt();
        parent = (SubSection) in.readValue(SubSection.class.getClassLoader());
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

    public int getType() {
        return TourItem.TYPE_POI;
    }

    public PointOfInterest getNextPOI() {
        if (nextIndex == -1) return null;

        return parent.nextPOI(nextIndex);
    }

    @Override
    public int describeContents() {
        return 0;
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
    public String toString() {
        return String.format("P: %s (%d)", title, nextIndex);
    }
}
