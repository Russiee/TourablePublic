import Foundation
import UIKit

//Handles API connectivity for tour sections. As these are handled differently to
//tour sections
class bundleRouteConnector: NSObject, NSURLConnectionDelegate{
    
    lazy var data = NSMutableData()
    var urlPath: String = ""
    
    //Makes the connection to the API
    private func startConnection( objectID: String){
        
        
        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData

        //The path to where the Tour Data is stored
       
        urlPath = "https://touring-api.herokuapp.com/api/v1/section/"+objectID
        //Standard URLConnection method
        let request: NSURLRequest = NSURLRequest(URL: NSURL(string: urlPath)!)
        
        //change to URLSession
        let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: false)!
        connection.start()
    }
    
    
    private func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
        //Storing the data for use
        self.data.appendData(data)
    }
    
    func initateConnection(objectId: String){
        startConnection(objectId)
    }
    
    //handles data after connection is complete.
    private func connectionDidFinishLoading(connection: NSURLConnection!) {
        
        do {
            
            let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
            
            self.storeTourJSON(jsonResult)
        }
        catch let err as NSError{
            //Need to let user know if the tourID they entered was faulty here
           
            print(err.description)
            

        }
        
    }

    //Takes the metadata and passes it to the tourIdParser.
    func storeTourJSON(JSONData: NSDictionary){
        //Storing Meta Data so we can access it for other use
        
        tourDataParser.init().saveTourSection(JSONData)
       //print(JSONData)
        
    }
   
   
    
}