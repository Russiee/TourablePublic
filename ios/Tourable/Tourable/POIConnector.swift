//
//  POIConnector.swift
//  Tourable
//
//  Created by Alex Gubbay on 20/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation
//Handles API connectivity for points of interest. As these are handled differently to 
//tour sections
class POIConnector: NSObject, NSURLConnectionDelegate {
    
    var urlPath: String = ""
    
    
    //Initiates the connection
    func initateConnection(objectID: String){
        
        //The path to where the Tour Data is stored
        urlPath = "https://touring-api.herokuapp.com/api/v1/poi/" + objectID

        //Standard URLConnection method
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            do {
                let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary

                //SAVE JSON
                POIParser.init().savePOI(jsonResult)
            }
            catch _ as NSError{
                //Need to let user know if the tourID they entered was faulty here
                print("POIConnector: there was an error parsing a poi")
            }
        }
        
        task.resume()
    }
    
    func noContent(){
        POIParser().createEmptyPOI()
    }
}
