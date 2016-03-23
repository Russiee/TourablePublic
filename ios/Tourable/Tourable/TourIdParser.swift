//
//  tourIdParser.swift
//  Tourable
//
//  Created by Alex Gubbay on 09/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

let TableUpdateNotificationKey = "tableAddWasComplete"
///TourIdParser class is responsible for parsing the tourIds. Adds tour metadata,g ets tour meta data, updates our "Array", deletes tours from "Array", saves tours to "Array", get all tour IDs and get all tour Titles.
public class TourIdParser {
    
     static let sharedInstance = TourIdParser()
    
    ///Takes as parameter the position of tour to delete. Deletes it from NSUserDefaults "Array"
    func deleteTourIdAtRow(row: Int) {
        //remove from "Array"
        var newArray : [AnyObject] = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! [[String: String]]
        //remove from Metadata
        newArray.removeAtIndex(row)
        saveArray(newArray)
    }
   
    
    ///Takes as parameter the tourID and tourTitle as string. Updates the "Array" and saves the key value pair in NSUserDefaults using the saveArray method.
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
    
    ///Takes as parameter the JSON received from APIConnector. Adds the metadata into NSUserDefauls, after tuurning it into a dictionary that can be retrieved with the tour Id as the key.
    func addTourMetaData(metadata: NSDictionary){

        let tourCode = metadata["code"]!

        let tourDict = metadata["tour"] as! NSMutableDictionary
        tourDict["code"] = metadata["code"]
        tourDict["expiry"] = metadata["expiry"]
        tourDict["updatedAt"] = metadata["updatedAt"]
        tourDict["createdAt"] = metadata["createdAt"]

        NSUserDefaults.standardUserDefaults().setObject(tourDict, forKey: tourCode as! String)
        NSUserDefaults.standardUserDefaults().synchronize()

        TourMetadataConnector().downloadTourUpdateMetadata(tourDict["objectId"] as! String, tourCode: tourCode as! String)

        //this comes from the initialised of bundle Connector
        let bundleRoute = bundleRouteConnector()
        bundleRoute.initiateBundleConnection(tourDict["objectId"] as! String)

        let tourData = bundleRoute.getJSONResult()
        tourDataParser().saveNewTour(tourData)
        let tourTitle = tourData["title"]
        self.updateArray(tourCode as! String, tourTitle: tourTitle as! String)

        NSNotificationCenter.defaultCenter().addObserver (
            self,
            selector: "TableChanged:",
            name: "TabledDataChanged",
            object: nil
        )
    }

    ///Takes as parameter the tourId and return the metaData stored in NSUserDefaults.
    func getTourMetadata(tourCode: String) -> Dictionary<String,AnyObject> {
        let result = NSUserDefaults.standardUserDefaults().objectForKey(tourCode)
        if result != nil{
             return result as! [String : AnyObject]
        }else{
            return Dictionary<String,AnyObject>()
        }
        
    }

    //Notifies observers that the table of tour Ids has been updated.
    func notify() {
        NSNotificationCenter.defaultCenter().postNotificationName(TableUpdateNotificationKey, object: self)
    }

    ///Return an array of the tour titles that are currently in "Array". Used for checking updates to the mainTableView.
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
    
    ///Return an array of the tour Ids that are currently in "Array". Used for checking updates to the mainTableView.
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