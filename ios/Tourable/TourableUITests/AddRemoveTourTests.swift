    //
//  AddRemoveTourTests.swift
//  Tourable
//
//  Created by Federico on 21/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest

class AddRemoveTourTests: XCTestCase {

    override func setUp() {
        super.setUp()

        // Put setup code here. This method is called before the invocation of each test method in the class.
        
        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false
        // UI tests must launch the application that they test. Doing this in setup will make sure it happens for each test method.
        XCUIApplication().launch()

        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
    }

    override func tearDown() {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
        super.tearDown()
    }

    func testAddingAndDeletingTour() {
        let app = XCUIApplication()
        let tablesQuery = app.tables
        tablesQuery.buttons["Add Tour"].tap()

        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-1010")
        collectionViewsQuery.buttons["Add"].tap()
        sleep(8)
        app.alerts["Download Tour"].collectionViews.buttons["Download text only (Smaller)"].tap()
        tablesQuery.staticTexts["Ultimate Flat Tour"].swipeLeft()
        tablesQuery.buttons["Delete"].tap()
    }

    func testAddTourWithMedia() {
        
        let app = XCUIApplication()
        
        // decide which add button to press based on if it is an empty screen or tours are already present
        if app.tables["Empty list"].buttons.count != 0 {
              app.tables["Empty list"].buttons["Add Tour"].tap()
        } else {
            app.tables.buttons["Add Tour"].tap()
        }
      
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-2020")
        
        let addButton = collectionViewsQuery.buttons["Add"]
        addButton.tap()
        sleep(10)
        app.alerts["Download Tour"].collectionViews.buttons["Download with media (Larger)"].tap()
        
    }
    
    func testAddTourWithoutMedia() {
        
        let app = XCUIApplication()
        
        if app.tables["Empty list"].buttons.count != 0 {
            app.tables["Empty list"].buttons["Add Tour"].tap()
        } else {
            app.tables.buttons["Add Tour"].tap()
        }
        
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-1010")
        collectionViewsQuery.buttons["Add"].tap()
        sleep(10)
        app.alerts["Download Tour"].collectionViews.buttons["Download text only (Smaller)"].tap()
        
    }
    
    func testNavigateTourBetweenPOIs() {
        
        let app = XCUIApplication()
        let tablesQuery = app.tables
        tablesQuery.buttons["Add Tour"].tap()
        
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-1111")
        collectionViewsQuery.buttons["Add"].tap()
        sleep(10)
        app.alerts["Download Tour"].collectionViews.buttons["Download with media (Larger)"].tap()
        let tablesQuery2 = tablesQuery
        tablesQuery2.staticTexts["Royal Brompton Test Tour"].tap()
        app.buttons["Start Tour"].tap()
        sleep(1)
        tablesQuery2.staticTexts["The Imaging Facilities"].tap()
        sleep(1)
        tablesQuery2.staticTexts["Medical Imaging Room 1"].tap()
        sleep(1)
        tablesQuery2.staticTexts["Medical Machine ZYX"].tap()
        sleep(1)
        let cell = tablesQuery.childrenMatchingType(.Cell).elementBoundByIndex(1)
        sleep(1)
        cell.tap()
        sleep(1)
        tablesQuery2.staticTexts["  Go to next POI (Medical Machine XYZ)"].tap()
        sleep(1)
        cell.childrenMatchingType(.TextView).element.tap()
        sleep(1)
        tablesQuery2.staticTexts["  Go to previous POI (Medical Machine ZYX)"].tap()
        sleep(1)
        cell.tap()
        sleep(1)
        tablesQuery.buttons["Back to overview"].tap()
        
    }


    
    
}
