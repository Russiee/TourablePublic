//
//  TourDeleter.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 05/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

///The TourDeleter class deals with the deletion of tours. Handles deletion of the media files and every object associated with the tour.
public class TourDeleter {
    
    ///Var used to store objectIDS in getAllIDs().
    private var objectIDs = [String]()
    ///Var used to store mediaURLs in getAllMediaURL().
    private var mediaURLS = [String]()
    
    static let sharedInstance = TourDeleter()
    
    ///Takes a parameter of String expecting a tourCode. Uses the tourcode to find it in the NSUserDefaults to find position in "Array" and to delete it.
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
        
        //removes the tour from the "Array"
        if pos != nil {
            TourIdParser().deleteTourIdAtRow(pos!)
        }
        
        
    }
    
    ///Take as param the sections of the tour. Gets all tours objectIDs recursively.
    func getAllIDs(section: NSArray){
        
        for subsection in section{
            objectIDs.append(subsection["objectId"] as! String)
            let keys = subsection.allKeys
            for value in keys{
                //base case checking if it is a poi
                if value as! String == "pois"{
                    let POIS = subsection["pois"] as! NSArray
                    for pois in POIS{
                        objectIDs.append(pois["objectId"] as! String)
                    }
                } //otherwise call this method again
                else if((value as! String) == "subsections"){
                    getAllIDs(subsection["subsections"] as! NSArray)
                }
            }
        }
    }
    
    ///Takes as param url of media and its file type. Permanently deletes the image with the specified name.
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
    
    ///Takes a parameter of String expecting a tourCode. Uses the tourcode to find it in the NSUserDefaults to find its media files and delete them.
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
        //resets the array to empty for next call of getAllMediaURL
        mediaURLS = []
    }
    
    ///Take as param the sections of the tour. Gets all tours media urls recursively.
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
                    getAllMediaURL(subsection["subsections"] as! NSArray)
                }
            }
        }
    }
    
}