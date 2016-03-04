//
//  DataCacheTest.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 10/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
import Alan_Touring


class DataCacheTest: XCTestCase{


    
    override func setUp() {
        super.setUp()

        // Put setup code here. This method is called before the invocation of each test method in the class.

    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.

        super.tearDown()
    }

    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
    }
    
    
    func testStringStorage(){
        
        let tourParserForTest = Alan_Touring.tourIdParser.init()

        tourParserForTest.clearArray()
        tourParserForTest.updateArray("test")
    
        let result = tourParserForTest.getAllTours()
        let resultString = result.objectAtIndex(0) as? String
        
        XCTAssertEqual(result.count, 1)

        
    }
    
    func testClearArray(){
        let tourParserForTest = Alan_Touring.tourIdParser.init()
        tourParserForTest.clearArray()
        let result = tourParserForTest.getAllTours()
        XCTAssertEqual(result.count, 0)
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        let tourParserForTest = Alan_Touring.tourIdParser.init()
        self.measureBlock {
            // Put the code you want to measure the time of here.
            tourParserForTest.updateArray("test")
            let result = tourParserForTest.getAllTours()
            _ = result.objectAtIndex(0) as? String
            
        }
    }

}
