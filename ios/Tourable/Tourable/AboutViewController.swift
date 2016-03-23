//
//  AboutViewController.swift
//  Tourable
//
//  Created by Alex Gubbay on 13/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class AboutViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
    }

    //Links the issues/feedback to a place where user can email us.
    @IBAction func supportButtonPressed(sender: AnyObject) {
        if let requestUrl = NSURL(string: "http://alexgubbay152.wix.com/tourable") {
            UIApplication.sharedApplication().openURL(requestUrl)
        }
    }

    //Link to where we got the icons from.
    @IBAction func icons8ButtonPressed(sender: AnyObject) {
        if let requestUrl = NSURL(string: "https://icons8.com") {
            UIApplication.sharedApplication().openURL(requestUrl)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
