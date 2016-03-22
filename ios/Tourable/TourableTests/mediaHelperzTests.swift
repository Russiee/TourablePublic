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
    
    func testImageDownload() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        
        let expectation = expectationWithDescription("imageDownload")
        MediaHelper.sharedInstance.getDataFromUrl(NSURL(string: "https://i.imgur.com/6UMNM1j.jpg")!) { (data, response, error)  in
            dispatch_async(dispatch_get_main_queue()) { () -> Void in
                guard let _ = data where error == nil else {
                    XCTFail()
                    return
                    
                }
                expectation.fulfill()
            }
        }
        
        self.waitForExpectationsWithTimeout(20) { error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
            }
        }
    }
    
    func testVideoDownload() {
        
        let expectation = expectationWithDescription("videoDownload")
        MediaHelper.sharedInstance.getDataFromUrl(NSURL(string: "https://s3-eu-west-1.amazonaws.com/practicemediabrompton/ER_heart_test.mp4")!) { (data, response, error)  in
            dispatch_async(dispatch_get_main_queue()) { () -> Void in
                guard let _ = data where error == nil else {
                    XCTFail()
                    return
                    
                }
                expectation.fulfill()
            }
        }
        
        self.waitForExpectationsWithTimeout(60) { error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
            }
        }
    }
    
    
    
    func testFileExists(){
        
        let image = UIImage(named: "PlayButton")
        let pngImageData = UIImagePNGRepresentation(image!)
        let path = MediaHelper.sharedInstance.fileInDocumentsDirectory("playButton", fileType: ".png")
        let result = pngImageData!.writeToFile(path, atomically: true)
        
        XCTAssertTrue(result)
        
        XCTAssertTrue(MediaHelper.sharedInstance.checkFileExists(path))
    }
    
    
    
    
    func testExistCheckPerformance() {
        // This is an example of a performance test case.
        
        let image = UIImage(named: "PlayButton")
        let pngImageData = UIImagePNGRepresentation(image!)
        let path = MediaHelper.sharedInstance.fileInDocumentsDirectory("playButton", fileType: ".png")
        let result = pngImageData!.writeToFile(path, atomically: true)
        
        XCTAssertTrue(result)
        self.measureBlock {
            XCTAssertTrue(MediaHelper.sharedInstance.checkFileExists(path))
        }
    }
    
    func testImageDLPerformance(){
        self.measureBlock{
            let expectation = self.expectationWithDescription("ImageDownloadPerformance")
            MediaHelper.sharedInstance.getDataFromUrl(NSURL(string: "https://i.imgur.com/6UMNM1j.jpg")!) { (data, response, error)  in
                dispatch_async(dispatch_get_main_queue()) { () -> Void in
                    guard let _ = data where error == nil else {
                        XCTFail()
                        return
                        
                    }
                    expectation.fulfill()
                }
            }
            
            self.waitForExpectationsWithTimeout(60) { error in
                if let error = error {
                    print("Error: \(error.localizedDescription)")
                }
            }
        }
    }
    
    func testVideoDownloadPerformance(){
        
        self.measureBlock{
            let expectation = self.expectationWithDescription("videoDownloadPerformance")
            MediaHelper.sharedInstance.getDataFromUrl(NSURL(string: "https://s3-eu-west-1.amazonaws.com/practicemediabrompton/ER_heart_test.mp4")!) { (data, response, error)  in
                dispatch_async(dispatch_get_main_queue()) { () -> Void in
                    guard let _ = data where error == nil else {
                        XCTFail()
                        return
                        
                    }
                    expectation.fulfill()
                }
            }
            
            self.waitForExpectationsWithTimeout(60) { error in
                if let error = error {
                    print("Error: \(error.localizedDescription)")
                }
            }
        }
    }
}
