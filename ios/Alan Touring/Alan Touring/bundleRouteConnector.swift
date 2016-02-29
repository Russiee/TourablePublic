import Foundation
import UIKit

//Handles API connectivity for tour sections. As these are handled differently to
//tour sections
class bundleRouteConnector: NSObject, NSURLConnectionDelegate{

    lazy var data = NSMutableData()
    var urlPath: String = ""

    //Makes the connection to the API
    private func startConnection( objectID: String){
        print("got here")

        let resetData = NSMutableData()
        //Reseting data to blank with every new connection
        data = resetData

        //The path to where the Tour Data is stored

        urlPath = "https://touring-api.herokuapp.com/api/v1/bundle/"+objectID
        print(urlPath)
        //Standard URLConnection method
        //        let request: NSURLRequest = NSURLRequest(URL: NSURL(string: urlPath)!)
        //
        //        //change to URLSession
        //        let connection: NSURLConnection = NSURLConnection(request: request, delegate: self, startImmediately: false)!
        //        connection.start()
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)

        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            self.data.appendData(data!)
            do {
                let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                print("BUNDLE DOWNLOAD COMPLETE")
                tourDataParser().saveNewTour(jsonResult)
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
    
    func initateConnection(objectId: String){
        startConnection(objectId)
    }
    
}