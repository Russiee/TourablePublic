//
//  ImageHandlerTests.swift
//  Tourable
//
//  Created by Alex Gubbay on 19/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

class ImageHandlerTests: XCTestCase {
    
    var imagesToDownload = 0
    var imagesDownloaded = 0
    
    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedDownloading", name: beginDownloadKey, object: nil)
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedFinishedDownloading", name: endDownloadKey, object: nil)
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }
    
    func testImagePersist() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.

        let image = UIImage(named: "test")
        TourDeleter().deleteMedia("test", fileType: ".png")
        XCTAssertTrue(imageHandler.sharedInstance.saveImage(image!, name: "test"))
        let image2 = imageHandler.sharedInstance.loadImageFromPath("test")
        XCTAssertNotNil(image2)
        XCTAssertEqual(image?.size.height, (image2?.size.height)!/2)
        XCTAssertEqual(image?.size.width, (image2?.size.width)!/2)
  
    }
    
    func testEmptyImageLoad(){
        
        let testImage = imageHandler.sharedInstance.loadImageFromPath("BlankTestImage")
        XCTAssertNil(testImage)
        
        
        let nilTestImage = imageHandler.sharedInstance.loadImageFromPath(nil)
        XCTAssertNil(nilTestImage)
        
    }
    
    func testImageQueue(){
        
        var urls = [String]()
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/alex%27s+bed.jpg")
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/alex%27s+desk.jpg")
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/peter%27s+bed.jpg")
        imageHandler.sharedInstance.queueImage(urls)
        XCTAssertEqual(imageHandler.sharedInstance.imageQueue.count, 3)
        imageHandler.sharedInstance.imageQueue.removeAll()
        XCTAssertEqual(imageHandler.sharedInstance.imageQueue.count, 0)
        
    }
    
    func NotifiedDownloading(){
        imagesToDownload++
        print(imagesToDownload)
        print(imagesDownloaded)
    }
    
    func NotifiedFinishedDownloading(){
        imagesDownloaded++
    }
    
     func testImageDownloadComplete(){
    
        
        var urls = [String]()
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/alex%27s+bed.jpg")
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/alex%27s+desk.jpg")
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/peter%27s+bed.jpg")
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/sample_video.mp4")
        
        imageHandler.sharedInstance.queueImage(urls)
        
        imageHandler.sharedInstance.downloadMediaSet(imageHandler.sharedInstance.imageQueue)
         let expectation = self.expectationWithDescription("ImageDownloadComplete")
        
        while imagesToDownload != urls.count-1{
            
        }
        if(imagesToDownload == urls.count-1){
            
            expectation.fulfill()
            
        }else{
            XCTFail()
        }
        self.waitForExpectationsWithTimeout(30) { error in
            if let error = error {
                print("Error: \(error.localizedDescription)")
                XCTFail()
            }
            
        }
        
    }
    
    func testImageQueuePerformance(){
        
        var urls = [String]()
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/alex%27s+bed.jpg")
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/alex%27s+desk.jpg")
        urls.append("https://s3-eu-west-1.amazonaws.com/testmediahobbyte/peter%27s+bed.jpg")
        
        self.measureBlock{
            imageHandler.sharedInstance.queueImage(urls)
            XCTAssertEqual(imageHandler.sharedInstance.imageQueue.count, 3)
            imageHandler.sharedInstance.imageQueue.removeAll()
            XCTAssertEqual(imageHandler.sharedInstance.imageQueue.count, 0)
        }
        
    }
    func testEmptyImageLoadPerformance(){
        
        self.measureBlock{
            let testImage = imageHandler.sharedInstance.loadImageFromPath("BlankTestImage")
            XCTAssertNil(testImage)
        }
    }
    func testNilImageLoadPerformance() {
        // This is an example of a performance test case.
        self.measureBlock {
            
            let nilTestImage = imageHandler.sharedInstance.loadImageFromPath(nil)
            XCTAssertNil(nilTestImage)
        }
    }
    
    func testImagePersistPerformance(){
        
        func testImagePersist() {
            // This is an example of a functional test case.
            // Use XCTAssert and related functions to verify your tests produce the correct results.
            
            let image = UIImage(named: "test")
            TourDeleter().deleteMedia("test", fileType: ".png")
            
            self.measureBlock{
            XCTAssertTrue(imageHandler.sharedInstance.saveImage(image!, name: "test"))
            let image2 = imageHandler.sharedInstance.loadImageFromPath("test")
            }
            
                
            
        }
    }
    
}
