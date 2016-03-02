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
    private final SubSection parent;

    /** This lets the object skip over any SubSections in `contents`. See {@link #nextPOI(int)}*/
    private final int numSubSections;

    /** Holds all child SubSections and POIs. From the way the JSON is parsed, this will be filled
     *  with SubSections first, and POIs after. */
    private ArrayList<TourItem> contents = new ArrayList<>();
    
    public SubSection(SubSection parent, String title, String objectID, int numSubSections) {
        super(objectID);
        this.title = title;
        this.parent = parent;
        this.numSubSections = numSubSections;
    }

    // needed for Parcelable
    public SubSection(Parcel in) {
        super(in.readString());
        title = in.readString();
        numSubSections = in.readInt();
        parent = (SubSection) in.readValue(SubSection.class.getClassLoader());
    }

    /**
     * Add a {@link SubSection} or {@link PointOfInterest} to {@link #contents}.
     */
    public void addItem(TourItem item) {
        contents.add(item);
    }

    /**
     * @return the list of this object's {@link SubSection}s and {@link PointOfInterest}s.
     */
    public ArrayList<TourItem> getContents() {
        return contents;
    }

    /**
     * Used by a {@link PointOfInterest} in a {@link com.hobbyte.touringandroid.ui.fragment.POIFragment}
     * to navigate to the next point of interest.
     *
     * @param i the index of the next POI. See {@link PointOfInterest#getNextPOI()}.
     */
    protected PointOfInterest nextPOI(int i) {
        return (PointOfInterest) contents.get(numSubSections + i);
    }

    /**
     * Get the SubSection's title, as described in the JSON.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get this object's parent section.
     */
    public SubSection getParent() {
        return parent;
    }

    /**
     * Get this SubSection's objectId, as described in the JSON.
     */
    public String getObjectID() {
        return objectID;
    }

    /**
     * Used when casting {@link TourItem} objects.
     */
    public int getType() {
        return TourItem.TYPE_SUBSECTION;
    }

    // needed for Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // needed for Parcelable
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectID);
        dest.writeString(title);
        dest.writeInt(numSubSections);
        dest.writeValue(parent);
    }

    // needed for Parcelable
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

    // currently used for debugging purposes
    @Override
    public String toString() {
        return String.format("S: %s (%d)", title, numSubSections);
    }
}
