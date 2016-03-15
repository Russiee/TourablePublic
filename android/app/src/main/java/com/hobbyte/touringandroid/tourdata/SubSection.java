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
    private final String description;
    private final SubSection parent;

    /** This lets the object skip over any SubSections in `contents`. See {@link #getPOI(int)}*/
    private final int numSubSections;

    /** Holds all child SubSections and POIs. From the way the JSON is parsed, this will be filled
     *  with SubSections first, and POIs after. */
    private ArrayList<TourItem> contents = new ArrayList<>();
    
    public SubSection(SubSection parent, String title, String description, String objectID, int numSubSections) {
        super(objectID);
        this.title = title;
        this.description = description;
        this.parent = parent;
        this.numSubSections = numSubSections;
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
     * Returns the {@link PointOfInterest} at a given index, relative to the first POI in
     * {@link #contents}.
     */
    public PointOfInterest getPOI(int i) {
        return (PointOfInterest) contents.get(numSubSections + i);
    }

    /**
     * Returns the {@link SubSection} at a given index. Will throw an error if `i` is a legal index
     * but points to what is actually a {@link PointOfInterest}.
     */
    public SubSection getSubSection(int i) throws ArrayIndexOutOfBoundsException {
        if (i >= numSubSections && i < contents.size()) throw new ArrayIndexOutOfBoundsException(
                "There are only " + numSubSections + " in this SubSection!"
        );

        return (SubSection) contents.get(i);
    }

    /**
     * Get the SubSection's title, as described in the JSON.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the SubSection's description, as described in the JSON.
     */
    public String getDescription() { return description; }

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

    // currently used for debugging purposes
    @Override
    public String toString() {
        return title;
    }

    /**
     * Checks for equality between two SubSection objects. Note that this <b>does not</b> check for
     * equality between {@link #contents}. This is intentional, and done because implementing
     * {@link Parcelable} on an abstract class is a) hard and b) not needed for our purposes.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubSection)) return false;

        SubSection s = (SubSection) o;

        if (!objectID.equals(s.objectID) || !title.equals(s.title) ||
                numSubSections != s.numSubSections || parent != s.parent) {
            return false;
        }

        return true;
    }

    /* ======================================================
    *          STUFF FOR PARCELABLE
    *  ======================================================*/

    public SubSection(Parcel in) {
        super(in.readString());
        title = in.readString();
        description = in.readString();
        numSubSections = in.readInt();
        parent = (SubSection) in.readValue(SubSection.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(objectID);
        dest.writeString(title);
        dest.writeString(description);
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
    public int describeContents() {
        return 0;
    }
}
