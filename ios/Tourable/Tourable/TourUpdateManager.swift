//
//  TourUpdateManager.swift
//  Tourable
//
//  Created by Federico on 05/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

public class TourUpdateManager {

    var tourTableRow: Int!
    var tourCode: String!
    var currentMetadata: Dictionary<String,AnyObject>!
    var newMetadata: NSDictionary!
    var alreadyFetchedMetadata = false
    
    // created just to initialise blank field object in other classes
  
    class var sharedInstance: TourUpdateManager {
        struct Static {
            static var onceToken: dispatch_once_t = 0
            static var instance: TourUpdateManager? = nil
        }
        dispatch_once(&Static.onceToken) {
            Static.instance = TourUpdateManager()
            
        }
        return Static.instance!
    }


   func getCurrentData(tourCodetoCheck: String, tableRow: Int) {
        self.tourTableRow = tableRow
        self.tourCode = tourCodetoCheck
        self.currentMetadata = TourIdParser().getTourMetadata(tourCode)


        downloadNewMetadata()
        checkForUpdates()
        //checkForOutdatedProject()

    }

    // download fresh metadata for the tour if there is internet connection
    func downloadNewMetadata() {
        if ApiConnector.sharedInstance.isConnectedToNetwork() {
           ApiConnector.sharedInstance.initiateConnection(tourCode, isCheckingForUpdate: true)
            newMetadata = ApiConnector.sharedInstance.getTourMetadata(tourCode)
        }
    }

    // check for updates comparing freshly downloaded metadata with current stored one
    // if there are updates the user is asked if he wants to download them
    func checkForUpdates() {
        downloadNewMetadata()
        
        if self.newMetadata != nil {
            
            let currentDate = dateFromString(currentMetadata["updatedAt"] as! String)
            let newDate = dateFromString(newMetadata["updatedAt"] as! String)
            
            let comparisonResultString = compareDates(currentDate, newDate: newDate)
            // check if the current date is less recent than the one in the metadata. If yes, ask the user to update tour.
            if comparisonResultString == "ascending" {
                print("current date \(currentDate) is less recent than the last updated \(newDate), therefore update triggered here")
                self.triggerUpdateAvailableNotification()
            }
        }
    }

    // check if a project is out to date comparing metadata with current today's date.
    // if the project is out to date, it is deleted after informing the user
    func checkIfOutdatedAndDeleteProject() {
        // in this way the metadata is downloaded only once when opening the app.
        if !alreadyFetchedMetadata {
            downloadNewMetadata()
            alreadyFetchedMetadata = true
        }
        
        if self.newMetadata != nil {
            let todaysDate = NSDate()
            let expiresDate = dateFromString(currentMetadata["expiresAt"] as! String)

            let comparisonResulFromString = compareDates(todaysDate, newDate: expiresDate)
            //if comparisonResulFromString == "descending" {
            if false {
                print("today is \(todaysDate) and it is beyond expiry \(expiresDate). Therefore delete project")
                TourDeleter().deleteTour(tourTableRow)
            } else if comparisonResulFromString == "same" {
                // warn the user that the this is the last day they can open the project
            }
        }
    }
    
    // compare two dates and returns a string saying the order of the dates.
    func compareDates(currentDate: NSDate, newDate: NSDate) -> String {
        let dateComparison = currentDate.compare(newDate)

        switch (dateComparison) {
        case NSComparisonResult.OrderedDescending:
            return "descending"
        case NSComparisonResult.OrderedAscending:
            return "ascending"
        default:
            return "same"
        }
    }
    
    // receive a string of format "yyyy-MM-dd'T'hh:mm:ss.SSSz" and returns an NSDate object
    func dateFromString(date: String) -> NSDate {
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSz"
        return dateFormatter.dateFromString(date)!
    }
    
    // trigger notification when there is an update avaiable
    func triggerUpdateAvailableNotification() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "Tour Update Available:",
            name: "Tour Update Notification",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(updateAvailableKey, object: self)

        }
        notify()
    }
    //get the current status of the tour from the latest data on the api. Returns a tuple
    func getTourStatusInfo() -> (timeHours: Int,timeMins: Int, isCurrent: Bool, expiresIn: Int){
    
        //place holder information, this will need implementation
        let timeHours = 1
        let timeMins = 30
        let isCurrent = false
        let expiresIn = 7
        return (timeHours, timeMins, isCurrent, expiresIn)
    }

    // called from the tourSummary when the user confirms to download the updates
    func triggerUpdate() {
        TourDeleter().deleteTour(tourTableRow)
        ApiConnector.sharedInstance.initiateConnection(tourCode, isCheckingForUpdate: false)
    }
}