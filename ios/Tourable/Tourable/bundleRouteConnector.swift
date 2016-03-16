import Foundation
import UIKit

//Handles API connectivity for tour sections. As these are handled differently to
//tour sections
class bundleRouteConnector: NSObject, NSURLConnectionDelegate{

    lazy var data = NSMutableData()
    //var urlPath: String = ""
    var jsonResultFromAPI: NSDictionary!

    override init() { }

    //Makes the connection to the API
    func startConnection( objectID: String){
        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData

        //The path to where the Tour Data is stored
        let urlPath = "https://touring-api.herokuapp.com/api/v1/bundle/" + objectID
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)

        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            self.data.appendData(data!)
            do {
                self.jsonResultFromAPI = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                //passing through the array of sections

            }
            catch let err as NSError{
                //Need to let user know if the tourID they entered was faulty here
                print(err.description)
            }
            
        }
        task.resume()
    }

    private func connection(connection: NSURLConnection!, didReceiveData data: NSData!){
        //Storing the data for use
        self.data.appendData(data)
    }
    
    // called to retrieve the data returned by the API
    func getJSONResult() -> NSDictionary {
        while(jsonResultFromAPI == nil){
            
        }
        return jsonResultFromAPI
    }
    
    
}