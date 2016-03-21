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
    
}
