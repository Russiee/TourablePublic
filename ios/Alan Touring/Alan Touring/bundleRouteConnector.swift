import Foundation
import UIKit


class bundleRouteConnector: NSObject, NSURLConnectionDelegate{
    
    lazy var data = NSMutableData()
    var urlPath: String = ""
    
    //Makes the connection to the API
    func startConnection(var objectID: String){
        
        
        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData
     
        //The path to where the Tour Data is stored
       
        urlPath = "https://touring-api.herokuapp.com/api/v1/section/m1dUFsZ1gt"
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
            //Need to let user know if the tourID they entered was faulty here
            print(err.description)

        }
        
    }

    //Takes the metadata and passes it to the tourIdParser.
    func storeMetadataJson(JSONData: NSDictionary){
        //Storing Meta Data so we can access it for other use
        print(JSONData["subsections"]!.count)
        tourDataParser.init().saveTourSection(JSONData)
       //print(JSONData)
        
    }
   
   
    
}