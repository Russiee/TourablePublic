
//  ApiConnector.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 12/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//
//
import Foundation
import UIKit

let invalidIdNotificationKey = "InvalidKeyEnteredNotification"
let validIdNotificationKey = "ValidKeyEnteredNotification"

class ApiConnector: NSObject, NSURLConnectionDelegate{
    
    lazy var data = NSMutableData()
    var urlPath: String = ""
    
    //Makes the connection to the API
    func startConnection(var tourId: String){
//        let resetData = NSMutableData()
//        data = resetData
//        //The path to where the Tour Data is stored
//        urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/" + tourId
//        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
//        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
//        let session = NSURLSession(configuration: config)
//        
//        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
//            self.data.appendData(data!)
//            do {
//                let jsonResult: NSArray = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSArray
//                
//                self.storeJson(jsonResult)
//            }
//            catch let err as NSError{
//                //Need to let user know if the tourID they entered was faulty here
//                print(err.description)
//                self.triggerInvalidKeyNotification()
//            }
//        }
//        task.resume()
        

                let resetData = NSMutableData()
                //Reseting data to blank with every new connection
                data = resetData
                tourId = cleanTourId(tourId)
                //The path to where the Tour Data is stored
               urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/" + tourId

                //Standard URLConnection method
        do {
            let request: NSURLRequest = NSURLRequest(URL: NSURL(string: urlPath)!)
            let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: false)!
            connection.start()
        }
        catch let err as NSError{
            //Need to let user know if the tourID they entered was faulty here
            print(err.description)
            self.triggerInvalidKeyNotification()
        }
        catch let err as NSCocoaError{
            
            self.triggerInvalidKeyNotification()
        }
        //change to URLSession
        
    }
    
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
        //Storing the data for use
        self.data.appendData(data)
    }
    
    func initateConnection(tourId: String){
        startConnection(tourId)
    }
    
    func connectionDidFinishLoading(connection: NSURLConnection!) {
   
        do {
            let jsonResult: NSArray = try NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers) as! NSArray

            self.storeMetadataJson(jsonResult)
        }
        catch let err as NSError{
            //Need to let user know if the tourID they entered was faulty here
            print(err.description)
            self.triggerInvalidKeyNotification()
        }
    
    }
    //send a notification that the tour id
    func triggerInvalidKeyNotification() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "TourAddFailed:",
            name: "TourIdAddFailed",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(invalidIdNotificationKey, object: self)

        }
        notify()
    }
    
    //Send a notifcation that the tour id entered was valid and parsed correctly
    func triggerValidKeyNotification() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "TourAddSuccess:",
            name: "TourIdAddSuccess",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(validIdNotificationKey, object: self)
            print("Valid key notify called")
        }
        notify()
    }
    
    //Takes the metadata and passes it to the tourIdParser.
    func storeMetadataJson(JSONData: NSArray){
        //Storing Meta Data so we can access it for other use
        _ = TourIdParser().addTourMetaData(JSONData)
        self.triggerValidKeyNotification()
        //This will be the objectId taken from the key verification route.
        
        _ = bundleRouteConnector.init().startConnection("m1dUFsZ1gt")
    }
    
    
    // remove the heading and trailing spaces
    func cleanTourId(tourId: String) -> String {

        let trimmedTourId = tourId.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceCharacterSet())
        
        if trimmedTourId.containsString(" ") || trimmedTourId.containsString("/")||trimmedTourId.containsString("\""){
            print("the tour id ou input must not contain whitespaces.")
            return ""
        }
        
        return trimmedTourId
    }
    
}