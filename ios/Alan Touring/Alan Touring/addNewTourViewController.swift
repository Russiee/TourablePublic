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
    override func viewDidLoad() {
        print("view summary was loaded")
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedInvalid", name: invalidIdNotificationKey, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedValid", name: validIdNotificationKey, object: nil)
        self.busyWheel.startAnimating()

    }
    
    @IBAction func withVideoButton(sender: AnyObject) {
        //tourIdParser.confirmTourId(true)
        
        
    }
    @IBAction func withoutVideoButton(sender: AnyObject) {

    }
    override func viewWillDisappear(animated: Bool) {
       // tourIdParser.confirmTourId(true)
    }
    
    func NotifiedInvalid() {
        self.busyWheel.stopAnimating()
        self.busyWheel.hidden = true
        let alert = UIAlertView(title: "Tour ID Error", message: "The tour ID entered is not valid or is out of date", delegate: self, cancelButtonTitle:"OK")
        alert.alertViewStyle = UIAlertViewStyle.Default
        alert.show()
        
        print("tour summary was notified")
        performSegueWithIdentifier("Cancel", sender: self)
    }
    func NotifiedValid(){
        self.busyWheel.stopAnimating()
        self.busyWheel.hidden = true
    }
}
