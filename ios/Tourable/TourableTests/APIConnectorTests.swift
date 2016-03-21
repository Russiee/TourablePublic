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
        let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1010"
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        self.measureBlock {
            let expectation = self.expectationWithDescription("connecting to api")
            
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
    
    //the checking if data is whats expected tests will fail if the API team change the expiry dates!
    
    func testDataFromKCL1010IsExpected(){
        
        let expectedKCL1010VerifierData = [
            "tour": [
                "__type": "Pointer",
                "className": "Tour",
                "objectId": "cjWRKDygIZ"
            ],
            "code": "KCL-1010",
            "updatedAt": "2016-03-20T12:10:42.175Z",
            "createdAt": "2016-03-18T10:50:47.172Z",
            "expiry": "2016-06-19T00:00:00.000Z",
            "objectId": "ZX8DHpGKxk"
        ]
        
        let expectation = expectationWithDescription("connection to api established and the data retrieved is correct")
        let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1010"
        let myData = NSMutableData()
        var JSONMetadataFromAPI: NSDictionary!
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            
            myData.appendData(data!)
            do {
                let jsonResult = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                JSONMetadataFromAPI = jsonResult
                
            }
            catch let err as NSError{
                print(err.description)
            }
            
            XCTAssertNil(error, "there shouldn't be any error")
            XCTAssertEqual(JSONMetadataFromAPI, expectedKCL1010VerifierData, "These should be identical")
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
    
    //the checking if data is whats expected tests will fail if the API team change the expiry dates!
    
    func testDataFromKCL1111IsExpected(){
        
        let expectedKCL111VerifierData = [
            "tour": [
                "__type": "Pointer",
                "className": "Tour",
                "objectId": "GpSEMT3hmG"
            ],
            "code": "KCL-1111",
            "updatedAt": "2016-03-20T11:54:18.586Z",
            "createdAt": "2016-03-18T10:51:04.357Z",
            "expiry": "2016-03-21T00:00:00.000Z",
            "objectId": "BvBRrYZNPU"
        ]
        
        let expectation = expectationWithDescription("connection to api established and data retrieved is correct")
        let urlPath = "https://touring-api.herokuapp.com/api/v1/key/verify/KCL-1111"
        let myData = NSMutableData()
        var JSONMetadataFromAPI: NSDictionary!
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            
            myData.appendData(data!)
            do {
                let jsonResult = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                JSONMetadataFromAPI = jsonResult
                
            }
            catch let err as NSError{
                print(err.description)
            }
            
            XCTAssertNil(error, "there shouldn't be any error")
            XCTAssertEqual(JSONMetadataFromAPI, expectedKCL111VerifierData, "These should be identical")
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
    
    func testCleanTourIds(){
        let test1 = "    KCL- 1  0 1     0"
        let test2 = "///KC/L-10/1/0/"
        let test3 = "\\KCL-\\1010"
        let test4 = "\"KCL\"-1010"
        let test5 = "KC;L-;10;10"
        let test6 = " K\\C/L;-1\"010 "
        let ans = "KCL-1010"
        XCTAssertEqual(ApiConnector.sharedInstance.cleanTourId(test1), ans, "should be same after trim")
        XCTAssertEqual(ApiConnector.sharedInstance.cleanTourId(test2), ans, "should be same after trim")
        XCTAssertEqual(ApiConnector.sharedInstance.cleanTourId(test3), ans, "should be same after trim")
        XCTAssertEqual(ApiConnector.sharedInstance.cleanTourId(test4), ans, "should be same after trim")
        XCTAssertEqual(ApiConnector.sharedInstance.cleanTourId(test5), ans, "should be same after trim")
        XCTAssertEqual(ApiConnector.sharedInstance.cleanTourId(test6), ans, "should be same after trim")
        
    }
    
    func testConnectivety(){
        let connection = ApiConnector.sharedInstance.isConnectedToNetwork()
        if connection {
            //as connection succeeded should be true
            XCTAssertTrue(connection)
        } else{
            //as conection failed should be false
            XCTAssertFalse(connection)
        }
    }
    
}
