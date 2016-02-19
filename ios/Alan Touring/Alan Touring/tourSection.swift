//
//  tour.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class tourSection{
    var sectionId: String
    var description: String
    var createdAt: String
    var superSection: NSDictionary
    var subSections: NSArray
    var pointsOfInterest: NSArray
    
    init(sectionId: String, description: String, createdAt: String, superSection: NSDictionary, subSections:NSArray, pointsOfInterest:NSArray){
        self.sectionId = sectionId
        self.description = description
        self.createdAt = createdAt
        self.superSection = superSection
        self.subSections = subSections;
        self.pointsOfInterest = pointsOfInterest;
        //self.triggerRecursion()
        //self.debugDataPass()
    }
    //Recursivley create all other required objects
    func triggerRecursion(){
        
    }
    func debugDataPass(){
        print(sectionId)
        print(description)
        print(createdAt)
        print(superSection)
        print(subSections)
        print(pointsOfInterest)
       
    }
    
    
    
   }


