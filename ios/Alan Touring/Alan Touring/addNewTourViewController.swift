//
//  addNewTourViewController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 08/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class addNewTourViewController: UIViewController {

    var tourID: String = ""
    
    override func viewDidLoad() {

    }
    
    @IBAction func withVideoButton(sender: AnyObject) {
        tourIdParser.confirmTourId(true)
        
        
    }
    @IBAction func withoutVideoButton(sender: AnyObject) {

    }
    override func viewWillDisappear(animated: Bool) {
        tourIdParser.confirmTourId(true)
    }
}
