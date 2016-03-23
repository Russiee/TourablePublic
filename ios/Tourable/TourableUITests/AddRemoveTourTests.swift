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

    func testOpenAboutPage() {
        let app = XCUIApplication()
        app.navigationBars["Your Tours"].buttons["About  "].tap()
        app.navigationBars["entryController"].childrenMatchingType(.Button).matchingIdentifier("Back").elementBoundByIndex(0).tap()
        
    }
    
    func testAddAndDeleteTourWithoutMedia() {
        let app = XCUIApplication()
        let tablesQuery = app.tables
        tablesQuery.buttons["Add Tour"].tap()

        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-0000")
        collectionViewsQuery.buttons["Add"].tap()
        app.alerts["Download Tour"].collectionViews.buttons["Download text only (Smaller)"].tap()
        tablesQuery.staticTexts["test"].swipeLeft()
        tablesQuery.buttons["Delete"].tap()
    }

    func testAddAndDeleteTourWithMedia() {
        
        let app = XCUIApplication()
        let tablesQuery = app.tables
        tablesQuery.buttons["Add Tour"].tap()
      
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-1010")
        
        let addButton = collectionViewsQuery.buttons["Add"]
        addButton.tap()
        sleep(10)
        app.alerts["Download Tour"].collectionViews.buttons["Download with media (Larger)"].tap()
        sleep(80) // very slow to download
        let tablesQuery2 = app.tables
        tablesQuery2.staticTexts["Ultimate Flat Tour"].swipeLeft()
        
    }

    func testNavigateTourAndPlayVideo() {
        
        let app = XCUIApplication()
        let tablesQueryBeginning = app.tables
        tablesQueryBeginning.buttons["Add Tour"].tap()
        
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-1111")
        collectionViewsQuery.buttons["Add"].tap()
        sleep(10)
        app.alerts["Download Tour"].collectionViews.buttons["Download with media (Larger)"].tap()
        sleep(20)
        let tablesQuery2 = app.tables
        let tablesQuery = tablesQuery2
        tablesQuery.staticTexts["Royal Brompton Test Tour"].tap()
        app.buttons["Start Tour"].tap()
        tablesQuery.staticTexts["The Imaging Facilities"].tap()
        tablesQuery.staticTexts["Specific Medical Imaging Hallway"].tap()
        tablesQuery.staticTexts["Medical Machine ABC"].tap()
        tablesQuery2.childrenMatchingType(.Cell).elementBoundByIndex(1).tap()
        sleep(6)
        app.buttons["Done"].tap()
        
        app.navigationBars["Medical Machine ABC"].childrenMatchingType(.Button).matchingIdentifier("Back").elementBoundByIndex(0).tap()
        app.navigationBars["Specific Medical Imaging Hallway"].childrenMatchingType(.Button).elementBoundByIndex(0).tap()
        app.navigationBars["The Imaging Facilities"].childrenMatchingType(.Button).matchingIdentifier("Back").elementBoundByIndex(0).tap()
        
        let backButton = app.navigationBars["Royal Brompton Test Tour"].childrenMatchingType(.Button).matchingIdentifier("Back").elementBoundByIndex(0)
        backButton.tap()
        backButton.tap()
        tablesQuery.staticTexts["Royal Brompton Test Tour"].swipeLeft()
        tablesQuery.buttons["Delete"].tap()
    }
   
    
    func testNaviateTourAndClickQuiz() {
        
        let app = XCUIApplication()
        let tablesQuery = app.tables
        
        tablesQuery.staticTexts["Ultimate Flat Tour"].tap()
        
        app.buttons["Start Tour"].tap()
        tablesQuery.staticTexts["The Flat"].tap()
        tablesQuery.staticTexts["Alex's Room"].tap()
        tablesQuery.staticTexts["Alex's Desk"].tap()
        tablesQuery.childrenMatchingType(.Cell).elementBoundByIndex(3).tap()
        tablesQuery.buttons["The correct answer is a?"].tap()
        tablesQuery.staticTexts["a"].tap()
        
    }

}
