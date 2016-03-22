//
//  POIConnectorTests.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 22/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest

class POIConnectorTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testDataReceivedFromConnectionIsWhatWeExpect1() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        
        let expectedData = [
            "section": [
                "__type": "Pointer",
                "className": "Section",
                "objectId": "1VUl5sTKYG"
            ],
            "post": [
                [
                    "type": "Header",
                    "content": "Drying Rack"
                ],
                [
                    "type": "body",
                    "content": " Here is where clothes are laid out to dry. Currently there is only a few articles on the rack, however this is not the norm."
                ],
                [
                    "type": "image",
                    "url": "https://s3-eu-west-1.amazonaws.com/testmediahobbyte/drying+rack.jpg",
                    "description": "#soClean"
                ]
            ],
            "title": "Drying Rack",
            "description": "Clothes clothes clothes",
            "updatedAt": "2016-03-21T16:52:40.676Z",
            "createdAt": "2016-02-24T12:29:00.658Z",
            "objectId": "t7J6rTqTaa"
        ]
        
        
        let expectation = expectationWithDescription("connection to bundle route established")
        
        let urlPath = "https://touring-api.herokuapp.com/api/v1/poi/t7J6rTqTaa"
        
        
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            do {
                let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                XCTAssertEqual(expectedData, jsonResult, "They should be identical")
                
                expectation.fulfill()
            }
            catch _ as NSError{
                print("POIConnector: there was an error parsing a poi")
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
    
    func testPerformanceOfConnectionToPOIObject1() {
        let expectedData = [
            "section": [
                "__type": "Pointer",
                "className": "Section",
                "objectId": "1VUl5sTKYG"
            ],
            "post": [
                [
                    "type": "Header",
                    "content": "Drying Rack"
                ],
                [
                    "type": "body",
                    "content": " Here is where clothes are laid out to dry. Currently there is only a few articles on the rack, however this is not the norm."
                ],
                [
                    "type": "image",
                    "url": "https://s3-eu-west-1.amazonaws.com/testmediahobbyte/drying+rack.jpg",
                    "description": "#soClean"
                ]
            ],
            "title": "Drying Rack",
            "description": "Clothes clothes clothes",
            "updatedAt": "2016-03-21T16:52:40.676Z",
            "createdAt": "2016-02-24T12:29:00.658Z",
            "objectId": "t7J6rTqTaa"
        ]
        
        let urlPath = "https://touring-api.herokuapp.com/api/v1/poi/t7J6rTqTaa"
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        self.measureBlock {
            let expectation = self.expectationWithDescription("connection to bundle route established")
            let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
                do {
                    let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                    XCTAssertEqual(expectedData, jsonResult, "They should be identical")
                    
                    expectation.fulfill()
                }
                catch _ as NSError{
                    print("POIConnector: there was an error parsing a poi")
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
    
    func testDataReceivedFromConnectionIsWhatWeExpect2() {
        
        let expectedData = [
            "section": [
                "__type": "Pointer",
                "className": "Section",
                "objectId": "JogbSzQfIK"
            ],
            "post": [
                [
                    "type": "Header",
                    "content": "Kitchen"
                ],
                [
                    "type": "body",
                    "content": "The kitchen is a crucial part of the flat. This is where 'cooking' takes place, as well as dish washing and the washing of any clothes in the washing machine."
                ],
                [
                    "type": "image",
                    "url": "https://s3-eu-west-1.amazonaws.com/testmediahobbyte/kitchen.jpg",
                    "description": "Look how tidy it is!"
                ]
            ],
            "title": "Kitchen",
            "description": "The kitchen is a crucial part of the flat.",
            "updatedAt": "2016-03-21T16:52:38.566Z",
            "createdAt": "2016-02-24T12:29:11.173Z",
            "objectId": "LFcBomfAFz"
        ]
        
        
        let expectation = expectationWithDescription("connection to bundle route established")
        
        let urlPath = "https://touring-api.herokuapp.com/api/v1/poi/LFcBomfAFz"
        
        
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
            do {
                let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                XCTAssertEqual(expectedData, jsonResult, "They should be identical")
                
                expectation.fulfill()
            }
            catch _ as NSError{
                print("POIConnector: there was an error parsing a poi")
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
    
    func testPerformanceOfConnectionToPOIObject2(){
        
        let expectedData = [
            "section": [
                "__type": "Pointer",
                "className": "Section",
                "objectId": "JogbSzQfIK"
            ],
            "post": [
                [
                    "type": "Header",
                    "content": "Kitchen"
                ],
                [
                    "type": "body",
                    "content": "The kitchen is a crucial part of the flat. This is where 'cooking' takes place, as well as dish washing and the washing of any clothes in the washing machine."
                ],
                [
                    "type": "image",
                    "url": "https://s3-eu-west-1.amazonaws.com/testmediahobbyte/kitchen.jpg",
                    "description": "Look how tidy it is!"
                ]
            ],
            "title": "Kitchen",
            "description": "The kitchen is a crucial part of the flat.",
            "updatedAt": "2016-03-21T16:52:38.566Z",
            "createdAt": "2016-02-24T12:29:11.173Z",
            "objectId": "LFcBomfAFz"
        ]
        
        let urlPath = "https://touring-api.herokuapp.com/api/v1/poi/LFcBomfAFz"
        
        let request = NSURLRequest(URL: NSURL(string: urlPath)!)
        let config = NSURLSessionConfiguration.defaultSessionConfiguration()
        let session = NSURLSession(configuration: config)
        
        self.measureBlock{
            let expectation = self.expectationWithDescription("connection to bundle route established")
            let task = session.dataTaskWithRequest(request) { (data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
                do {
                    let jsonResult: NSDictionary = try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! NSDictionary
                    XCTAssertEqual(expectedData, jsonResult, "They should be identical")
                    
                    expectation.fulfill()
                }
                catch _ as NSError{
                    print("POIConnector: there was an error parsing a poi")
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
