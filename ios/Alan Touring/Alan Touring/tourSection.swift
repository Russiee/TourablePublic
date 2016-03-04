//
//  tour.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

//Class reprisenting a subsection in the tour. Repsonsible for triggering the
//download of its contents
class tourSection{
    //Unique id of section
    var sectionId: String
    //Section description
    var description: String
    //DateTime of creation
    var createdAt: String
    //All subsection pointers
    var subsections: NSArray
    //All point of interest pointers
    var pointsOfInterest: NSArray
    //Title of section
    var title: NSString
    
    //Constructor
    init(sectionId: String, description: String, createdAt: String, subsections:NSArray, pointsOfInterest:NSArray, title:NSString){
        self.sectionId = sectionId
        self.description = description
        self.createdAt = createdAt
        self.subsections = subsections
        self.pointsOfInterest = pointsOfInterest
        self.title = title
    }

    // download the content for all the POI (called when you save a tour section)
    func downloadPOIcontent() {
        for poi in pointsOfInterest{
            //Dont move this, for god sake.
            let poic = POIConnector.init()
            let objectId = poi["objectId"] as! String
            poic.initateConnection(objectId)
        }
    }
    
    //Prints tour section object for debugging
    func debugDataPass(){
        print("--------")
        print(sectionId)
        print(description)
        print(createdAt)
       
        print(pointsOfInterest)
        print(title)
        print("--------")
       
    }
    
    //Endless getters for tour section properties
    
    func getSectionId() -> String{
        return self.sectionId
    }
    func getDescription() -> String{
        return self.description
    }
    func getCreatedAt() -> String{
        return self.createdAt
    }
    func getSubSections() -> NSArray{
        return self.subsections
        
    }
    func getPointsOfInterest() -> NSArray{
        return self.pointsOfInterest
        
    }
    
}


