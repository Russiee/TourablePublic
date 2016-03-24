
//  ApiConnector.swift
//  Tourable
//
//  Created by Alex Gubbay on 12/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//
// internet connectivity code from: http://stackoverflow.com/questions/25623272/how-to-use-scnetworkreachability-in-swift/25623647#25623647
//See isConnectedToNetwork for full credit.

import Foundation
import UIKit
import SystemConfiguration

//keys for notifying observers of outcome of the key verification route
let invalidIdNotificationKey = "InvalidKeyEnteredNotification"
let validIdNotificationKey = "ValidKeyEnteredNotification"
var tourIdForSummary = ""

///KeyVerifyConnector is responsible for connecting to the API and to check if a tour exists for key entered by user. If it exists then it continue onto download the meta data.
class KeyVerifyConnector: NSObject, NSURLConnectionDelegate{
    
    static let sharedInstance = KeyVerifyConnector()
    
    var JSONMetadataFromAPI: NSDictionary!
    var isUpdating = false
    
    
    ///Connects to the API, tkaing the tour code as an input to check if the key is valid. It does this by checking the result and calling
    ///The correct notifier asynchronusly, either triggerValidKeyNotification or triggerInvalidKeyNotification.
    ///If not in update mode, it will trigger asynchronus methods to update the cache with the new data.
    func initiateKeyVerifyConnection( var tourCode: String, isCheckingForUpdate: Bool){
        
        //Set update status and sanitise tour code.
        isUpdating = isCheckingForUpdate
        tourCode = cleanTourCode(tourCode)
        //The path to where the verifer is stored
    
        //Set up the configuration of the NSURL session for connection to the API
        let request = NSURLRequest(URL: NSURL(string: "https://touring-api.herokuapp.com/api/v1/key/verify/" + tourCode)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        
        //creates and runs the request to the server for key verifcation data.
        if self.isConnectedToNetwork(){
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            
            do { //Parses the response into JSON data
                let jsonResult = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                self.JSONMetadataFromAPI = jsonResult
                if !self.checkIfTourAlreadyOutdatedWhenDownloading(jsonResult["expiry"] as! String) {

                    dispatch_sync(dispatch_get_main_queue()){
                        
                        if !self.isUpdating {
                            //If not updating continue to save the rest of the tour objects.
                            _ = TourIdParser().addTourMetaData(jsonResult)
                        }
                        //Trigger the notification to observers that the key is valid.
                        self.triggerValidKeyNotification()
                    }
                    
                } else {
                    //Key is not valid, notify observers.
                    dispatch_async(dispatch_get_main_queue()){
                        
                        self.triggerInvalidKeyNotification()
                    }
                    
                }
            }
            catch let err as NSError{
                //Let user know if the tourID they entered was faulty here
                print(err.description)
                dispatch_async(dispatch_get_main_queue()){
                    self.triggerInvalidKeyNotification()
                }
            }
            
        }
        task.resume()
    }
    else{
         self.triggerInvalidKeyNotification()   
    }
    }

    ///Send a notification that the tour id is valid and has been parsed correctly.
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
    
    ///A synchronus method to get return from API. Will hold until data is returned. This is useful for some UI blocking waits.
    func getTourMetadata(tourCode: String) -> NSDictionary {
        // if the network call is not finished retrieve the tour metadata from the cache
        if JSONMetadataFromAPI != nil {
           return JSONMetadataFromAPI
        } else {
            return TourIdParser().getTourMetadata(tourCode)
        }
    }
    
    ///Sends a notifcation that the tour id entered was valid and parsed correctly
    func triggerValidKeyNotification() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "TourAddSuccess:",
            name: "TourIdAddSuccess",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(validIdNotificationKey, object: self)
        }
        notify()
        
    }


    /// Check if there is less than 1 minute left so as to prevent download of expired tours
    /// Will return false if tour is out of date or has less than 1 min remaining.
    func checkIfTourAlreadyOutdatedWhenDownloading(expiryString: String) -> Bool {
        let expiryDate = TourUpdateManager.sharedInstance.getDateFromString(expiryString)
        if expiryDate.minutesFrom(NSDate()) < 1 {
            return true
        }
        return false
    }

    // remove heading and trailing white spaces, removes /,\,"
    // rejects any tourIds with invalid symbols
    func cleanTourCode(tourId: String) -> String {

        var trimmedTourId = tourId.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceCharacterSet())
        //getting rid of whitespaces, \, /, " as they are invalid characters in a tour
        trimmedTourId = trimmedTourId.stringByReplacingOccurrencesOfString(" ", withString: "")
        trimmedTourId = trimmedTourId.stringByReplacingOccurrencesOfString("/", withString: "")
        trimmedTourId = trimmedTourId.stringByReplacingOccurrencesOfString("\"", withString: "")
        trimmedTourId = trimmedTourId.stringByReplacingOccurrencesOfString("\\", withString: "")

        tourIdForSummary = trimmedTourId
        return trimmedTourId
    }
    
    //checks if the device currently has an active internet connection.
    func isConnectedToNetwork() -> Bool {
        
        //Code and comment guidance from http://stackoverflow.com/questions/25623272/how-to-use-scnetworkreachability-in-swift/25623647#25623647
        //With thanks to user: Martin R http://stackoverflow.com/users/1187415/martin-r
        //Creates the socket address structure.
        var zeroAddress = sockaddr_in()
        //Gives the size of this structure, converted to UInt8
        zeroAddress.sin_len = UInt8(sizeofValue(zeroAddress))
        //Converts AF_INET to correct format for sin_family
        zeroAddress.sin_family = sa_family_t(AF_INET)
        
        //passes the address of the structure to the closure where it is used as an argument
        //In the SCNetworkReachabilityCreateWthAddress()
        //The guard let assigns the unwrapped value to the defaultRouteReachability if it is not nill.
        //If it is nill, no internet connection, return false.
        guard let defaultRouteReachability = withUnsafePointer(&zeroAddress, {
            //This conversion is required because the function expects a pointer to sockaddr not sockaddr_in
            SCNetworkReachabilityCreateWithAddress(nil, UnsafePointer($0))
        }) else {
            return false
        }
        //Returns a managed object.
        var flags : SCNetworkReachabilityFlags = []
        if !SCNetworkReachabilityGetFlags(defaultRouteReachability, &flags) {
            //If there are no flags, no connection, return false.
            return false
        }
        //Checks for flags indicating connectiivty status.
        let isReachable = flags.contains(.Reachable)
        let needsConnection = flags.contains(.ConnectionRequired)
        //Only got a working connection if the system is connected and it is not waiting for a connection.
        return (isReachable && !needsConnection)
    }
    
    
}