//
//  POIParser.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 20/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class POIParser{
    
    // func createNewPOI(data: NSDictionary)-> pointOfInterest{
    
    
    
    // }
    
    
    func savePOI(data: NSDictionary){
        
        let key = data["objectId"] as! String
        
        NSUserDefaults.standardUserDefaults().setObject(data, forKey: key)
        //Commits changes to memory, required for iOS 7 and below.
        NSUserDefaults.standardUserDefaults().synchronize()
        print(data)
        
        
    }
    
    //    func getTourSection(objectId: String)-> tourSection{
    //
    //        let data = NSUserDefaults.standardUserDefaults().objectForKey(objectId) as! NSDictionary
    //        return createNewPOI(data)
    //
    //    }
    
    func deletePOI(){
        //this will be complicated, so will do it later
    }
    
}