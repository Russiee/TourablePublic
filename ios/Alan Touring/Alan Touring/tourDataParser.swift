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
    
   private func createNewTour(data: NSDictionary) ->tourSection{
        
        let toplevelTour = tourSection.init(sectionId: data["objectId"] as! String,
            description: data["description"] as! String,
            createdAt: data["createdAt"] as! String,
            superSection: data["superSection"] as! NSDictionary,
            subSections: data["subsections"] as! NSArray,
            pointsOfInterest: data["pois"] as! NSArray,
            title: data["title"] as! NSString
    )
        
       return toplevelTour
        
    }
    
    func saveTourSection(data: NSDictionary){
       
        let key = data["objectId"] as! String
        
        NSUserDefaults.standardUserDefaults().setObject(data, forKey: key)
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        //Makes sure that all sections are recursivley downloaded.
        
        _ = createNewTour(data).triggerRecursion()
    }
    
    func getTourSection(objectId: String)-> tourSection{
      
        let data = NSUserDefaults.standardUserDefaults().objectForKey(objectId) as! NSDictionary
        return createNewTour(data)
        
    }
    
    func deleteTourSection(){
        //this will be complicated, so will do it later
    }
    
    
}
