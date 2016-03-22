//
//  TourMetadataConnector.swift
//  Tourable
//
//  Created by Federico on 19/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class TourMetadataConnector {

        lazy var data = NSMutableData()
        var urlPath: String = ""
        var jsonResult: NSDictionary!

        //Stores the data into the data var
        private func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
            //Storing the data for use
            self.data.appendData(data)
        }

        //Initiates the connection
        func checkTourMetadataForUpdates(objectID: String){
            let resetData = NSMutableData()
            //Reseting data to blank with every new connection
            data = resetData

            //The path to where the Tour Data is stored
            urlPath = "https://touring-api.herokuapp.com/api/v1/tour/" + objectID

            //Standard URLConnection method
            let request = NSURLRequest(URL: NSURL(string: urlPath)!)
            let config = NSURLSessionConfiguration.defaultSessionConfiguration()
            let session = NSURLSession(configuration: config)
            let task = session.dataTaskWithRequest(request, completionHandler: { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
                self.data.appendData(data!)
                // callback from api
                do {
                    self.jsonResult = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                    // dispatch action in the background to don't block the ui
                    dispatch_async(dispatch_get_main_queue()){
                        TourUpdateManager.sharedInstance.formatDataforTourSummaryAndDiplayIt(self.jsonResult)
                    }
                }
                catch _ as NSError{
                    //Need to let user know if the tourID they entered was faulty here
                    print("TourMetadataConnector: there was an error downloading data")
                }
            });
            
            task.resume()
        }

    //Initiates the connection
    func downloadTourUpdateMetadata(objectID: String, tourCode: String){
        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData

        //The path to where the Tour Data is stored
        urlPath = "https://touring-api.herokuapp.com/api/v1/tour/" + objectID

        //Standard URLConnection method
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        let task = session.dataTaskWithRequest(request, completionHandler: { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            self.data.appendData(data!)

            dispatch_async(dispatch_get_main_queue()){
                // try and catch needed to convert binary API file
                do{
                    let jsonResultFromAPI = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                    self.saveUpdateMetadata(jsonResultFromAPI, objectId: objectID, tourCode: tourCode)
                }catch let error as NSError{
                    print(error)
                }
            }
        });

        task.resume()
    }

    // update the cache when the tourMetadata is downloaded It is stored in the same cache dictionary of the tourKey
    func saveUpdateMetadata(metadata: NSDictionary, objectId: String, tourCode: String){

        var tourDict = NSUserDefaults.standardUserDefaults().objectForKey(tourCode) as! [String : AnyObject]

        tourDict["version"] = metadata["version"]
        tourDict["estimatedTime"] =  metadata["estimatedTime"]

        NSUserDefaults.standardUserDefaults().setObject(tourDict, forKey: tourCode)
        NSUserDefaults.standardUserDefaults().synchronize()
    }

}
