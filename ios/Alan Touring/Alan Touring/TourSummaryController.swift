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
    var objectId = ""
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
        objectId = setup["objectId"] as! String
        print("trying to get tour from ID: \(objectId)")
        let topLayerTourInfo = tourDataParser.init().getTourSection(objectId )
        tourTitleLabel.text = topLayerTourInfo.title as String
        UIDescriptionBox.text = topLayerTourInfo.description
        print("SETUP: \(setup)")
        let data = setup["objectId"]
        TourExpiryLabel.text = (data as! String)
        
        self.navigationController?.setToolbarHidden(true, animated: false)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedDownloading", name: beginDownloadKey, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedFinishedDownloading", name: endDownloadKey, object: nil)
        
       _ = TourUpdateManager(tourCodetoCheck: tourId)
    }
    
    func NotifiedDownloading(){
        print("download begun")
    }
    
    func NotifiedFinishedDownloading(){
        print("download finished in here")
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
        // Create a new variable to store the instance of PlayerTableViewController
        if segue.identifier == "goToTourSections" {
            let destinationVC = segue.destinationViewController as! TourSectionsController
            let topLayerTourInfo = tourDataParser.init().getTourSection(objectId)
            print(topLayerTourInfo.subsections)
            destinationVC.superTableId = topLayerTourInfo.sectionId 
        }
    }
    
    @IBAction func clickBeginTour(sender: AnyObject) {
//      beingTourButton.setTitle("Loading Tour", forState: .Normal)
        beingTourButton.enabled = false
    }
    
    
}

