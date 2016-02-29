//
//  tourDataParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class tourDataParser{
    
    
    init(){ }
    
    private func createNewTour(data: NSDictionary) ->tourSection{
        
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
            let toplevelTour = tourSection.init(sectionId: data["objectId"] as! String,
                description: data["description"] as! String,
                createdAt: data["createdAt"] as! String,
                subsections: [NSArray](),
                pointsOfInterest: poisToAdd,
                title: data["title"] as! NSString)
            
            return toplevelTour
        }
        
    }
    
    func saveTourSection(data: NSDictionary){
        let key = data["objectId"] as! String
        NSUserDefaults.standardUserDefaults().setObject(data, forKey: key)
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        //Makes sure that all sections are recursivley downloaded.
        
        createNewTour(data).downloadPOIcontent()
    }
    
    func getTourSection(objectId: String)-> tourSection{
        let data = NSUserDefaults.standardUserDefaults().objectForKey(objectId) as! NSDictionary
        return createNewTour(data)
    }
    
    func saveNewTour(data: NSDictionary){
        saveTourSection(data)
        //Saves the top level tour which maps to the key stored in the metadata.
        let tourTopLevelSection = data["sections"]![0] as! NSDictionary
        let tourSubsections = tourTopLevelSection["subsections"] as! NSArray
        self.saveTourSection(tourTopLevelSection )
        self.saveSubsections(tourSubsections)
    }
    
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
    
    func deleteTourSection(){
        //this will be complicated, so will do it later
    }
    
}
