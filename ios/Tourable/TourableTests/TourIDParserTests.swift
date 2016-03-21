//
//  TourIDParserTests.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 20/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

class TourIDParserTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        NSUserDefaults.standardUserDefaults().setObject([], forKey: "Array")
        NSUserDefaults.standardUserDefaults().removeObjectForKey("KCL-1010")
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testAddingTourId() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "No tours been added yet")
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")![0], ["Ultimate Flat Tour" : "KCL-1010" ], "should be the same, as they are the params passed into the updateArray function")
    }
    
    func testAddingAndDeletingTourId(){
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "No tours been added yet")
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")![0], ["Ultimate Flat Tour" : "KCL-1010" ], "should be the same, as they are the params passed into the updateArray function")
        
        TourIdParser.sharedInstance.deleteTourIdAtRow(0)
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "Tour's been deleted")
    }
    
    func testGetAllTourIdsAndGetAllTourTitles(){
        let myTourIDs = ["KCL-1010","KCL-1111"]
        let myTourTitles = ["Ultimate Flat Tour", "Royal Brompton Test Tour"]
        
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        TourIdParser.sharedInstance.updateArray("KCL-1111", tourTitle: "Royal Brompton Test Tour")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 2, "two tours we added")
        XCTAssertEqual(myTourIDs, TourIdParser.sharedInstance.getAllTourIDs(), "Tours Ids should be identical")
        XCTAssertEqual(myTourTitles, TourIdParser.sharedInstance.getAllTours(), "The tour titles should be identical")
        
    }
    
    func testGettingTourMetaData(){
        
        let metaData = [
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
        
        let tourDict = metaData["tour"] as! NSMutableDictionary
        tourDict["expiresAt"] = metaData["expiresAt"]
        tourDict["updatedAt"] = metaData["updatedAt"]
        tourDict["createdAt"] = metaData["createdAt"]
        
        NSUserDefaults.standardUserDefaults().setObject(tourDict, forKey: "KCL-1010")
        NSUserDefaults.standardUserDefaults().synchronize()
        
        XCTAssertEqual(tourDict, TourIdParser.sharedInstance.getTourMetadata("KCL-1010"), "Should be identical tour metaData")
        
    }
    
    
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measureBlock {
            // Put the code you want to measure the time of here.
        }
    }
    
}
