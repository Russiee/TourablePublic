//
//  TourUpdateManager.swift
//  Alan Touring
//
//  Created by Federico on 05/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

public class TourUpdateManager {

    let tourCode: String!
    var currentMetadata: Dictionary<String,AnyObject>!
    var newMetadata: NSDictionary!

    init(){
        //does nothing
        self.tourCode = ""
        self.currentMetadata = Dictionary()
        self.newMetadata = NSDictionary()
    }

    init(tourCodetoCheck: String) {
        self.tourCode = tourCodetoCheck
        self.currentMetadata = TourIdParser().getTourMetadata(tourCode)
        checkForUpdates()
    }

    func downloadNewMetadata() {
        let connection = ApiConnector.init()
        connection.initateConnection(tourCode, isUpdate: true)
        newMetadata = connection.getTourMetadata(tourCode)
    }

    func checkForUpdates() {
        downloadNewMetadata()

        let currentDateString = currentMetadata["updatedAt"]
        let newDateString = newMetadata["updatedAt"]

        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSz"
        let currentDate = dateFormatter.dateFromString(currentDateString as! String)
        let newDate = dateFormatter.dateFromString(newDateString as! String)

        let dateComparison = currentDate!.compare(newDate!)

        switch (dateComparison) {
            case NSComparisonResult.OrderedDescending:
                self.triggerUpdateAvailableNotification()
            default:
                print("This is because Swift")
        }
    }

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

    func triggerUpdate() {
        _ = TourIdParser().getTourMetadata(tourCode)
    }
}