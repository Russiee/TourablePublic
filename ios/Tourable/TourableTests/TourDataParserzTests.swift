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
    
    
    //Test the download of tour by triggering download and confirming result contains the correct tour markers
    func testTopLevelTourCreation() {
        
        let bundleDownload = bundleRouteConnector()
        bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        let result = tourDataParser().createNewTour(data)
        
        
        //Check result has all asociated types that are required.
        
        XCTAssertNotNil(result)
        XCTAssertNotNil(result.title.rangeOfString("title"))
        XCTAssertNotNil(result.title.rangeOfString("type"))
        XCTAssertNotNil(result.title.rangeOfString("Content"))
        
    }
    
    //Test the persisting a tour to NSUserDefaults, by saving a sample tour and then reading it again to confirm
    
    func testTourSave(){
        let bundleDownload = bundleRouteConnector()
        bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        tourDataParser().saveNewTour(data)
        
        let result = tourDataParser().getTourSection("xI21AHATXD")
        XCTAssertEqual(result.title, "Alex's Room")
        XCTAssertEqual(result.sectionId, "xI21AHATXD")
        
    }
    
    
    //test performance of persisting a tour to NSUserDefaults
    
    func testSavePerformance(){
        
        self.measureBlock {
            let bundleDownload = bundleRouteConnector()
            bundleDownload.initiateBundleConnection("cjWRKDygIZ")
            let data = bundleDownload.getJSONResult()
            tourDataParser().saveNewTour(data)
        }
    }
    
    
    //Test performance of reading a tour from persistant storage
    func testReadPerformance(){
        
        let bundleDownload = bundleRouteConnector()
        bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        tourDataParser().saveNewTour(data)
        
        self.measureBlock{
            let result = tourDataParser().getTourSection("xI21AHATXD")
            XCTAssertEqual(result.title, "Alex's Room")
            XCTAssertEqual(result.sectionId, "xI21AHATXD")
        }
    }
    
    
    func testDownloadPerformanceExample() {
        
        self.measureBlock {
            let bundleDownload = bundleRouteConnector()
            bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        }
    }
    func testParsePerformance(){
        
        
        let bundleDownload = bundleRouteConnector()
        bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        
        self.measureBlock {
            let data = bundleDownload.getJSONResult()
            let _ = tourDataParser().createNewTour(data)
        }
        
    }
    
}
