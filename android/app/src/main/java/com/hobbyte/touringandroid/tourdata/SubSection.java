package com.hobbyte.touringandroid.tourdata;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Backend representation of a SubSection. It has fields for its title, its parent SubSection, as
 * well as any child subsections or points of interest that it may contain. This class implements
 * Parcelable so that SubSection instances can be passed from TourActivity to a SectionFragment
 * instance.
 */
public class SubSection extends TourItem implements Parcelable {
    
    private final String title;
    private final int numSubSections;
    private final SubSection parent;

    private ArrayList<TourItem> contents = new ArrayList<>();
    
    public SubSection(SubSection parent, String title, String objectID, int numSubSections) {
        super(objectID);
        this.title = title;
        this.parent = parent;
        this.numSubSections = numSubSections;
    }

    public SubSection(Parcel in) {
        super(in.readString());
        title = in.readString();
        numSubSections = in.readInt();
        parent = (SubSection) in.readValue(SubSection.class.getClassLoader());
    }

    public void addItem(TourItem item) {
        contents.add(item);
    }

    public ArrayList<TourItem> getContents() {
        return contents;
    }

    protected PointOfInterest nextPOI(int i) {
        return (PointOfInterest) contents.get(numSubSections + i);
    }

    public String getTitle() {
        return title;
    }

    public SubSection getParent() {
        return parent;
    }

    public String getObjectID() {
        return objectID;
    }

    public int getType() {
        return TourItem.TYPE_SUBSECTION;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectID);
        dest.writeString(title);
        dest.writeInt(numSubSections);
        dest.writeValue(parent);
    }

    public static final Parcelable.Creator<SubSection> CREATOR
            = new Parcelable.Creator<SubSection>() {
        @Override
        public SubSection createFromParcel(Parcel in) {
            return new SubSection(in);
        }

        @Override
        public SubSection[] newArray(int size) {
            return new SubSection[size];
        }
    };

    @Override
    public String toString() {
        return String.format("S: %s (%d)", title, numSubSections);
    }
}
