//
//  AlertViewBuilder.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 06/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation
import UIKit

class AlertViewBuilder: NSObject, UIAlertViewDelegate{
    
    
    //in order to access TourIdParser methods call TourIdParser.shardInstance.METHOD()
    class var sharedInstance: AlertViewBuilder {
        struct Static {
            static var onceToken: dispatch_once_t = 0
            static var instance: AlertViewBuilder? = nil
        }
        dispatch_once(&Static.onceToken) {
            Static.instance = AlertViewBuilder()
            
        }
        return Static.instance!
    }
    
    func showWarningAlert(title: String, message: String){
        let alert = UIAlertView(title: title, message: message, delegate: self, cancelButtonTitle:"Cancel")
        alert.alertViewStyle = UIAlertViewStyle.Default
        alert.show()
    }
    
    
}