//
//  TourUpdateManager.swift
//  Alan Touring
//
//  Created by Federico on 05/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

public class TourUpdateManager {

    let tourTableRow: Int!
    let tourCode: String!
    var currentMetadata: Dictionary<String,AnyObject>!
    var newMetadata: NSDictionary!

    // created just to initialise blank field object in other classes
    init(){
        //does nothing
        self.tourTableRow = Int()
        self.tourCode = ""
        self.currentMetadata = Dictionary()
        self.newMetadata = NSDictionary()
    }

    init(tourCodetoCheck: String, tableRow: Int) {
        self.tourTableRow = tableRow
        self.tourCode = tourCodetoCheck
        self.currentMetadata = TourIdParser().getTourMetadata(tourCode)
        checkForUpdates()
    }

    // download fresh metadata for the tour if there is internet connection
    func downloadNewMetadata() {

        if ApiConnector.sharedInstance.isConnectedToNetwork() {
            ApiConnector.sharedInstance.initateConnection(tourCode, isCheckingForUpdate: true)
            newMetadata = ApiConnector.sharedInstance.getTourMetadata(tourCode)
        }
    }

    // check for updates comparing freshly downloaded metadata with current stored one
    func checkForUpdates() {
        downloadNewMetadata()

        if self.newMetadata != nil {
            let currentDateString = currentMetadata["updatedAt"]
            let newDateString = newMetadata["updatedAt"]

            let dateFormatter = NSDateFormatter()
            dateFormatter.dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSz"
            let currentDate = dateFormatter.dateFromString(currentDateString as! String)
            let newDate = dateFormatter.dateFromString(newDateString as! String)

            let dateComparison = currentDate!.compare(newDate!)

            // check if the current date is less recent than the one in the metadata. If yes, ask the user to update tour.
            switch (dateComparison) {
                case NSComparisonResult.OrderedDescending:
                    self.triggerUpdateAvailableNotification()
                default:
                    // remove this next line when finsihed developing feature.
                    self.triggerUpdateAvailableNotification()
                    print("This is because Swift")
            }
        }
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

    // called from the tourSummary when the user confirms to download the updates
    func triggerUpdate() {
        TourDeleter().deleteTour(tourTableRow)
        ApiConnector.sharedInstance.initateConnection(tourCode, isCheckingForUpdate: false)
    }
}