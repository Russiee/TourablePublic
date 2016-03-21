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
                    dispatch_async(dispatch_get_main_queue()){
                        
                        TourUpdateManager.sharedInstance.receiveDataReadyFromApi(self.jsonResult)
  
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
            // callback from api
            do {
                self.jsonResult = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                dispatch_async(dispatch_get_main_queue()){
                
                    do{
                        let jsonResultFromAPI = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                        self.saveUpdateMetadata(jsonResultFromAPI, objectId: objectID, tourCode: tourCode)
                    }catch let error as NSError{
                        print(error)
                    }
                }
            }
            catch _ as NSError{
                //Need to let user know if the tourID they entered was faulty here
                print("TourMetadataConnector: there was an error downloading data")
            }
        });
        
        task.resume()
    }
    
  
    func saveUpdateMetadata(metadata: NSDictionary, objectId: String, tourCode: String){
        
            print(metadata)
        
        print("============")
//            let tourDict = NSUserDefaults.standardUserDefaults().objectForKey(tourCode) as! [String : AnyObject]
//        
//            NSUserDefaults.standardUserDefaults().setObject(tourDict, forKey: tourCode as! String)
//            NSUserDefaults.standardUserDefaults().synchronize()
//            print(tourDict)
//            
//
//            //bundleRoute.getAllPOIs((MYDAMNDATA["sections"]) as! NSArray)
//            let tourTitle = tourData["title"]
//            self.updateArray(tourCode as! String, tourTitle: tourTitle as! String)
        

        }
    
}
