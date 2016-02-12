//
//  Alan_TouringUITests.swift
//  Alan TouringUITests
//
//  Created by Daniel Baryshnikov on 05/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import XCTest

class Alan_TouringUITests: XCTestCase {
        
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
    
    func testAddTour() {
        // Use recording to get started writing UI tests.
        // Use XCTAssert and related functions to verify your tests produce the correct results.
        
        
        let app = XCUIApplication()
        app.toolbars.buttons["Add"].tap()
        
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour ID"].typeText("test1")
        
        let addButton = collectionViewsQuery.buttons["Add"]
        addButton.tap()
        app.buttons["With Video"].tap()
        
        
    }
    
    func testDeleteTour() {
        
        let app = XCUIApplication()
        app.toolbars.buttons["Add"].tap()
        
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour ID"].typeText("test3")
        
        let addButton = collectionViewsQuery.buttons["Add"]
        addButton.tap()
        app.buttons["With Video"].tap()
        
        let tablesQuery = app.tables
        tablesQuery.staticTexts["test3"].swipeLeft()
        tablesQuery.buttons["Delete"].tap()
        
        
        
    }
    
}
