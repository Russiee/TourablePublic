//
//  addNewTourViewController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 08/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class addNewTourViewController: UIViewController, UIAlertViewDelegate {

    var tourID: String = ""
    var tourIndex: Int?
    var totalImagesToDownload: Float = 0.0
    var downloadedImages: Float = 0.0
    
    @IBOutlet weak var busyWheel: UIActivityIndicatorView!
    @IBOutlet weak var ProgressBar: UIProgressView!
    @IBOutlet weak var saveTourButton: UIButton!
    @IBOutlet weak var DownloadTypeChooser: UISegmentedControl!
    @IBOutlet weak var tourTitleLabel: UILabel!
    @IBOutlet weak var downloadStatusLabel: UILabel!
    @IBOutlet weak var tourDescriptionLabel: UILabel!
    
    
    let newCancelButton = UIBarButtonItem()
    
    
    override func viewDidLoad() {
        
        //Adds the observers for image downloading, alowing progress bar to be updated.
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedDownloading", name: beginDownloadKey, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedFinishedDownloading", name: endDownloadKey, object: nil)
          //Adds the observers for a valid or invalid key input completion message from ApiController
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedInvalid", name: invalidIdNotificationKey, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedValid", name: validIdNotificationKey, object: nil)
       //Starts busy wheel animation and hides the other items in the view.
        self.busyWheel.startAnimating()
        self.hideButtonsForBusyWheel(true)
        
        let newCancelButton = UIBarButtonItem(barButtonSystemItem: .Cancel, target: self, action: "cancelDownload")
        self.navigationItem.setLeftBarButtonItem(newCancelButton, animated: false)
        ProgressBar.setProgress(0.0, animated: true)
        downloadStatusLabel.text = ""
    }
    
    //Lets the UI know that another image is due to be downloaded.
    func NotifiedDownloading() {
        totalImagesToDownload++
    }
    
    //Lets UI know that an image has been downloaded: progress bar incremented.
    func NotifiedFinishedDownloading(){
        
        downloadedImages++
        //Get progress as fraction of 1
        let progress = (downloadedImages/totalImagesToDownload)
        downloadStatusLabel.text = "Downloading Media: \(Int(downloadedImages)) of \(Int(totalImagesToDownload))"
        ProgressBar.setProgress(progress, animated: true)
 
        //I.e progess = 100%
        if progress == 1.0{
            //Allow user to leave page, hide download status
            saveTourButton.enabled = true
            saveTourButton.setTitle("Save Tour", forState: .Normal)
            downloadStatusLabel.hidden = true
        }
    }
    
    //a method to allow the user to cancel the data download and return to the main table view
    func cancelDownload() {

        //delete the metaData and data stored in "Array"
        TourIdParser.sharedInstance.deleteTourIdAtRow(tourIndex!)
        //unwind segue back to the main tableview
        self.dismissViewControllerAnimated(true, completion: nil)
        
    }
    
    
    override func viewWillDisappear(animated: Bool) {
       
    }
    
    //Called if the tourId entered is invalid. Stops busy wheel, hides view and presents error message.
    func NotifiedInvalid() {
        self.busyWheel.stopAnimating()

        //Shows warning to user that tour id was invalid.
        let alert = UIAlertView(title: "Tour ID Error", message: "The tour ID entered is not valid or is out of date", delegate: self, cancelButtonTitle:"OK")
        alert.alertViewStyle = UIAlertViewStyle.Default
        alert.show()
        
        //Closes the view and returns user to the mainTableview if key was invalid.

        performSegueWithIdentifier("cancelAdd", sender: self)
        
    }
    
    //Called if the tourId if valid. Stops the busy wheel and shows the download settings.
    func NotifiedValid(){
        self.busyWheel.stopAnimating()
        self.hideButtonsForBusyWheel(false)
        saveTourButton.setTitle("Downloading...", forState: .Normal)
        saveTourButton.enabled = false
        self.setTourInfomation()
    }
    
    func setTourInfomation(){
        let dataId = TourIdParser().getTourMetadata(tourIdForSummary)["objectId"] as! String
        let tour = tourDataParser().getTourSection(dataId)
        tourTitleLabel.text = tour.title as String
        tourDescriptionLabel.text =  tour.description 
        
    }
    
    //Method for hiding all other items in the view besides teh busy wheel. 
    //Visibility:True = hides all items
    func hideButtonsForBusyWheel(visibility: Bool){
      
        tourTitleLabel.hidden = visibility
        DownloadTypeChooser.hidden = visibility
        saveTourButton.hidden = visibility
        ProgressBar.hidden = visibility
        downloadStatusLabel.hidden = visibility
        tourDescriptionLabel.hidden = visibility
    }
}
