
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

class ApiConnector: NSObject, NSURLConnectionDelegate{
    
    class var sharedInstance: ApiConnector {
        struct Static {
            static var onceToken: dispatch_once_t = 0
            static var instance: ApiConnector? = nil
        }
        dispatch_once(&Static.onceToken) {
            Static.instance = ApiConnector()
            
        }
        return Static.instance!
    }
    
    
    lazy var data = NSMutableData()
    var urlPath: String = ""
    var JSONMetadataFromAPI: NSDictionary!
    var isUpdating = false
    
    func initiateConnection( var tourCode: String, isCheckingForUpdate: Bool){
        isUpdating = isCheckingForUpdate
        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData
        tourCode = cleanTourId(tourCode)
        //The path to where the verifer is stored
        let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/" + tourCode
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            self.data.appendData(data!)
            do {
                let jsonResult = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                self.JSONMetadataFromAPI = jsonResult
                dispatch_async(dispatch_get_main_queue()){
                if !self.isUpdating {
                        _ = TourIdParser().addTourMetaData(jsonResult)
                }
                self.triggerValidKeyNotification()
                //passing through the array of sections
                }
            }
            catch let err as NSError{
                //Need to let user know if the tourID they entered was faulty here
                print(err.description)
                self.triggerInvalidKeyNotification()
            }
            
        }
        task.resume()
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
    
    func getTourMetadata(tourCode: String) -> NSDictionary {
        // if the network call is not finished retrieve the tour metadata from the cache
        if JSONMetadataFromAPI != nil {
           return JSONMetadataFromAPI
        } else {
            return TourIdParser().getTourMetadata(tourCode)
        }
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
        }
        notify()
    }

    // remove the heading and trailing spaces
    // rejects any tourIds with invalid symbols
    func cleanTourId(tourId: String) -> String {

        let trimmedTourId = tourId.stringByTrimmingCharactersInSet(NSCharacterSet.whitespaceCharacterSet())

        if trimmedTourId.containsString(" ") || trimmedTourId.containsString("/")||trimmedTourId.containsString("\"")||trimmedTourId.containsString("\\"){
            //not possible to return nil so returns blank.
             tourIdForSummary = ""
             return ""
        }
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