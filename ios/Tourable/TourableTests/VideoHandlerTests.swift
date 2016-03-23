//
//  VideoHandlerTests.swift
//  Tourable
//
//  Created by Alex Gubbay on 18/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//


import XCTest
@testable import Tourable

class VideoHandlerTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

   func testDownload(){
        //Sanitise environment
        TourDeleter.sharedInstance.deleteMedia("https://s3-eu-west-1.amazonaws.com/practicemediabrompton/ER_heart_test.mp4", fileType: ".mp4")
        let video = videoHandler().loadVideoPath("https://s3-eu-west-1.amazonaws.com/practicemediabrompton/ER_heart_test.mp4")
        XCTAssertNotNil(video)
        XCTAssertEqual(video?.absoluteString, "https://s3-eu-west-1.amazonaws.com/practicemediabrompton/ER_heart_test.mp4")
    }
}
