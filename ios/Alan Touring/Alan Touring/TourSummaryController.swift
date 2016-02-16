//
//  TourSummaryController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 10/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class TourSummaryController: UIViewController {

    
    @IBOutlet weak var tourIdLabel: UILabel!
    var tourId = ""
    var tourIndex: Int!
    
    @IBOutlet weak var TourExpiryLabel: UILabel!
    
    override func viewWillAppear(animated: Bool) {
        tourIdLabel.text = tourId

        let setup = TourIdParser.sharedInstance.getTourMetadata(tourId)
        let data = setup["expiresAt"]![0]
        TourExpiryLabel.text = (data as! String)
        
    }
   
    
}
