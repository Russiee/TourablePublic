package com.hobbyte.touringandroid.tourdata;

/**
 * @author Nikita
 */
public class SubSection {
    
    private final String title;
    private final SubSection parent;
    
    private SubSection[] subSections = null;
    private PointOfInterest[] POIs = null;
    
    public SubSection(String title, SubSection parent) {
        this.title = title;
        this.parent = parent;
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
}
