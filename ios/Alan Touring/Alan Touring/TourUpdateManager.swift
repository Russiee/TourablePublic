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
                // if user clicks 'yes' on update alert 
                triggerUpdate()
                // here you should 'say' that the app is updating stuff (maybe)
            default:
                // here update a label that the tour is updated (maybe)
                print("")
        }
    }
    
    func triggerUpdate() {
        _ = TourIdParser().getTourMetadata(tourCode)
    }
}