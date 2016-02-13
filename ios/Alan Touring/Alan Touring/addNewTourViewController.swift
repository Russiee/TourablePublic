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
    
    @IBOutlet weak var busyWheel: UIActivityIndicatorView!
    @IBOutlet weak var withVideoButton: UIButton!
    @IBOutlet weak var withOutVideoButton: UIButton!
    @IBOutlet weak var tourInformationLabel: UILabel!
    
    
    override func viewDidLoad() {
        print("view summary was loaded")
        //Adds the observers for a valid or invalid key input completion message from ApiController
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedInvalid", name: invalidIdNotificationKey, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedValid", name: validIdNotificationKey, object: nil)
       //Starts busy wheel animation and hides the other items in the view.
        self.busyWheel.startAnimating()
        self.hideButtonsForBusyWheel(true)

    }
    
    //Will be used to confirm downloads of video for the tour
    @IBAction func withVideoButton(sender: AnyObject) {
        //tourIdParser.confirmTourId(true)
        
    }
    
    //Will be used to confirm no download of video from the tour
    @IBAction func withoutVideoButton(sender: AnyObject) {

    }
    
    override func viewWillDisappear(animated: Bool) {
       // tourIdParser.confirmTourId(true)
    }
    
    //Called if the tourId entered is invalid. Stops busy wheel, hides view and presents error message.
    func NotifiedInvalid() {
        self.busyWheel.stopAnimating()
        self.busyWheel.hidden = true
        //Shows warning to user that tour id was invalid.
        let alert = UIAlertView(title: "Tour ID Error", message: "The tour ID entered is not valid or is out of date", delegate: self, cancelButtonTitle:"OK")
        alert.alertViewStyle = UIAlertViewStyle.Default
        alert.show()
        
        //Closes the view and returns user to the mainTableview if key was invalid.
        print("tour summary was notified")
        performSegueWithIdentifier("Cancel", sender: self)
    }
    
    //Called if the tourId if valid. Stops the busy wheel and shows the download settings.
    func NotifiedValid(){
        self.busyWheel.stopAnimating()
        self.busyWheel.hidden = true
        self.hideButtonsForBusyWheel(false)
    }
    
    //Method for hiding all other items in the view besides teh busy wheel. 
    //Visibility:True = hides all items
    func hideButtonsForBusyWheel(visibility: Bool){
        withOutVideoButton.hidden = visibility
        withVideoButton.hidden = visibility
        tourInformationLabel.hidden = visibility
    }
}
