//
//  NavViewController.swift
//  Tourable
//
//  Created by Alex Gubbay on 08/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class NavViewController: UINavigationController, UIViewControllerTransitioningDelegate {

    override func viewDidLoad() {
        super.viewDidLoad()

        //set nav bar colour
        UINavigationBar.appearance().barTintColor = UIColor(red: 21/255.0, green: 42.0/255.0, blue: 74.0/255.0, alpha: 1.0)
        //set navigation button colour
        UINavigationBar.appearance().tintColor = UIColor(red: 1.0, green: 1.0, blue: 1.0, alpha: 0.64)
        //set title colour
        UINavigationBar.appearance().titleTextAttributes = [NSForegroundColorAttributeName : UIColor.whiteColor()]
        
        
        
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
