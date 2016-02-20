//
//  POIConnector.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 20/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class POIConnector: NSObject, NSURLConnectionDelegate{
    
    lazy var data = NSMutableData()
    var urlPath: String = ""
    
    //Makes the connection to the API
    func startConnection( objectID: String){
        
        
        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData
        
        //The path to where the Tour Data is stored
        
        urlPath = "https://touring-api.herokuapp.com/api/v1/poi/"+objectID
        //Standard URLConnection method
        let request: NSURLRequest = NSURLRequest(URL: NSURL(string: urlPath)!)
        
        //change to URLSession
        let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: false)!
        connection.start()
    }
    
    
    func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
        //Storing the data for use
        self.data.appendData(data)
    }
    
    func initateConnection(objectId: String){
        startConnection(objectId)
    }
    
    func connectionDidFinishLoading(connection: NSURLConnection!) {
        
        do {
            
            let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
            
            self.storeMetadataJson(jsonResult)
        }
        catch let err as NSError{
            print(urlPath+"    ERROR HERE")
            print(err.description)
            
            
        }
        
    }
    
    func storeMetadataJson(JSONData: NSDictionary){
        
        POIParser.init().savePOI(JSONData)
        //print(JSONData)
    }
    
    
    
}
