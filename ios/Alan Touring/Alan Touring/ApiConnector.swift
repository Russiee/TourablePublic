
//  ApiConnector.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 12/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//
//
import Foundation
import UIKit

//keys for notifying observers of outcome of the key verification route
let invalidIdNotificationKey = "InvalidKeyEnteredNotification"
let validIdNotificationKey = "ValidKeyEnteredNotification"
var tourIdForSummary = ""

class ApiConnector: NSObject, NSURLConnectionDelegate{
    
    lazy var data = NSMutableData()
    var urlPath: String = ""
    
    func initateConnection(var tourId: String){
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
        catch _ as NSCocoaError{
            
            self.triggerInvalidKeyNotification()
        }
        //change to URLSession
    }
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
        //Storing the data for use
        self.data.appendData(data)
    }
    //Completion handler for the key verification route.
    func connectionDidFinishLoading(connection: NSURLConnection!) {
   
        do {
            let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
            _ = TourIdParser().addTourMetaData(jsonResult)
            self.triggerValidKeyNotification()
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

    // remove the heading and trailing spaces
    // rejects any tourIds with invalid symbols
    func cleanTourId(tourId: String) -> String {

        let trimmedTourId = tourId.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceCharacterSet())
        
        if trimmedTourId.containsString(" ") || trimmedTourId.containsString("/")||trimmedTourId.containsString("\"")||trimmedTourId.containsString("\\"){
            print("the tour id input must not contain whitespaces.")
            //not possible to return nil so returns blank.
             tourIdForSummary = ""
             return ""
        }
        tourIdForSummary = trimmedTourId
        return trimmedTourId
    }
    
}