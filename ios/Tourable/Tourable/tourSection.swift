//
//  tour.swift
//  Tourable
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
            let poic = POIConnector()
            let objectId = poi["objectId"] as! String
            poic.initateConnection(objectId)
        }
        if pointsOfInterest.count == 0{
            let poic = POIConnector()
            poic.noContent()
        }
    }
}


