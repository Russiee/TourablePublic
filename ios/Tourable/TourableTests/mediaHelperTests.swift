//
//  mediaHelper.swift
//  Tourable
//
//  Created by Alex Gubbay on 18/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

class mediaHelper: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testVideoDownload() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        MediaHelper.sharedInstance.getDataFromUrl(NSURL(string: "https://s3-eu-west-1.amazonaws.com/testmediahobbyte/sample_video.mp4")!) { (data, response, error)  in
            dispatch_sync(dispatch_get_main_queue()) { () -> Void in
                guard let data = data where error == nil else {
                    return
                }
                //Save the data from the server as a video, with the url as its name.
                XCTAssertNotNil(data)
            }
            XCTAssertNotNil(data)
        }
    }
    
    func testImageDownload(){
        
        MediaHelper.sharedInstance.getDataFromUrl(NSURL(string: "https://s3-eu-west-1.amazonaws.com/testmediahobbyte/alex%27s+desk.jpg")!) { (data, response, error)  in
            dispatch_sync(dispatch_get_main_queue()) { () -> Void in
                guard let data = data where error == nil else {
                    return
                }
                
                XCTAssertNotNil(data)
            }
            XCTAssertNotNil(data)
        }
    }
    
    
    func testCheckFileExists(){
            
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measureBlock {
            // Put the code you want to measure the time of here.
        }
    }

}
