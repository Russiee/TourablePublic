
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
    urlPath = ""
    var newData = NSMutableData()
    data = newData
    urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/" + tourId
    let url: NSURL = NSURL(string: urlPath)!
    let request: NSURLRequest = NSURLRequest(URL: url)
    let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: false)!
    connection.start()

    }
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
    self.data.appendData(data)
    }
    
    func initateConnection(tourId: String){
    startConnection(tourId)
    }
    
    func connectionDidFinishLoading(connection: NSURLConnection!) {
   
    do{
    let jsonResult: NSArray = try NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers) as! NSArray

    self.dealWithData(jsonResult)
}
    catch let err as NSError{
    print(err.description)
    }
    
    }
    
    func dealWithData(JSONData: NSArray){
        var data = JSONData
       let tour = tourIdParser.init()
        tour.addTourMetaData(JSONData)
        print(tour.getTourMetadata("KCL-1010").description+"LIFE IS BADr")
    
    
    //print(JSONData)
    }
    
}