//
//  BundleRouteConnectorTests.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 21/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable
class BundleRouteConnectorTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testConnectionToBundleRoute() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        
        let expectation = expectationWithDescription("connection to bundle route established")
        let urlPath = "https://touring-api.herokuapp.com/api/v1/bundle/cjWRKDygIZ"
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            //XCTAssertNotNil(ourData, "the data should be nil as we havent received anything yet")
            XCTAssertNil(error, "there shouldn't be any error")
            expectation.fulfill()
            
        }
        task.resume()
        
        waitForExpectationsWithTimeout(task.originalRequest!.timeoutInterval) { error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
            }
            task.cancel()
        }
    }
    
    func testPerformanceOfConnectionToKCL1010Bundle() {
        // This is an example of a performance test case.
      
        let urlPath = "https://touring-api.herokuapp.com/api/v1/bundle/cjWRKDygIZ"
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        self.measureBlock {
            let expectation = self.expectationWithDescription("connection to bundle route established")
            let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
                //XCTAssertNotNil(ourData, "the data should be nil as we havent received anything yet")
                XCTAssertNil(error, "there shouldn't be any error")
                expectation.fulfill()
                
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
    
    func testExpectedDataReceivedFromBundleRouteConnection(){
        
        let bundleRouteDataDescription = "This tour is for testing and review use only and will not be released to the public. Public tours will include the Royal Brompton Hospital Cardiac Imaging Department"
        let bundleRouteDatatitle = "Ultimate Flat Tour"
        
        let expectation = expectationWithDescription("connection to bundle route established")
        let urlPath = "https://touring-api.herokuapp.com/api/v1/bundle/cjWRKDygIZ"
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            
            do {
                 let jsonResultFromAPI = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                //passing through the array of sections
                XCTAssertEqual((jsonResultFromAPI["description"] as! String), bundleRouteDataDescription, "they should be identical")
                XCTAssertEqual((jsonResultFromAPI["title"] as! String), bundleRouteDatatitle, "titles should match")
                
            }
            catch let err as NSError{
                //Need to let user know if the tourID they entered was faulty here
                print(err.description)
            }
            XCTAssertNil(error, "there shouldn't be any error")
            expectation.fulfill()
            
        }
        task.resume()
        
        waitForExpectationsWithTimeout(task.originalRequest!.timeoutInterval) { error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
            }
            task.cancel()
        }
    }
    
    func testGetJSONResult(){
        let bundleRouteDataDescription = "This tour is for testing and review use only and will not be released to the public. Public tours will include the Royal Brompton Hospital Cardiac Imaging Department"
        let bundleRouteDatatitle = "Ultimate Flat Tour"
        let brc = bundleRouteConnector()
        brc.startConnection("cjWRKDygIZ")
        let jsonResultFromAPI = brc.getJSONResult()
        XCTAssertEqual(jsonResultFromAPI["description"] as? String, bundleRouteDataDescription, "they should be identical")
        XCTAssertEqual(jsonResultFromAPI["title"] as? String, bundleRouteDatatitle, "titles should match")

        
    }
    
}
