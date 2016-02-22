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
    

    

    
    override func viewDidLoad() {
//    testImage.contentMode = .ScaleAspectFit
        var images = [UIImage]()
       let poi = POIParser.init().getTourSection(poiID)
        let imgHnd = imageHandler.init()
       let poiPost = poi.post
        for poiPoint in poiPost{

            if var url: String? = poiPoint["url"] as? String{
                if  url != nil{
                    print(url)
                    
                    images.append(imgHnd.loadImageFromPath(url!)!)
                }
            }
        }
        

        print(poi.getTitle())
        
        
        if !(images.count==0){
          
        }
    }
}
