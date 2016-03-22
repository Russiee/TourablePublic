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

    func testAddTour() {
        
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
        sleep(5)
        app.alerts["Download Tour"].collectionViews.buttons["Download with media (Larger)"].tap()
        
    }
    
    func testAddTourFail() {
        
        let app = XCUIApplication()
        app.tables.buttons["Add Tour"].tap()
        
        let collectionViewsQuery = app.alerts["Add New Tour"].collectionViews
        collectionViewsQuery.textFields["Enter Tour Key"].typeText("KCL-1010")
        collectionViewsQuery.buttons["Add"].tap()
        
        let cancelButton = app.alerts["Tour Add Error"].collectionViews.buttons["Cancel"]
        cancelButton.tap()
    }

}
