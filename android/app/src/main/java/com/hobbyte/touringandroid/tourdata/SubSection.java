package com.hobbyte.touringandroid.tourdata;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Backend representation of a SubSection. It has fields for its title, its parent SubSection, as
 * well as any child subsections or points of interest that it may contain. This class implements
 * Parcelable so that SubSection instances can be passed from TourActivity to a SectionFragment
 * instance.
 */
public class SubSection implements Parcelable {
    
    private final String title;
    private final SubSection parent;
    
    private SubSection[] subSections = null;
    private PointOfInterest[] POIs = null;
    
    public SubSection(String title, SubSection parent) {
        this.title = title;
        this.parent = parent;
    }

    public SubSection(Parcel in) {
        title = in.readString();
        parent = (SubSection) in.readValue(SubSection.class.getClassLoader());
    }

    public void addSubSection(SubSection subSection, int i) {
        subSections[i] = subSection;
    }

    public void addPOI(PointOfInterest poi, int i) {
        POIs[i] = poi;
    }

    public void initSubSections(int length) {
        subSections = new SubSection[length];
    }

    public void initPOIs(int length) {
        POIs = new PointOfInterest[length];
    }

    public String getTitle() {
        return title;
    }

    public SubSection getParent() {
        return parent;
    }

    public SubSection[] getSubSections() {
        return subSections;
    }

    public PointOfInterest[] getPOIs() {
        return POIs;
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
        return title;
    }
}
