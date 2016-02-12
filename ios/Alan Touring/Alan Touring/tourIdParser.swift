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
    
    //TourId user has entered but not confirmed download of yet.
    var tourIdtoDownload = ""
    
    //Failsafe to make sure methods called in order.
    var readyToDownload = false
    
    
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


    func updateArray(tourId: String){
        
       API.newConnection()

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
    
    
    //function to allow us to check the tour id is valid without attempting to
    //add it to the database until we know if user wants video. If id is valid, ready to download becomes true.
    //will only return true if tourId is valid.
    //NOTE: THIS DOES NOT UPDATE THE ARRAY IN WAY.
    public func addNewTourId(tourId: String) -> Bool{
        
        tourIdtoDownload = tourId
        readyToDownload = true
        return true
    }
    
    
    //Method stub for downloading tour. Will return true to confirm download complete and it has been added to the database.
    public func confirmTourId(withVideo: Bool) -> Bool{
        
        if readyToDownload == true{
             self.updateArray(tourIdtoDownload)
            //download will only happen if readyToDownload is true. This is a failsafe to make sure func arent called
            //out of order.
            readyToDownload = false
            //this will return true if download is successful
            return true
        }
        return false
    }
    
    
    
    
      //temporary method for getting tourIds that have been added for checking the table updates.
    public func getAllTours() -> NSMutableArray {
      
        return tourIdContainer
    }
    

}