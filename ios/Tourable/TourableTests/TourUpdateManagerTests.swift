//
//  TourUpdateManagerTests.swift
//  Tourable
//
//  Created by Federico on 12/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest
@testable import Tourable

class TourUpdateManagerTests: XCTestCase {

    override func setUp() {
        super.setUp()
        // Put setup code here. This method is called before the invocation of each test method in the class.
    }
    
    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testCreatedDateFromString() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        let dateInString = "2016-02-24T12:32:06.952Z"
        let formatter = NSDateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"
       
        let date = TourUpdateManager.sharedInstance.obtainDateFromString(dateInString)

        let newDateInString = formatter.stringFromDate(date)

        XCTAssertEqual(dateInString, newDateInString)
    }
    
    
    func testCompareDatesSame() {
        let dateNow = NSDate()
        let resultComparison = TourUpdateManager.sharedInstance.compareDates(dateNow, newDate: dateNow)
        XCTAssertEqual("same", resultComparison)
    }
    
    func testCompareDateOld() {
        let dateOld = NSDate()
        sleep(3)
        let dateNew = NSDate()
        print(dateOld)
        print(dateNew)
        let resultComparison = TourUpdateManager.sharedInstance.compareDates(dateOld, newDate: dateNew)
        XCTAssertEqual("ascending", resultComparison)
    }
    
    func testCompareDateNew() {
        let dateOld = NSDate()
        sleep(3)
        let dateNew = NSDate()
        print(dateOld)
        print(dateNew)
        let resultComparison = TourUpdateManager.sharedInstance.compareDates(dateNew, newDate: dateOld)
        XCTAssertEqual("descending", resultComparison)
    }
    
    
    func testObtainDateFromStringSuccess() {
        let dateInString = "2016-02-24T12:32:06.952Z"
        let actualDate = TourUpdateManager.sharedInstance.obtainDateFromString(dateInString)
        let actualDateMirror = Mirror(reflecting: actualDate) // mirror copy of the object to access type
        XCTAssertEqual("__NSDate", String(actualDateMirror.subjectType))
    }
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measureBlock {
            // Put the code you want to measure the time of here.
        }
    }

}
