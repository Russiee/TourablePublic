//
//  POIParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 20/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class POIParser{
    
    
    //creates and returns a pointOfInterest object from the JSON passed to it
    private func createNewPOI(data: NSDictionary)-> pointOfInterest {
        //Create and return the POI
        return  pointOfInterest(objectId: data["objectId"] as! String,
                description: data["description"] as! String,
                createdAt: data["createdAt"] as! String,
                post: data["post"] as! NSArray,
                section: data["section"] as! NSDictionary,
                title: data["title"] as! String,
                updatedAt: data["updatedAt"] as! String)
    }
    
    //Saves the JSON to the cache
    func savePOI(data: NSDictionary) {
        //Get the unique object id used as key for the cache
        let key = data["objectId"] as! String
        //Save the JSON under the key.
        NSUserDefaults.standardUserDefaults().setObject(data, forKey: key)
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        //triggers the saving of images to the cache
        self.createNewPOI(data).downloadContent()
    }
    
    //Retrives the JSON from the cache and returns a POI object created from it
    func getTourSection(objectId: String)-> pointOfInterest{
        print(objectId)
        //Get the JSON
        let data = NSUserDefaults.standardUserDefaults().objectForKey(objectId) as! NSDictionary
        //Create a poi object
        return createNewPOI(data)
        
    }
    
    func deletePOI(){
        //this will be complicated, so will do it later
    }
    
}