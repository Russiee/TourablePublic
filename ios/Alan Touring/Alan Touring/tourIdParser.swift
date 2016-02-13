//
//  tourIdParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 09/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

let TableUpdateNotificationKey = "tableAddWasComplete"

public class tourIdParser {

    var API = ApiConnector.init()
    
    
    public init(){
        //If the app is launched for the first time, a new empty array is created for the 
        //tour ids.
        if(NSUserDefaults.standardUserDefaults().stringArrayForKey("Array")==nil){
            let newArray = [AnyObject]()
            self.saveArray(newArray)
        }
    }
    
   public func deleteTourIdAtRow(row:Int){
        var newArray : [AnyObject] = NSUserDefaults.standardUserDefaults().objectForKey("Array")! as AnyObject as! [AnyObject]
        NSUserDefaults.standardUserDefaults().removeObjectForKey(newArray[row] as! String)
        newArray.removeAtIndex(row)
        saveArray(newArray)
    }
   
    
    //Adds a new tourId to the array
    public func updateArray(tourId: String){
        //Duplicates the array, creating a mutable version that the new tourId can be added to.
        var newArray : [AnyObject] = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! NSMutableArray as [AnyObject]
        newArray.append(tourId)
        print("Update Array called now saving changes")
        saveArray(newArray)
    }
    
    
    
    //saves the copy of the array passed to it persistently and updates working copy kept in this class.
    //Never called outside of this class
    private func saveArray(newArray: AnyObject){
        //Stores the Array in NSUserDefaults, overwriting existing copy
        NSUserDefaults.standardUserDefaults().setObject(newArray, forKey: "Array")
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        NSUserDefaults.standardUserDefaults().synchronize()

        notify()
    }
    
    //Adds the metadata passed to it into the cache, after turning it into a dictonary that can be retrieved 
    // from the cache with its tour Id code
    func addTourMetaData(metadata: NSArray){

        let keys = ["code","createdAt","expiresAt","objectId","tour","updatedAt"]
        var dict = metadata.dictionaryWithValuesForKeys(keys)
        let tourCode = dict["code"]!

        let metadataDict = dict as NSDictionary
        NSUserDefaults.standardUserDefaults().setObject(metadataDict, forKey: tourCode[0] as! String)
        NSUserDefaults.standardUserDefaults().synchronize()
        print("here")
        self.updateArray(tourCode[0] as! String)
        
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "TableChanged:",
            name: "TabledDataChanged",
            object: nil
        )
    }
    
    //Gets the dictonary from the cache with the tour code passed to it
    func getTourMetadata(tourCode: String) -> Dictionary<String,AnyObject>{
       return NSUserDefaults.standardUserDefaults().objectForKey(tourCode) as! Dictionary<String,AnyObject>
    }
    //Notifies observers that the table of tour Ids has been updated.
    func notify() {
        NSNotificationCenter.defaultCenter().postNotificationName(TableUpdateNotificationKey, object: self)
        print("notify called")
    }
    
    //method for getting tourIds that have been added for checking the table updates.
    public func getAllTours() -> NSMutableArray {
        return NSUserDefaults.standardUserDefaults().objectForKey("Array") as! NSMutableArray
    }
}