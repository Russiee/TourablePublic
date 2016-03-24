//
//  tourDataParser.swift
//  Tourable
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation
///TourDataParser class is responsible for the creation of new tours, saving new tours/sections/subsections to NSUserDefaults and for the retrieval of tour sections from NSUserDefaults.
class tourDataParser{
    
    
    init(){ }
    
     func createNewTour(data: NSDictionary) ->tourSection{

        //pois might not exist, for example if it is the highest level
        var poisToAdd = [NSArray]()
        if let pois = data["pois"]{
            poisToAdd = pois as! [NSArray]
        }
        let keys = data.allKeys as! [String]
        if keys.contains("subsections") {

            let realSubs = data["subsections"]

            let toplevelTour = tourSection.init(sectionId: data["objectId"] as! String,
                description: data["description"] as! String,
                createdAt: data["createdAt"] as! String,
                subsections: realSubs as! NSArray,
                pointsOfInterest: poisToAdd,
                title: data["title"] as! NSString)
            
            return toplevelTour
            
        }else if keys.contains("sections"){
            let toplevelTour = tourSection.init(sectionId: data["objectId"] as! String,
                description: data["description"] as! String,
                createdAt: data["createdAt"] as! String,
                subsections: data["sections"] as! NSArray,
                pointsOfInterest: poisToAdd,
                title: data["title"] as! NSString)
            
            return toplevelTour
        }else{
            print(data)
            let toplevelTour = tourSection.init(sectionId: data["objectId"] as! String,
                description: data["description"] as! String,
                createdAt: data["createdAt"] as! String,
                subsections: [NSArray](),
                pointsOfInterest: poisToAdd,
                title: data["title"] as! NSString)
            
            return toplevelTour
        }
    }

    ///Takes as parameter a NSDictionary expecting the tour section data that is going to be saved into NSUserDefauls with its objectId as the key. Gets any POI data associated with section.
    func saveTourSection(data: NSDictionary){
        let key = data["objectId"] as! String
        NSUserDefaults.standardUserDefaults().setObject(data, forKey: key)
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        //Makes sure that all sections are recursivley downloaded.
        
       let returnValue = createNewTour(data)
        returnValue.downloadPOIcontent()
     
    }

    ///Takes as parameter a String expecting an objectId and will return a tour section saved with that objectId from NSUserDefaults.
    func getTourSection(objectId: String)-> tourSection{
        let data = NSUserDefaults.standardUserDefaults().objectForKey(objectId) as! NSDictionary
        let returnVlaue = createNewTour(data)
       
        return returnVlaue
    }

    ///Takes as parameter a NSDictionary and saves tours sections and subsections using saveTourSection and saveSubsections.
    func saveNewTour(data: NSDictionary){
        saveTourSection(data)
        
        //Saves the top level tour which maps to the key stored in the metadata.
        for tourTopLevelSection in data["sections"] as! NSArray{
            let topSection = tourTopLevelSection as! NSDictionary
            if topSection["subsections"] != nil{
                let tourSubsections = tourTopLevelSection["subsections"] as! NSArray
                self.saveSubsections(tourSubsections)
            }
            self.saveTourSection(tourTopLevelSection as! NSDictionary)
        }
    }

    ///Takes as paramter a NSArray expecting the array of tours subsections. Saves them recursively.
    func saveSubsections(tourSubsections: NSArray){
        
        for section in tourSubsections{
            
            if let NextLevelSubsections = section["subsections"]{
                if NextLevelSubsections != nil{
                    self.saveSubsections(NextLevelSubsections as! NSArray)
                    
                }
            }else  if let NextLevelSubsections = section["sections"]{
                if NextLevelSubsections != nil{
                    self.saveSubsections(NextLevelSubsections as! NSArray)
                }
            }
            self.saveTourSection(section as! NSDictionary)
        }
    }
}
