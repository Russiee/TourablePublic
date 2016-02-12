//
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
        urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/" + tourId
        let url: NSURL = NSURL(string: urlPath)!
        let request: NSURLRequest = NSURLRequest(URL: url)
        let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: false)!
        connection.start()
        print("starting connection")
    }
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
        self.data.appendData(data)
    }
    
    func initateConnection(tourId: String){
        startConnection(tourId)
    }
    
    func connectionDidFinishLoading(connection: NSURLConnection!) {
        print("connected")
        print(urlPath)
        
        // throwing an error on the line below (can't figure out where the error message is)
        do{
        let jsonResult: NSArray = try NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers) as! NSArray
        print(jsonResult)
    }
        catch let err as NSError{
            print(err.description)
        }
    
    }
    
}