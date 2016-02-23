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
    
    @IBOutlet weak var tourTitleLabel: UILabel!
    @IBOutlet weak var UIDescriptionBox: UITextView!
    @IBOutlet weak var TourExpiryLabel: UILabel!
    @IBOutlet weak var beingTourButton: UIButton!
    
    
    
    override func viewWillAppear(animated: Bool) {
        tourIdLabel.text = tourId
        UIDescriptionBox.sizeToFit()
        UIDescriptionBox.layoutIfNeeded()
        UIDescriptionBox.textAlignment = NSTextAlignment.Center
        beingTourButton.enabled = true
        
        setup = TourIdParser.sharedInstance.getTourMetadata(tourId)
        let objectId = setup["objectId"]!
        let topLayerTourInfo = tourDataParser.init().getTourSection(objectId as! String)
        tourTitleLabel.text = topLayerTourInfo.title as String
        UIDescriptionBox.text = topLayerTourInfo.description
        print(objectId)
        let data = setup["expiresAt"]![0]
        TourExpiryLabel.text = (data as! String)
        
    }
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {

        // Create a new variable to store the instance of PlayerTableViewController
        if segue.identifier == "goToTourSections"{
        let destinationVC = segue.destinationViewController as! TourSectionsController
        destinationVC.superTableId = setup["objectId"] as! String
        }
    }
    
    
    @IBAction func clickBeginTour(sender: AnyObject) {
//        beingTourButton.setTitle("Loading Tour", forState: .Normal)
        beingTourButton.enabled = false
    }
    
    
}

