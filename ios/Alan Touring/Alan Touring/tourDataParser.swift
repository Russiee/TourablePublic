//
//  tourDataParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class tourDataParser{
    
    init(){
        
    }
    
    func createNewTour(data: NSDictionary){
        
        var toplevelTour = tourSection.init(sectionId: data["objectId"] as! String,
            description: data["description"] as! String,
            createdAt: data["createdAt"] as! String,
            superSection: data["superSection"] as! NSDictionary,
            subSections: data["subsections"] as! NSArray,
            pointsOfInterest: data["pois"] as! NSArray )
        
        print("done")
        self.saveTourSection(toplevelTour)
        
    }
    func saveTourSection(tourToCache: tourSection){
        NSUserDefaults.standardUserDefaults().setObject(tourToCache, forKey: tourToCache.getSectionId())
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
    }
    func getTourSection(objectId: String)-> tourSection{
        return NSUserDefaults.standardUserDefaults().objectForKey(objectId) as! tourSection;
    }
    func deleteTourSection(){
        //this will be complicated, so will do it later
    }
    
    
}
