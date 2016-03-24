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
 
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "No tours been added yet")
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")![0] as! [String : String], ["Ultimate Flat Tour" : "KCL-1010" ], "should be the same, as they are the params passed into the updateArray function")
    }
    
    func testAddingAndDeletingTourId(){
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "No tours been added yet")
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")![0] as! [String : String], ["Ultimate Flat Tour" : "KCL-1010" ], "should be the same, as they are the params passed into the updateArray function")
        
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
    
    func testAddTourMetaData(){
        //create mock data
        
        let data = [
            "tour": [
                "__type": "Pointer",
                "className": "Tour",
                "objectId": "cjWRKDygIZ"
            ] as NSMutableDictionary,
            "code": "KCL-1010",
            "updatedAt": "2016-03-20T12:10:42.175Z",
            "createdAt": "2016-03-18T10:50:47.172Z",
            "expiry": "2016-06-19T00:00:00.000Z",
            "objectId": "ZX8DHpGKxk"
        ]
        TourIdParser().addTourMetaData(data)
        sleep(1)
        let returnedData = TourIdParser().getTourMetadata("KCL-1010")
        let originalData = data["tour"] as! NSDictionary
        
        
        XCTAssertEqual(originalData["objectId"] as? String, returnedData["objectId"] as? String)
        XCTAssertEqual(data["updatedAt"] as? String, returnedData["updatedAt"] as? String)
    }
    
    func testDeleteTourIdAtRow(){
        let data = [
            "tour": [
                "__type": "Pointer",
                "className": "Tour",
                "objectId": "cjWRKDygIZ"
                ] as NSMutableDictionary,
            "code": "KCL-1010",
            "updatedAt": "2016-03-20T12:10:42.175Z",
            "createdAt": "2016-03-18T10:50:47.172Z",
            "expiry": "2016-06-19T00:00:00.000Z",
            "objectId": "ZX8DHpGKxk"
        ]
        TourIdParser().addTourMetaData(data)
        
        XCTAssertEqual(TourIdParser().getAllTours().count, 1)
        TourIdParser().deleteTourIdAtRow(0)
        XCTAssertEqual(TourIdParser().getAllTours().count, 0)
        
    }
    
    
    func testAddingTourIdPerformance() {
       
        self.measureBlock{
            TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        }
       
    }
    
    func testAddingAndDeletingTourIdPerformance(){

        self.measureBlock{
            TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
            TourIdParser.sharedInstance.deleteTourIdAtRow(0)
        }

    }
    
}
