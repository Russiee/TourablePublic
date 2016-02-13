
//  ApiConnector.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 12/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//
//
import Foundation

class ApiConnector: NSObject, NSURLConnectionDelegate{
    
    lazy var data = NSMutableData()
    var urlPath: String = ""
    
    func startConnection(tourId: String){
        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData
        //The path to where the Tour Data is stored
        urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/" + tourId
        //Standard URLConnection method
        let url: NSURL = NSURL(string: urlPath)!
        let request: NSURLRequest = NSURLRequest(URL: url)
        let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: true)!
        connection.start()
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

            self.storeJson(jsonResult)
        }
        catch let err as NSError{
            //Need to let user know if the tourID they entered was faulty here
            print(err.description)
        }
    
    }
    
    func storeJson(JSONData: NSArray){
        //Storing Meta Data so we can access it for other use
        let tour = tourIdParser.init()
        tour.addTourMetaData(JSONData)
    }
    
}