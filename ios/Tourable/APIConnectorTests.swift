//
//  APIConnectorTests.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 18/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable
class APIConnectorTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        var array = NSUserDefaults.standardUserDefaults().objectForKey("Array")
        array = []
        NSUserDefaults.standardUserDefaults().setObject(array, forKey: "Array")
        NSUserDefaults.standardUserDefaults().synchronize()

        
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
        
    }
    
    func testDownloadKCL1010() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        let expectation = expectationWithDescription("connecting to api")
        let ourData = NSMutableData()
        var jsonResultFromAPI = NSDictionary()
        let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1010"
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            //XCTAssertNotNil(ourData, "the data should be nil as we havent received anything yet")
            XCTAssertNil(error, "there shouldn't be any error")
            
            ourData.appendData(data!)
            do {
                jsonResultFromAPI = try NSJSONSerialization.JSONObjectWithData(ourData, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                    expectation.fulfill()
                
                
            }
            catch let err as NSError{
                //Need to let user know if the tourID they entered was faulty here
                print(err.description)
            }
            
        }
        task.resume()
        
        waitForExpectationsWithTimeout(task.originalRequest!.timeoutInterval) { error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
            }
            task.cancel()
        }
        
        
    }
    
    func testPerformanceOfAPIConnection() {
        self.measureBlock {
            let expectation = self.expectationWithDescription("connecting to api")
            let ourData = NSMutableData()
            var jsonResultFromAPI = NSDictionary()
            let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1010"
            let request = NSURLRequest(URL: NSURL(string: urlPath)!)
            let config = NSURLSessionConfiguration.defaultSessionConfiguration()
            let session = NSURLSession(configuration: config)
            
            let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
                //XCTAssertNotNil(ourData, "the data should be nil as we havent received anything yet")
                XCTAssertNil(error, "there shouldn't be any error")
                
                ourData.appendData(data!)
                do {
                    jsonResultFromAPI = try NSJSONSerialization.JSONObjectWithData(ourData, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                    expectation.fulfill()
                    
                    
                }
                catch let err as NSError{
                    //Need to let user know if the tourID they entered was faulty here
                    print(err.description)
                }
                
            }
            task.resume()
            
            self.waitForExpectationsWithTimeout(task.originalRequest!.timeoutInterval) { error in
                if let error = error {
                    print("Error: \(error.localizedDescription)")
                }
                task.cancel()
            }

        }
    }
    
}
