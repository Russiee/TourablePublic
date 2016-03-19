//
//  AlertViewBuilder.swift
//  Tourable
//
//  Created by Alex Gubbay on 06/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation
import UIKit

class AlertViewBuilder: NSObject, UIAlertViewDelegate{
    
    
    //in order to access TourIdParser methods call TourIdParser.shardInstance.METHOD()
     static let sharedInstance = AlertViewBuilder()
    
    func showWarningAlert(title: String, message: String){
        let alert = UIAlertView(title: title, message: message, delegate: self, cancelButtonTitle:"Cancel")
        alert.alertViewStyle = UIAlertViewStyle.Default
        alert.show()
    }
    
    
}