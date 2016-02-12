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
    
    func startConnection(){
        let urlPath: String = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1010"
        let url: NSURL = NSURL(string: urlPath)!
        let request: NSURLRequest = NSURLRequest(URL: url)
        let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: false)!
        connection.start()
        print("starting connection")
    }
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
        self.data.appendData(data)
    }
    
    func initateConnection(){
        startConnection()
    }
    
    func connectionDidFinishLoading(connection: NSURLConnection!) {
        print("connected")
        var err: NSError
        // throwing an error on the line below (can't figure out where the error message is)
        do{
        let jsonResult: NSArray = try NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers) as! NSArray
        print(jsonResult)
    }
        catch{
            print("did not work")
        }
    
    }
    
}