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

    func testGetDateFromString() {
        // This is an example of a functional test case.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        let dateInString = "2016-02-24T12:32:06.952Z"
        let formatter = NSDateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"
       
        let date = TourUpdateManager.sharedInstance.getDateFromString(dateInString)

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
        let actualDate = TourUpdateManager.sharedInstance.getDateFromString(dateInString)
        let actualDateMirror = Mirror(reflecting: actualDate) // mirror copy of the object to access type
        XCTAssertEqual("__NSDate", String(actualDateMirror.subjectType))
    }
    
    
    func testCalculateTourLengthFromMinutes1() {
        let HoursAndMinutes = TourUpdateManager.sharedInstance.calculateTourLengthFromMinutes(60)
        let expectedHours = 1
        let expectedMinutes = 0
        XCTAssertEqual(expectedHours , HoursAndMinutes.timeHours)
        XCTAssertEqual(expectedMinutes , HoursAndMinutes.timeMins)
    }
    
    func testCalculateTourLengthFromMinutes2() {
        let HoursAndMinutes = TourUpdateManager.sharedInstance.calculateTourLengthFromMinutes(43)

        let expectedHours = 0
        let expectedMinutes = 43
        XCTAssertEqual(expectedHours , HoursAndMinutes.timeHours)
        XCTAssertEqual(expectedMinutes , HoursAndMinutes.timeMins)
    }

    func testCalculateTourLengthFromMinutes3() {
        let HoursAndMinutes = TourUpdateManager.sharedInstance.calculateTourLengthFromMinutes(462)
        
        let expectedHours = 7
        let expectedMinutes = 42
        XCTAssertEqual(expectedHours , HoursAndMinutes.timeHours)
        XCTAssertEqual(expectedMinutes , HoursAndMinutes.timeMins)
    }

    func testPrepareTourMangaer() {
        TourUpdateManager.sharedInstance.prepareTourMangaer("KCL-1010", tableRow: 3)
        let expectedTourCode = "KCL-1010"
        let expectedIndexPath = 3
        XCTAssertEqual(expectedTourCode, TourUpdateManager.sharedInstance.tourCode)
        XCTAssertEqual(expectedIndexPath, TourUpdateManager.sharedInstance.tourTableRow)
    }


    func testformatDataforTourSummaryAndDiplayIt() {
        let jsonMetadata = [
            "admin" :     [
                "__type" : "Pointer",
                "className" : "Admin",
                "objectId" : "0tOD0B8AOn",
            ],
            "createdAt" : "2016-02-24T12:18:39.855Z",
            "description" : "This tour is for testing and review use only and will not be released to the public. Public tours will include the Royal Brompton Hospital Cardiac Imaging Department",
            "estimatedTime" : 120,
            "isPublic" : 1,
            "objectId" : "cjWRKDygIZ",
            "title" : "Ultimate Flat Tour",
            "updatedAt" : "2016-03-22T13:53:10.338Z",
            "version" : 20
        ]

        TourUpdateManager.sharedInstance.formatDataforTourSummaryAndDiplayIt(jsonMetadata)

        let expectedTimeHours = 2
        let expectedTimeMinutes = 0
        let expectedIsTourUpTodate = true
        let expiryDate = TourUpdateManager.sharedInstance.getDateFromString("2016-03-22T13:53:10.338Z")

        if expiryDate.daysFrom(NSDate()) == 0 {
            let HoursAndMinutesLeft = TourUpdateManager.sharedInstance.calculateTourLengthFromMinutes(abs(expiryDate.minutesFrom(NSDate())))
            let expectedExpiresInHours = HoursAndMinutesLeft.timeHours
            let expectedExpiresInMinutes = HoursAndMinutesLeft.timeMins

            // is the minutes are negative tour is already expired
            if expiryDate.minutesFrom(NSDate()) > 0 {
                XCTAssertEqual(expectedExpiresInHours, TourUpdateManager.sharedInstance.expiresInHours)
                XCTAssertEqual(expectedExpiresInMinutes, TourUpdateManager.sharedInstance.expiresInMinutes)
            }

        } else {
            let expectedExpiresIn = expiryDate.daysFrom(NSDate())
            XCTAssertEqual(expectedExpiresIn, TourUpdateManager.sharedInstance.expiresIn)
        }

        XCTAssertEqual(expectedTimeHours, TourUpdateManager.sharedInstance.timeHours)
        XCTAssertEqual(expectedTimeMinutes, TourUpdateManager.sharedInstance.timeMinutes)
        XCTAssertEqual(expectedIsTourUpTodate, TourUpdateManager.sharedInstance.isTourUpTodate)
    }

    
    func testIsTourUpdateTrue() {
        let currentVersion = 10
        let newVersion = 10
        let expectedIsUpToDate = true
        let isUptoDate = TourUpdateManager.sharedInstance.isTourUpToDate(currentVersion, versionFreshFromAPI: newVersion)
        XCTAssertEqual(expectedIsUpToDate, isUptoDate)
    }
    
    func testIsTourUpdateFalse() {
        let currentVersion = 10
        let newVersion = 12
        let expectedIsUpToDate = false
        let isUptoDate = TourUpdateManager.sharedInstance.isTourUpToDate(currentVersion, versionFreshFromAPI: newVersion)
        XCTAssertEqual(expectedIsUpToDate, isUptoDate)
    }
    
    
    func testPerformanceExample() {
        // This is an example of a performance test case.
        self.measureBlock {
            // Put the code you want to measure the time of here.
        }
    }

}
