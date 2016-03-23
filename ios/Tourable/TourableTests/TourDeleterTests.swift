//
//  TourDeleterTests.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 20/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

class TourDeleterTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        NSUserDefaults.standardUserDefaults().setObject([] , forKey: "Array")

    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
        
    }
    
    func testDeletionOfOneTour_KCL1010() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "Should be empty array")
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
        
        NSUserDefaults.standardUserDefaults().setObject(metaData["tour"], forKey: "KCL-1010")
        NSUserDefaults.standardUserDefaults().synchronize()
        let bundleDownload = bundleRouteConnector()
        bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        _ = tourDataParser().saveNewTour(data)
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 1, "There should be only one tour in our array, KCL-1010")
        XCTAssertNotNil(NSUserDefaults.standardUserDefaults().objectForKey("cjWRKDygIZ"), "This is our downloaded tour. so should exist")
        TourDeleter.sharedInstance.deleteTour("KCL-1010")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "Should be empty array after deleting")
        XCTAssertNil(NSUserDefaults.standardUserDefaults().objectForKey("cjWRKDygIZ"), "Tour should be deleted now")
    }
    
    
    //testing performance of TourDeleter deleting tours already downloaded in NSUserDefaults
    func testDeletionOfOneTourWhen2Exist_KCL1111_KCL1010() {
        // This is an example of a performance test case.
        
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 0, "Should be empty array")
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
        
        NSUserDefaults.standardUserDefaults().setObject(metaData["tour"], forKey: "KCL-1010")
        NSUserDefaults.standardUserDefaults().synchronize()
        let bundleDownload = bundleRouteConnector()
        bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        _ = tourDataParser().saveNewTour(data)
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 1, "Should have 1 tour in the array")
        let metaData2 = [
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
        
        NSUserDefaults.standardUserDefaults().setObject(metaData2["tour"], forKey: "KCL-1111")
        NSUserDefaults.standardUserDefaults().synchronize()
        let bundleDownload2 = bundleRouteConnector()
        bundleDownload2.initiateBundleConnection("GpSEMT3hmG")
        let data2 = bundleDownload2.getJSONResult()
        _ = tourDataParser().saveNewTour(data2)
        TourIdParser.sharedInstance.updateArray("KCL-1111", tourTitle: "Royal Brompton Hospital")
        
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 2, "There should be 2 tours in our array, KCL-1010 and KCL-1111")
        XCTAssertNotNil(NSUserDefaults.standardUserDefaults().objectForKey("cjWRKDygIZ"), "This is our downloaded tour. so should exist")
        XCTAssertNotNil(NSUserDefaults.standardUserDefaults().objectForKey("GpSEMT3hmG"), "This is our other downloaded tour. so should exist")
            TourDeleter.sharedInstance.deleteTour("KCL-1111")
        XCTAssertEqual(NSUserDefaults.standardUserDefaults().objectForKey("Array")!.count, 1, "Should have only 1 tour now")
        XCTAssertNil(NSUserDefaults.standardUserDefaults().objectForKey("GpSEMT3hmG"), "Tour should be deleted now")
        XCTAssertNotNil(NSUserDefaults.standardUserDefaults().objectForKey("cjWRKDygIZ"), "Hasnt been deleted")
    }
    
    
    func testDeletingMedia(){
        let url = "https://s3-eu-west-1.amazonaws.com/testmediahobbyte/sample_video.mp4"
        videoHandler.sharedInstance.downloadVideo(url)
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
        
        NSUserDefaults.standardUserDefaults().setObject(metaData["tour"], forKey: "KCL-1010")
        NSUserDefaults.standardUserDefaults().synchronize()
        let bundleDownload = bundleRouteConnector()
        bundleDownload.initiateBundleConnection("cjWRKDygIZ")
        let data = bundleDownload.getJSONResult()
        _ = tourDataParser().saveNewTour(data)
        TourIdParser.sharedInstance.updateArray("KCL-1010", tourTitle: "Ultimate Flat Tour")
        TourDeleter.sharedInstance.deleteMediaInTour("KCL-1010")
        let fileName = String(url.hash)
        let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileName,fileType: ".mp4")
        XCTAssertFalse(MediaHelper.sharedInstance.checkFileExists(path), "This video should be deleted")
        TourDeleter.sharedInstance.deleteTour("KCL-1010")
        
    }
    
    
}
