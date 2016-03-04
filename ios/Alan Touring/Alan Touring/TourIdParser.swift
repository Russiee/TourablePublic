//
//  tourIdParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 09/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

let TableUpdateNotificationKey = "tableAddWasComplete"

public class TourIdParser {

    var API = ApiConnector()

    //making the TourIdParser a singleton to parse all tours from the API
    //in order to access TourIdParser methods call TourIdParser.shardInstance.METHOD()
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
        var newArray : [AnyObject] = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! [AnyObject]
        NSUserDefaults.standardUserDefaults().removeObjectForKey(newArray[row] as! String)
        
        //remove from Metadata
        NSUserDefaults.standardUserDefaults().removeObjectForKey(newArray[row] as! String)
        NSUserDefaults.standardUserDefaults().synchronize()
        
        newArray.removeAtIndex(row)
        saveArray(newArray)
    }
   
    
    //Adds a new tourId to the array
    func updateArray(tourId: String){
        //Duplicates the array, creating a mutable version that the new tourId can be added to.
        var newArray : [AnyObject] = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! NSMutableArray as [AnyObject]
        newArray.append(tourId)
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

        let tourDict = metadata["tour"]
        let tourCode = metadata["code"]!

        NSUserDefaults.standardUserDefaults().setObject(tourDict, forKey: tourCode as! String)
        NSUserDefaults.standardUserDefaults().synchronize()

        self.updateArray(tourCode as! String)

        //Give objectId of tour as param


        //this comes from the initialised of bundle Connector
        let bundleRoute = bundleRouteConnector()
        
        bundleRoute.startConnection(tourDict!["objectId"] as! String)
        
        let MYDAMNDATA = bundleRoute.getJSONResult()
        tourDataParser().saveNewTour(MYDAMNDATA)
        bundleRoute.getAllPOIs((MYDAMNDATA["sections"]) as! NSArray)
        NSUserDefaults.standardUserDefaults().setObject(bundleRoute.getPOIList(), forKey: "POIList")

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
    public func getAllTours() -> NSMutableArray {
        return NSUserDefaults.standardUserDefaults().objectForKey("Array") as! NSMutableArray
    }
}