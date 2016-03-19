//
//  tourIdParser.swift
//  Tourable
//
//  Created by Alex Gubbay on 09/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

let TableUpdateNotificationKey = "tableAddWasComplete"

public class TourIdParser {

    //making the TourIdParser a singleton to parse all tours from the API
    //in order to access TourIdParser methods call TourIdParser.shardInstance.METHOD()
    
    //static let sharedInstance = TourIdParser()
    
    class var sharedInstance: TourIdParser {
        struct Static {
            static var onceToken: dispatch_once_t = 0
            static var instance: TourIdParser? = nil
        }
        dispatch_once(&Static.onceToken) {
            Static.instance = TourIdParser()
  
        }
        return Static.instance!
    }

    func deleteTourIdAtRow(row: Int) {
        //remove from "Array"
        var newArray : [AnyObject] = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! [[String: String]]
        //remove from Metadata
        newArray.removeAtIndex(row)
        saveArray(newArray)
    }
   
    
    //Adds a new tourId to the array
    func updateArray(tourId: String, tourTitle: String){
        //Duplicates the array, creating a mutable version that the new tourId can be added to.
        var newArray : [AnyObject] = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! [[String: String]]
        newArray.append([tourTitle : tourId])
        saveArray(newArray)
    }
    
    
    //saves the copy of the array passed to it persistently and updates working copy kept in this class.
    //Never called outside of this class
    private func saveArray(newArray: AnyObject){
        //Stores the Array in NSUserDefaults, overwriting existing copy
        NSUserDefaults.standardUserDefaults().setObject(newArray, forKey: "Array")
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        notify()
    }
    
    //Adds the metadata passed to it into the cache, after turning it into a dictonary that can be retrieved 
    // from the cache with its tour Id code
    func addTourMetaData(metadata: NSDictionary){
        let tourCode = metadata["code"]!

        let tourDict = metadata["tour"] as! NSMutableDictionary
        tourDict["expiresAt"] = metadata["expiresAt"]
        tourDict["updatedAt"] = metadata["updatedAt"]
        tourDict["createdAt"] = metadata["createdAt"]

        NSUserDefaults.standardUserDefaults().setObject(tourDict, forKey: tourCode as! String)
        NSUserDefaults.standardUserDefaults().synchronize()

        //Give objectId of tour as param


        //this comes from the initialised of bundle Connector
        let bundleRoute = bundleRouteConnector()
        bundleRoute.startConnection(tourDict["objectId"] as! String)
        
        let tourData = bundleRoute.getJSONResult()
        tourDataParser().saveNewTour(tourData)
        //bundleRoute.getAllPOIs((MYDAMNDATA["sections"]) as! NSArray)
        let tourTitle = tourData["title"]
        self.updateArray(tourCode as! String, tourTitle: tourTitle as! String)

        NSNotificationCenter.defaultCenter().addObserver (
            self,
            selector: "TableChanged:",
            name: "TabledDataChanged",
            object: nil
        )
    }

    //Gets the dictonary from the cache with the tour code passed to it
    func getTourMetadata(tourCode: String) -> Dictionary<String,AnyObject> {
        return NSUserDefaults.standardUserDefaults().objectForKey(tourCode) as! [String : AnyObject]
    }

    //Notifies observers that the table of tour Ids has been updated.
    func notify() {
        NSNotificationCenter.defaultCenter().postNotificationName(TableUpdateNotificationKey, object: self)
    }

    //method for getting tourIds that have been added for checking the table updates.
    public func getAllTours() -> [String] {
        let tours = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! NSMutableArray
        var tourTitles = [String]()
        if(tours.count != 0){
        for tour in tours{
            tourTitles.append(tour.allKeys[0] as! String)
        }
        }
        return tourTitles
    }
    
    public func getAllTourIDs() -> [String] {
        let tours = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! NSMutableArray
        var tourIDs = [String]()
        if(tours.count != 0){
        for tour in tours{
            let key = tour.allKeys[0] as! String
            tourIDs.append(tour[key] as! String)
        }
        }
        return tourIDs
    }
    
    
}