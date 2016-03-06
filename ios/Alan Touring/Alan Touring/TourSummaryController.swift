//
//  TourSummaryController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 10/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

let updateAvailableKey = "updateAvailable"

class TourSummaryController: UIViewController {


    @IBOutlet weak var tourIdLabel: UILabel!
    var tourId = ""
    var tourObjectId = ""
    var tourIndex: Int!
    var objectId = ""
    var setup = Dictionary<String, AnyObject>()
    var tourManager = TourUpdateManager()
    var tableRow = 0

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
        // Notification for TourUpdateManager called when there is an update available
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedUpdateAvailable", name: updateAvailableKey, object: nil)

        tourManager = TourUpdateManager(tourCodetoCheck: tourId, tableRow: tableRow)
    }

    func NotifiedDownloading(){
        print("download begun")
    }

    func NotifiedFinishedDownloading(){
        print("download finished in here")
    }

    // triggered if the TourUpdateManager sends the notification that an update is available
    func NotifiedUpdateAvailable(){
        print("update was found to be avialble")
        showTourUpdateAlert()
    }

    func showTourUpdateAlert(){
        let alert = UIAlertView(title: "Tour Update", message: "An update to this tour is avialable. Would you like to download it?", delegate: self, cancelButtonTitle:"Cancel")
        alert.alertViewStyle = UIAlertViewStyle.Default
        alert.addButtonWithTitle("Update")
        alert.show()
    }

    //controls the behavior of the alerts to trigger updates
    func alertView(alertView: UIAlertView, clickedButtonAtIndex buttonIndex: Int) {
        switch buttonIndex{
            case 1:
                tourManager.triggerUpdate()
            case 0:
                break  //Cancel pressed, do not download update
            default:
                print("This is here because Swift")
        }
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

