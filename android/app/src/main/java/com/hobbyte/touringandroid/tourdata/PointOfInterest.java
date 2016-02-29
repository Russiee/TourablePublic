package com.hobbyte.touringandroid.tourdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Backend representation of a POI. It has fields for its title, objectId (from the JSON), and its
 * parent SubSection. This class implements Parcelable so that PointOfInterest instances can be
 * passed from TourActivity to a SectionFragment instance.
 */
public class PointOfInterest implements Parcelable {

    private final String title;
    private final String objectID;
    private final SubSection parent;

    public PointOfInterest(SubSection parent, String title, String objectID) {
        this.parent = parent;
        this.title = title;
        this.objectID = objectID;
    }

    public PointOfInterest(Parcel in) {
        title = in.readString();
        objectID = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
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
        return title;
    }
}
