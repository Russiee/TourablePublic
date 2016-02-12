//
//  tourIdParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 09/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

public class tourIdParser {
    
    //READ ONLY. TO ADD ITEM USE updateArray()
   var tourIdContainer = NSMutableArray()
    var API = ApiConnector.init()
    
    
    
    public init(){
            //pulls the latest version from the cache.
        if(NSUserDefaults.standardUserDefaults().stringArrayForKey("Array")==nil){
             let newArray = [AnyObject]()
            self.saveArray(newArray)
        }
            self.updateWorkingArray()
        
            }
    
  
    //Clears all records from the array permanently. Use with caution
    public func clearArray(){
        let newArray = [AnyObject]()
        saveArray(newArray)
        
    }
    
   public func deleteTourIdAtRow(row:Int){
        var newArray : [AnyObject] = tourIdContainer as [AnyObject]
       newArray.removeAtIndex(row)
        saveArray(newArray)
        
    }
    
    private func updateWorkingArray(){

       tourIdContainer  = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! NSMutableArray
    }
    
    //Adds a new tourId to the array


    public func updateArray(tourId: String){

        //Duplicates the array, creating a mutable version that the new tourId can be added to.
        var newArray : [AnyObject] = tourIdContainer as [AnyObject]
        newArray.append(tourId)
        saveArray(newArray)
    }
    
    
    
    //saves the copy of the array passed to it persistently and updates working copy kept in this class.
    //Never called outside of this class
    private func saveArray(newArray: AnyObject){
        
        //This updates the working copy
        tourIdContainer = NSMutableArray(objects: newArray)
        //Stores the Array in NSUserDefaults, overwriting existing copy
        NSUserDefaults.standardUserDefaults().setObject(newArray, forKey: "Array")
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        //Pushes changes to working copy
        self.updateWorkingArray()
        
    }
    
    func addTourMetaData(metadata: NSArray){

        let keys = ["code","createdAt","expiresAt","objectId","tour","updatedAt"]
        var dict = metadata.dictionaryWithValuesForKeys(keys)
        let tourCode = dict["code"]!
        print(tourCode[0])
        print(dict)
        let fuckEverything = dict as! NSDictionary
        NSUserDefaults.standardUserDefaults().setObject(fuckEverything, forKey: tourCode[0] as! String)
        NSUserDefaults.standardUserDefaults().synchronize()
        
   
    }
    
    func getTourMetadata(tourCode: String) -> Dictionary<String,AnyObject>{
       return NSUserDefaults.standardUserDefaults().objectForKey(tourCode) as! Dictionary<String,AnyObject>
    }
    
      //temporary method for getting tourIds that have been added for checking the table updates.
    public func getAllTours() -> NSMutableArray {
      
        return tourIdContainer
    }
    

}