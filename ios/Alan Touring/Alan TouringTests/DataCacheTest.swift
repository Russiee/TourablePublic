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
          var tour_id = Alan_Touring.tourIdParser.init()
        // Put setup code here. This method is called before the invocation of each test method in the class.
        NSUserDefaults.resetStandardUserDefaults()
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        NSUserDefaults.resetStandardUserDefaults()
        super.tearDown()
    }

    func testExample() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.

    }
    
    func testStringStorage(){
        var tourParserForTest = Alan_Touring.tourIdParser.init()

        
    }

    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measureBlock {
            // Put the code you want to measure the time of here.
        }
    }

}
