//
//  POIParserTests.swift
//  Tourable
//
//  Created by Alex Gubbay on 19/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

class POIParserTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testMockPOICreation() {
       
        //Create mock data
        var post = [NSDictionary]()
        var postObject = Dictionary<String, String>()
        postObject["type"] = "Header"
        postObject["content"] = "Test data"
        let postNSDictonary = postObject as NSDictionary
        post.append(postNSDictonary)
        
        let section = NSDictionary()

        var data = Dictionary<String, AnyObject>()
        data["objectId"] = "testing123"
        data["description"] = "This is a test point of interest object"
        data["createdAt"] = "2016-03-13T17:42:09.534Z"
        data["post"] = post as NSArray
        data["section"] = section
        data["title"] = "Test Point of Interest 1"
        data["updatedAt"] = "2016-03-13T17:42:09.534Z"
        
        let result = POIParser().createNewPOI(data)
        XCTAssertNotNil(result)
        XCTAssertEqual(result.objectId, "testing123")
        XCTAssertEqual(result.description, "This is a test point of interest object")
        XCTAssertEqual(result.createdAt, "2016-03-13T17:42:09.534Z")
        XCTAssertEqual(result.post[0]["type"], "Header")
        XCTAssertTrue(result.section.isKindOfClass(NSDictionary))
        XCTAssertEqual(result.title, "Test Point of Interest 1")
        XCTAssertEqual(result.updatedAt, "2016-03-13T17:42:09.534Z")
        
    }
    
    func testPersistOfMockPOI(){
    
        
        //Create mock data
        var post = [NSDictionary]()
        var postObject = Dictionary<String, String>()
        postObject["type"] = "Header"
        postObject["content"] = "Test data"
        let postNSDictonary = postObject as NSDictionary
        post.append(postNSDictonary)
        
        let section = NSDictionary()
        
        var data = Dictionary<String, AnyObject>()
        data["objectId"] = "testing123"
        data["description"] = "This is a test point of interest object"
        data["createdAt"] = "2016-03-13T17:42:09.534Z"
        data["post"] = post as NSArray
        data["section"] = section
        data["title"] = "Test Point of Interest 1"
        data["updatedAt"] = "2016-03-13T17:42:09.534Z"
        
        POIParser().savePOI(data)
       let result = POIParser().getTourSection("testing123")
        XCTAssertNotNil(result)
        XCTAssertEqual(result.objectId, "testing123")
        XCTAssertEqual(result.description, "This is a test point of interest object")
        XCTAssertEqual(result.createdAt, "2016-03-13T17:42:09.534Z")
        XCTAssertEqual(result.post[0]["type"], "Header")
        XCTAssertTrue(result.section.isKindOfClass(NSDictionary))
        XCTAssertEqual(result.title, "Test Point of Interest 1")
        XCTAssertEqual(result.updatedAt, "2016-03-13T17:42:09.534Z")


    }
    
    func testMockPOIPersistPerformance(){
    
        //Create mock data
        var post = [NSDictionary]()
        var postObject = Dictionary<String, String>()
        postObject["type"] = "Header"
        postObject["content"] = "Test data"
        let postNSDictonary = postObject as NSDictionary
        post.append(postNSDictonary)
        
        let section = NSDictionary()
        
        var data = Dictionary<String, AnyObject>()
        data["objectId"] = "testing123"
        data["description"] = "This is a test point of interest object"
        data["createdAt"] = "2016-03-13T17:42:09.534Z"
        data["post"] = post as NSArray
        data["section"] = section
        data["title"] = "Test Point of Interest 1"
        data["updatedAt"] = "2016-03-13T17:42:09.534Z"
        self.measureBlock{
            
            POIParser().savePOI(data)
        }
    }
    
    func testReadMockDataFromPersistance(){
        
        
        //Create mock data
        var post = [NSDictionary]()
        var postObject = Dictionary<String, String>()
        postObject["type"] = "Header"
        postObject["content"] = "Test data"
        let postNSDictonary = postObject as NSDictionary
        post.append(postNSDictonary)
        
        let section = NSDictionary()
        
        var data = Dictionary<String, AnyObject>()
        data["objectId"] = "testing123"
        data["description"] = "This is a test point of interest object"
        data["createdAt"] = "2016-03-13T17:42:09.534Z"
        data["post"] = post as NSArray
        data["section"] = section
        data["title"] = "Test Point of Interest 1"
        data["updatedAt"] = "2016-03-13T17:42:09.534Z"
        POIParser().savePOI(data)

        self.measureBlock{
        
        let result = POIParser().getTourSection("testing123")
            
        }
        
    }

    func testPOICreationPerformance() {
        // This is an example of a performance test case.
       
        //Create mock data
        var post = [NSDictionary]()
        var postObject = Dictionary<String, String>()
        postObject["type"] = "Header"
        postObject["content"] = "Test data"
        let postNSDictonary = postObject as NSDictionary
        post.append(postNSDictonary)
        
        let section = NSDictionary()
        
        var data = Dictionary<String, AnyObject>()
        data["objectId"] = "testing123"
        data["description"] = "This is a test point of interest object"
        data["createdAt"] = "2016-03-13T17:42:09.534Z"
        data["post"] = post as NSArray
        data["section"] = section
        data["title"] = "Test Point of Interest 1"
        data["updatedAt"] = "2016-03-13T17:42:09.534Z"
        
        var result = pointOfInterest()
        self.measureBlock {
            
            result = POIParser().createNewPOI(data)
        }
        
        
    }

}
