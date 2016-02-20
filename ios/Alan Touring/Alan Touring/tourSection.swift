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
    //Supersection pointer for the section. will be empty for the top level
    var superSection: NSDictionary
    //All subsection pointers
    var subSections: NSArray
    //All point of interest pointers
    var pointsOfInterest: NSArray
    //Title of section
    var title: NSString
    
    //Constructor
    init(sectionId: String, description: String, createdAt: String, superSection: NSDictionary, subSections:NSArray, pointsOfInterest:NSArray, title:NSString){
        self.sectionId = sectionId
        self.description = description
        self.createdAt = createdAt
        self.superSection = superSection
        self.subSections = subSections
        self.pointsOfInterest = pointsOfInterest
        self.title = title


    }
    
    //Recursivley create all other required objects
    //Should only be called once, externally
    
    func triggerRecursion(){
       // print("recursion on \(sectionId) called")
        
        for section in subSections{
            let brp = bundleRouteConnector.init()

            let objectId = section["objectId"] as! String
            
            brp.initateConnection(objectId)
        }
        
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
        print(superSection)
        print(subSections)
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
    func getSuperSection() -> NSDictionary{
        return self.superSection
    }
    func getSubSections() -> NSArray{
        return self.subSections
        
    }
    func getPointsOfInterest() -> NSArray{
        return self.pointsOfInterest
        
    }
    
    
    
   }


