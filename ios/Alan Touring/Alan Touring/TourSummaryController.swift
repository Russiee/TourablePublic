//
//  TourSummaryController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 10/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

let updateAvailableKey = "updateAvailable"

class TourSummaryController: UIViewController, UITableViewDataSource, UITableViewDelegate {



    var tourId = ""
    var tourObjectId = ""
    var tourIndex: Int!
    var objectId = ""
    var setup = Dictionary<String, AnyObject>()
    var tableRow = 0
    // counter to activate the busy wheel while updating
    var imageCount = 0
    var summaryTable = [String]()
    var summaryData = (timeHours: 0,timeMins: 0, isCurrent: true, expiresIn: 0)


    @IBOutlet weak var UIDescriptionBox: UITextView!
    @IBOutlet weak var beginTourButton: UIButton!
    @IBOutlet weak var updateIndicator: UIActivityIndicatorView! //busy wheel
    @IBOutlet weak var tableView: UITableView!
    
    override func viewWillAppear(animated: Bool) {

        beginTourButton.enabled = true
        setup = TourIdParser.sharedInstance.getTourMetadata(tourId)
        objectId = setup["objectId"] as! String
        let topLayerTourInfo = tourDataParser.init().getTourSection(objectId )
        UIDescriptionBox.text = topLayerTourInfo.description
        self.title = topLayerTourInfo.title as String
        updateIndicator.hidden = true
    }

    override func viewDidLoad() {
        //we dont need a toolbar for this view
        self.navigationController?.setToolbarHidden(true, animated: false)
        //notify this class about the status of update downloads
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedDownloading", name: beginDownloadKey, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedFinishedDownloading", name: endDownloadKey, object: nil)
        // Notification for TourUpdateManager called when there is an update available
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedUpdateAvailable", name: updateAvailableKey, object: nil)
       
        //get the latest formatted data for the ui.
        summaryTable = formatDataForTable(tourId, tableRow: tableRow)
        //set the data source and deligate of the table to this class.
        tableView.dataSource = self
        tableView.delegate = self
        //make the table only as big as the number of nows.
        tableView.tableFooterView = UIView(frame: CGRectZero)
         // code that checks for updates. not working atm.
        // tourManager.checkForUpdates()
    }

    
    // increase counter to activate user wheel
    func NotifiedDownloading(){
        imageCount++
    }
    
    // decrease the counter to stop the busy wheel.
    func NotifiedFinishedDownloading(){
        imageCount--
        // if 0 stop animation
        if imageCount == 0{
            updateIndicator.stopAnimating()
            updateIndicator.hidden = true
            beginTourButton.setTitle("Begin Tour", forState: .Normal)
            beginTourButton.enabled = true
        }
    }
    func formatDataForTable(tourId: String, tableRow: Int) -> [String]{
        //get the current update and expiry status for the current tour
        TourUpdateManager.sharedInstance.getCurrentData(tourId, tableRow: tableRow)
        //set the summary data tuple to the result of this call
        summaryData = TourUpdateManager.sharedInstance.getTourStatusInfo()
        
        //format the strings with the data from TourUpdateManager for display on the tour summary
        var updateStatus = "Version status unkown"
        let estimatedTime = "Estimated time: \(summaryData.timeHours) hour \(summaryData.timeMins) minutes"
        //checks the update status of the tour
        if summaryData.isCurrent {
            updateStatus = "Your version is current"
        }else{
            updateStatus = "An update is available"
        }
        let timeRemaining = "Tour key expires in \(summaryData.expiresIn) days"
        //create an array of the data
        var result = [String]()
        result.append(estimatedTime)
        result.append(updateStatus)
        result.append(timeRemaining)
        return result
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
                // change the 'begin tour' button appearance if updating
                beginTourButton.setTitle("Updating...", forState: .Normal)
                beginTourButton.enabled = false
                updateIndicator.hidden = false
                updateIndicator.startAnimating()
                TourUpdateManager.sharedInstance.triggerUpdate()
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
        beginTourButton.enabled = false
    }
    
  
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1 // This was put in mainly for my own unit testing
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return summaryTable.count // Most of the time my data source is an array of something...  will replace with the actual name of the data source
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        //get the cell at that point in the table
        let cell = tableView.dequeueReusableCellWithIdentifier("fakeCell")! as UITableViewCell
        //set the text of the row
        cell.textLabel?.text = summaryTable[indexPath.row]
        //time is running out, highlight expiry red
        if indexPath.row == 2 && summaryData.expiresIn < 3{
           
            cell.textLabel?.textColor = UIColor.redColor()
        }
        return cell
    }
    
}

