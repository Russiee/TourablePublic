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
    
    //Tour code that identifies the tour
    var tourId = ""
    //objectId that identifies the tour metadata in the cache
    var objectId = ""
    //Tour metadata dictionay from cache
    var tableRow = 0
    // counter to activate the busy wheel while updating
    var imageCount = 0
    //Formatted data to be added to the UITableView
    var summaryTableData = [String]()
    //Tuple from TourUpdateManager with current status of the tour
    var summaryData = (timeHours: 0,timeMins: 0, isCurrent: true, expiresIn: 0)
    //button shown if update is availble
    let updateButton = UIButton(type: UIButtonType.RoundedRect)
    //lets the class know if the tour is currently being updated
    var isUpdating = false

    //Outlets to various static UI elements
    @IBOutlet weak var beginTourButton: UIButton!
    @IBOutlet weak var UIDescriptionBox: UITextView!
    @IBOutlet weak var updateIndicator: UIActivityIndicatorView! //busy wheel
    @IBOutlet weak var tableView: UITableView!
    
    override func viewWillAppear(animated: Bool) {
        //Get the object id of the tour based on the tourcode given from the table
        objectId =  TourIdParser.sharedInstance.getTourMetadata(tourId)["objectId"] as! String
        //gets the information about the tour to be displayed in the summary
        let topLayerTourInfo = tourDataParser.init().getTourSection(objectId)
        
        //add the tour description to the UI, set title and set up buttons + indicators
        UIDescriptionBox.text = topLayerTourInfo.description
        self.title = topLayerTourInfo.title as String
        updateIndicator.hidden = true
        beginTourButton.enabled = true
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
        summaryTableData = formatDataForTable(tourId, tableRow: tableRow)
        //set the data source and deligate of the table to this class.
        tableView.dataSource = self
        tableView.delegate = self
        //make the table only as big as the number of nows.
        tableView.tableFooterView = UIView(frame: CGRectZero)
        tableView.rowHeight = 60.0
         // code that checks for updates. not working atm.
        // tourManager.checkForUpdates()
    }
    
    //Takes the data from the tuple and formats it for presentation in the tableView
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

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject!) {
        // Create a new variable to store the instance of PlayerTableViewController
        if segue.identifier == "goToTourSections" {
            let destinationVC = segue.destinationViewController as! TourSectionsController
            let topLayerTourInfo = tourDataParser.init().getTourSection(objectId)
            print(topLayerTourInfo.subsections)
            destinationVC.superTableId = topLayerTourInfo.sectionId 
        }
    }
    
    //user has tapped begin tour, triggers the segue implicitly
    @IBAction func clickBeginTour(sender: AnyObject) {
        beginTourButton.enabled = false
    }
    
    // triggered if the TourUpdateManager sends the notification that an update is available
    func NotifiedUpdateAvailable(){
        summaryData.isCurrent = false
        tableView.reloadData()
    }
    
    //user has tapped update tour, modify UI and begin update.
    @IBAction func updateButtonClicked(sender: AnyObject){
        //let class know that tour is being updated
        isUpdating = true
        
        //hide update button and show busy wheel. Updates begin button.
        updateButton.removeFromSuperview()
        tableView.reloadData()
        beginTourButton.setTitle("Updating...", forState: .Normal)
        beginTourButton.enabled = false
        updateIndicator.hidden = false
        updateIndicator.startAnimating()
        beginTourButton.enabled = false
        
        //trigger update
        TourUpdateManager.sharedInstance.triggerUpdate()
        
    }
    
    // increase counter to activate user wheel
    func NotifiedDownloading(){
        imageCount++
    }
    
    // decrease the counter to stop the busy wheel.
    func NotifiedFinishedDownloading(){
        
        //decrement number of images left to download
        imageCount--
        
        // if 0 stop animation, update data sources.
        if imageCount == 0{
            updateIndicator.stopAnimating()
            updateIndicator.hidden = true
            beginTourButton.setTitle("Begin Tour", forState: .Normal)
            beginTourButton.enabled = true
            beginTourButton.enabled = true
            
            isUpdating = false
            summaryTableData = formatDataForTable(tourId, tableRow: tableRow)
            //shouldnt need this next line in final version
            summaryData.isCurrent = true
            tableView.reloadData()
           
        }
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1 //will only ever be 1 section here
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return summaryTableData.count // Most of the time my data source is an array of something...  will replace with the actual name of the data source
    }
    
    //
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        //get the cell at that point in the table
        let cell = tableView.dequeueReusableCellWithIdentifier("fakeCell")! as UITableViewCell
        //set the text of the row
        cell.textLabel?.text = summaryTableData[indexPath.row]
        
        //time is running out, highlight expiry red
        if indexPath.row == 2 && summaryData.expiresIn < 3{
            cell.textLabel?.textColor = UIColor.redColor()
        }
        
        //Set the dynamic content of the update status row.
        if indexPath.row == 1 {
            //tour is not upto date and not currently being updated, show update button.
            if !summaryData.isCurrent && !isUpdating{
                updateButton.frame = CGRectMake(40, 60, 75, 24)
                updateButton.center = CGPoint(x: view.bounds.width * 0.85, y: 60.0 / 2.0)
                updateButton.layer.cornerRadius = 3
                updateButton.layer.borderWidth = 1
                updateButton.layer.borderColor = updateButton.titleLabel?.textColor.CGColor
                updateButton.addTarget(self, action: "updateButtonClicked:", forControlEvents: UIControlEvents.TouchUpInside)
                updateButton.setTitle("UPDATE", forState: UIControlState.Normal)
                cell.addSubview(updateButton)
            
            //tour is up to date and not being updated, show the green tick
            }else if summaryData.isCurrent && !isUpdating{
                
                let tick_image = UIImage(named: "green_tick")
                let tickFrame = UIImageView(image: tick_image)
                tickFrame.center = CGPoint(x: view.bounds.width * 0.95, y: 60.0 / 2.0)
                cell.addSubview(tickFrame)
             
            //Tour is being updated, show the busy wheel
            }else{
                
                updateIndicator.frame = CGRectMake(40, 60, 75, 24)
                updateIndicator.center = CGPoint(x: view.bounds.width * 0.90, y: 60.0 / 2.0)
                cell.addSubview(updateIndicator)
                
            }
        }
        
        return cell
    }
}

