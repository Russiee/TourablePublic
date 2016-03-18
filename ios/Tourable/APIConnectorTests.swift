//
//  APIConnectorTests.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 18/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

//tests just ot check that a connection is being established with the API, so only a connectivety 
//tested here and not what happens with the data, as we arent testing that here
//testing connection to our 2 test tours KCL-1010 and KCL-1111

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
    
    func testConnectionToAPIWithKCL1010() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        let expectation = expectationWithDescription("connection to api established")
        let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1010"
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
    
    
    func testPerformanceOfAPIConnectionWithKCL1010() {
        self.measureBlock {
            let expectation = self.expectationWithDescription("connecting to api")
            let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1010"
            let request = NSURLRequest(URL: NSURL(string: urlPath)!)
            let config = NSURLSessionConfiguration.defaultSessionConfiguration()
            let session = NSURLSession(configuration: config)
            
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
    
    func testConnectionToAPIWithKCL1111(){
        let expectation = expectationWithDescription("connecting to api")
        let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1111"
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
    
    func testPerformanceOfAPIConnectionWithKCL1111() {
        self.measureBlock {
            let expectation = self.expectationWithDescription("connecting to api")
            let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1111"
            let request = NSURLRequest(URL: NSURL(string: urlPath)!)
            let config = NSURLSessionConfiguration.defaultSessionConfiguration()
            let session = NSURLSession(configuration: config)
            
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
    
}
