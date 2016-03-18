//
//  TestSet1.swift
//  Tourable
//
//  Created by Alex Gubbay on 18/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

class TestTourDataParser: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testTopLevelTourCreation() {
        
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        let bundleDownload = bundleRouteConnector()
        bundleDownload.startConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        let result = tourDataParser().createNewTour(data)

        //Check result has all asocited types that are required.
        XCTAssertNotNil(result)
        XCTAssertNotNil(result.title.rangeOfString("title"))
        XCTAssertNotNil(result.title.rangeOfString("type"))
        XCTAssertNotNil(result.title.rangeOfString("Content"))
        
    }
    
    func testTourSave(){
        let bundleDownload = bundleRouteConnector()
        bundleDownload.startConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        tourDataParser().saveNewTour(data)
        
        let result = tourDataParser().getTourSection("xI21AHATXD")
        print(result.sectionId)
        XCTAssertEqual(result.title, "Alex's Room")
        XCTAssertEqual(result.sectionId, "xI21AHATXD")

    }
    
    func testSavePerformance(){
        
        self.measureBlock {
            let bundleDownload = bundleRouteConnector()
            bundleDownload.startConnection("cjWRKDygIZ")
            let data = bundleDownload.getJSONResult()
            tourDataParser().saveNewTour(data)
        }
    }
    
    func testReadPerformance(){
        
        let bundleDownload = bundleRouteConnector()
        bundleDownload.startConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        tourDataParser().saveNewTour(data)
        
        self.measureBlock{
            let result = tourDataParser().getTourSection("xI21AHATXD")
            print(result.sectionId)
            XCTAssertEqual(result.title, "Alex's Room")
            XCTAssertEqual(result.sectionId, "xI21AHATXD")
        }
    }


    func testDownloadPerformanceExample() {
        
        self.measureBlock {
            let bundleDownload = bundleRouteConnector()
            bundleDownload.startConnection("cjWRKDygIZ")
        }
    }
    func testParsePerformance(){
       
        
        let bundleDownload = bundleRouteConnector()
        bundleDownload.startConnection("cjWRKDygIZ")
        
        self.measureBlock {
            let data = bundleDownload.getJSONResult()
            let _ = tourDataParser().createNewTour(data)
        }
        
    }

}
