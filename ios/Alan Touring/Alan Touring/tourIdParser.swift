//
//  tourIdParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 09/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class tourIdParser {
    
    //again temporary, will need replacing by a database.
   static var tourIdContainer = NSMutableArray()
    
    //TourId user has entered but not confirmed download of yet.
    static var tourIdtoDownload = ""
    
    //Failsafe to make sure methods called in order.
    static var readyToDownload = false
    init(){
        
    }
    static func addNewTourId(tourId: String) -> Bool{
        //function to allow us to check the tour id is valid without attempting to 
        //add it to the database until we know if user wants video. If id is valid, ready to download becomes true.
        //will only return true if tourId is valid.
        print("tour id added "+tourId)
        tourIdtoDownload = tourId
        readyToDownload = true
        return true
    }
    
    static func confirmTourId(withVideo: Bool) -> Bool{
        
        //Method stub for downloading tour. Will return true to confirm download complete and it has been added to the database.
        
        tourIdContainer.addObject(tourIdtoDownload)
        print("tourId to confirm "+tourIdtoDownload)
        
        if readyToDownload == true{
            //download will only happen if readyToDownload is true. This is a failsafe to make sure func arent called
            //out of order.
            readyToDownload = false
            //this will return true if download is successful
            return true
        }
        return false
    }
    
    
    
    
    
    static func getAllTours() -> NSMutableArray {
        //temporary method for getting tourIds that have been added for checking the table updates.
        print("total \(tourIdContainer.count)tourIds:")
        return tourIdContainer
    }
    

}