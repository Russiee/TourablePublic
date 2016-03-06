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

        print(newMetadata)

    }

    func checkForUpdates() {
        downloadNewMetadata()

        let currentDateString = currentMetadata["updatedAt"]
        let newDateString = newMetadata["updatedAt"]

        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSSz"
        let currentDate = dateFormatter.dateFromString(currentDateString as! String)
        let newDate = dateFormatter.dateFromString(newDateString as! String)

        print(currentDateString)
        print(newDateString)

        print(currentDate)
        print(newDate)

        let dateComparison = currentDate!.compare(newDate!)

        switch (dateComparison) {
            case NSComparisonResult.OrderedDescending:
                _ = TourIdParser().getTourMetadata(tourCode)
                // here you should 'say' that the app is updating stuff
            default:
                // here trigger alert that everything is up to date
                print("")
        }
    }
}