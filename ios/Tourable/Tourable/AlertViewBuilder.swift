//
//  AlertViewBuilder.swift
//  Tourable
//
//  Created by Alex Gubbay on 06/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation
import UIKit

//Singleton class for Alert Views.
class AlertViewBuilder: NSObject, UIAlertViewDelegate{
    
    static let sharedInstance = AlertViewBuilder()
    
    ///Takes as parameter an alert Title and Message. Displays the alert.
    func showWarningAlert(title: String, message: String){
        let alert = UIAlertView(title: title, message: message, delegate: self, cancelButtonTitle:"Cancel")
        alert.alertViewStyle = UIAlertViewStyle.Default
        alert.show()
    }
    
    
}