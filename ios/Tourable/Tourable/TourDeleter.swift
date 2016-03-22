//
//  TourDeleter.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 05/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation


public class TourDeleter {
    
    private var objectIDs = [String]()
    private var mediaURLS = [String]()
    
     static let sharedInstance = TourDeleter()
    
    //deletes whole tour
    func deleteTour(tourCode: String){
        let arrayOfTours = NSUserDefaults.standardUserDefaults().objectForKey("Array") as! [AnyObject]
        var tourIDs = [String]()
        for tours in arrayOfTours{
            let key = tours.allKeys[0] as! String
            tourIDs.append(tours[key] as! String)
        }
        //get position of the tour we want to delete
        let pos = tourIDs.indexOf(tourCode)
        //gets the pointer of the tour
        let tourPointer = NSUserDefaults.standardUserDefaults().objectForKey(tourCode)
        //setting the Tour ID to tourPointer ObjectId
        let tourID = tourPointer!["objectId"] as! String
        //get the tour from UserDefaults
        let tour = NSUserDefaults.standardUserDefaults().objectForKey(tourID)
        //gets all the objectIDs of the tour (which we have stored in UserDefaults)
        getAllIDs(tour!["sections"] as! NSArray)
        //deletes al the objectIDs from User Defaults
        for ids in objectIDs{
            NSUserDefaults.standardUserDefaults().removeObjectForKey(ids)
            NSUserDefaults.standardUserDefaults().synchronize()
        }
        //reset array to empty for next call of getAllIDs
        objectIDs = []
        //deletes the objectId of the tour itself
        NSUserDefaults.standardUserDefaults().removeObjectForKey(tourID)
        NSUserDefaults.standardUserDefaults().synchronize()
        if pos != nil {
           TourIdParser().deleteTourIdAtRow(pos!)
        }
        
        
    }
    
    //gets all tours objectIDs
    func getAllIDs(section: NSArray){
        
        for subsection in section{
            objectIDs.append(subsection["objectId"] as! String)
            let keys = subsection.allKeys
            for value in keys{
                if value as! String == "pois"{
                    let POIS = subsection["pois"] as! NSArray
                    for pois in POIS{
                        objectIDs.append(pois["objectId"] as! String)
                    }
                }
                else if((value as! String) == "subsections"){
                    getAllIDs(subsection["subsections"] as! NSArray)
                }
            }
        }
    }
    
    //permanently deletes the image with the specified name
    func deleteMedia(imageURL: String, fileType: String)-> Bool {
        //get the storage name and path of the file to delete
        let fileName = String(imageURL.hash)
        let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileName, fileType: fileType)
        
        do{
            //try executing the delete and report on its success.
            try  NSFileManager.defaultManager().removeItemAtPath(path)
            return true
        }
        catch{
            return false
        }
        
    }
    
    //deletes the media files in the tour. Currently only .jpg s
    func deleteMediaInTour(tourCode: String){
        //gets the pointer of the tour
        let tourPointer = NSUserDefaults.standardUserDefaults().objectForKey(tourCode)
        //setting the Tour ID to tourPointer ObjectId
        let tourID = tourPointer!["objectId"] as! String
        //get the tour from UserDefaults
        let tour = NSUserDefaults.standardUserDefaults().objectForKey(tourID)
        //gets all the image urls of the tour (which is stored in UserDefaults)
        getAllMediaURL(tour!["sections"] as! NSArray)
        //calls the deleteMedia function on the urls we gathered from the Tour JSON
        for url in mediaURLS{
            deleteMedia(url, fileType: url.substringFromIndex(url.endIndex.advancedBy(-4)))
        }
        //resets the array
        mediaURLS = []
    }
    
    //searches the JSON for image URLS
    func getAllMediaURL(tour: NSArray){
        
        for subsection in tour{
            let keys = subsection.allKeys
            for value in keys{
                if value as! String == "pois"{
                    let POIS = subsection["pois"] as! NSArray
                    for pois in POIS{
                        if (pois.allKeys as! [String]).contains("post"){
                            let post = pois["post"] as! NSArray
                            for items in post{
                                //get all the keys of the post entry
                                let types = items.allKeys as! [String]
                                //currently first one can be a url hence we dont loop through the keys
                                if(types.contains("url")){
                                    //append the found url to mediaURLS
                                    mediaURLS.append(items["url"] as! String)
                                }
                            }
                        }
                    }
                }
                    //if is a subsection recall this method
                else if((value as! String) == "subsections"){
                    print("going to next subsection")
                    getAllMediaURL(subsection["subsections"] as! NSArray)
                }
            }
        }
    }
    
}