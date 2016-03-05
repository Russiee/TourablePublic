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
    var currentMetaData: Dictionary<String,AnyObject>!

    init(tourCodetoCheck: String) {
        self.tourCode = tourCodetoCheck
        self.currentMetaData = TourIdParser().getTourMetadata(tourCode)
    }

    // TODO2: add functionality to connect to the API (through APIConnector) and retrieve the 'last-updated' field.
    // Check against saved tourMetaData (throught tourIDParser). Re-downlaoad if needed.

}