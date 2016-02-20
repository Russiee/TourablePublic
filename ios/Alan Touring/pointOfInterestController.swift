//
//  pointOfInterestController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 20/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit



class pointOfInterestController: UIViewController {

    var poiID = ""
    
    @IBOutlet weak var poiTitleLabel: UILabel!
    
    override func viewDidLoad() {
        
       let poi = POIParser.init().getTourSection(poiID)
        
        poiTitleLabel.text = poi.title
        print(poi.getTitle())
    }
}
