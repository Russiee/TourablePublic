//
//  TourSummaryController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 10/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class TourSummaryController: UIViewController {

    
    @IBOutlet weak var tourIdLabel: UILabel!
    var tourId = ""
    var tourObjectId = ""
    var tourIndex: Int!
    let objectId = ""
    var setup = Dictionary<String, AnyObject>()
    
    
    @IBOutlet weak var TourExpiryLabel: UILabel!
    
    override func viewWillAppear(animated: Bool) {
        tourIdLabel.text = tourId

        setup = TourIdParser.sharedInstance.getTourMetadata(tourId)
        let objectId = setup["objectId"]!
        print(objectId)
        let data = setup["expiresAt"]![0]
        TourExpiryLabel.text = (data as! String)
        
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {

        // Create a new variable to store the instance of PlayerTableViewController
        let destinationVC = segue.destinationViewController as! TourSectionsController
        destinationVC.programVar = setup["objectId"] as! String
    }
}

