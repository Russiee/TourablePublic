//
//  TourUpdateManager.swift
//  Tourable
//
//  Created by Federico on 05/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

///TourUpdateManager is responsible for handling updates of tours that are in NSUserDefaults.
public class TourUpdateManager: NSObject {

    var tourTableRow: Int!
    var tourCode: String!
    var currentTourKEYmetadata: Dictionary<String,AnyObject>!
    var newTourKEYmetadata: NSDictionary!

    //metadata to be displayed on the tourSummary
    var timeHours: Int!
    var timeMinutes: Int!
    var expiresIn: Int!
    var isTourUpTodate = true
    var expiresInHours: Int!
    var expiresInMinutes: Int!

    class var sharedInstance: TourUpdateManager {
        struct Static {
            static var onceToken: dispatch_once_t = 0
            static var instance: TourUpdateManager? = nil
        }
        dispatch_once(&Static.onceToken) {
            Static.instance = TourUpdateManager()

        }
        return Static.instance!
    }

    // to be called as an initialiser to prepare the object for other methods
    func prepareTourMangaer(tourCodetoCheck: String, tableRow: Int) {
        setTourCodeAndTableRow(tourCodetoCheck, tableRow: tableRow)

        // get current tourKey metadata from cache
        self.currentTourKEYmetadata = TourIdParser().getTourMetadata(tourCode)

        // get new tourKey metadata
        if KeyVerifyConnector.sharedInstance.isConnectedToNetwork() {
            KeyVerifyConnector.sharedInstance.initiateKeyVerifyConnection(tourCode, isCheckingForUpdate: true)
            newTourKEYmetadata = KeyVerifyConnector.sharedInstance.getTourMetadata(tourCode)
        }
    }

    ///Set field variables when preparing the class object for the various tasks of the class
    func setTourCodeAndTableRow(tourCodetoCheck: String, tableRow: Int) {
        self.tourCode = tourCodetoCheck
        self.tourTableRow = tableRow
    }
    
    ///This method received data from the api or cache and load it onto the tourSummaryViewController
    func formatDataforTourSummaryAndDiplayIt(jsonResult: NSDictionary) {
        // TOUR LENGTH HOURS AND MINUTES
        let minutes = jsonResult["estimatedTime"]
        let estimatedLenght = calculateTourLengthFromMinutes(minutes as! Int)
        timeHours = estimatedLenght.timeHours
        timeMinutes = estimatedLenght.timeMins

        // TOUR UPDATE STATUS
        // you will need to pass jsonResult["version"] to the isTourUpToDate()
        isTourUpTodate = self.isTourUpToDate(currentTourKEYmetadata["version"] as! Int ,versionFreshFromAPI: jsonResult["version"] as! Int)

        // EXPIRY DATE
        let expiryDate = getDateFromString(currentTourKEYmetadata["expiry"] as! String)

        // if the tour lasts less than a day then return the hours and minutes
        if expiryDate.daysFrom(NSDate()) == 0 {
            expiresIn = 0

            let HoursAndMinutesLeft = calculateTourLengthFromMinutes(abs(expiryDate.minutesFrom(NSDate())))
            expiresInHours = HoursAndMinutesLeft.timeHours
            expiresInMinutes = HoursAndMinutesLeft.timeMins
        } else {
            expiresIn = expiryDate.daysFrom(NSDate())
            expiresInHours = 0
            expiresInMinutes = 0
        }

        // call the tour summary to update tourSummary fields
        self.triggerTourMetaDataAvailableNotification()
    }

    ///Receive minutes as a paramenter and return hours and minutes of that length
    func calculateTourLengthFromMinutes(minutes: Int) -> (timeHours: Int, timeMins: Int) {
        let hours = minutes / 60
        let minutes = minutes % 60
        return (hours,minutes)
    }

    ///Returns fields variable set when data is returned by API
    func getTourStatusInfo() -> (timeHours: Int,timeMins: Int, isCurrent: Bool, expiresIn: Int, expiresInHours: Int, expiresInMinutes: Int){
        return (self.timeHours, self.timeMinutes, self.isTourUpTodate, self.expiresIn, self.expiresInHours, self.expiresInMinutes)
    }

    ///Check for updates comparing freshly downloaded metadata with current stored one. if there are updates the user is asked if he wants to download them
    func isTourUpToDate(currentVersion: Int, versionFreshFromAPI: Int) -> Bool {
        if KeyVerifyConnector.sharedInstance.isConnectedToNetwork() {
            if currentVersion < versionFreshFromAPI {
                return false
            }
        }
        return true
    }

    ///Trigger download of the tour metadata needed on the tourSummary
    func getTourMetadata() {
        // if there is no connection get the data from the cache and load that on tourSummary
        if KeyVerifyConnector.sharedInstance.isConnectedToNetwork() {
            let tourConnector = TourMetadataConnector()
            tourConnector.checkTourMetadataForUpdates(currentTourKEYmetadata["objectId"] as! String)
        } else {
            formatDataforTourSummaryAndDiplayIt(currentTourKEYmetadata)
        }
    }


    ///Check if a project is out to date comparing metadata with current today's date. if the project is out to date, it is deleted after informing the use
    func checkIfOutdatedAndDeleteProject() {
        if self.newTourKEYmetadata != nil {
            let todaysDate = NSDate()
            let expiresDate = getDateFromString(currentTourKEYmetadata["expiry"] as! String)

            let comparisonResulFromString = compareDates(todaysDate, newDate: expiresDate)
            if comparisonResulFromString == "descending" {
                TourDeleter().deleteTour(tourCode)
            }
        }
    }
    
    // compare two dates and returns a string saying the order of the dates.
    func compareDates(currentDate: NSDate, newDate: NSDate) -> String {
        let dateComparison = currentDate.compare(newDate)

        switch (dateComparison) {
        case NSComparisonResult.OrderedDescending:
            return "descending"
        case NSComparisonResult.OrderedAscending:
            return "ascending"
        default:
            return "same"
        }
    }
    
    ///Receive a string of format "yyyy-MM-dd'T'hh:mm:ss.SSSz" and returns an NSDate object
    func getDateFromString(date: String) -> NSDate {
        let enUSPOSIXLocale: NSLocale = NSLocale(localeIdentifier: "en_US")
        let dateFormatter = NSDateFormatter()
        dateFormatter.locale = enUSPOSIXLocale
        dateFormatter.dateFormat = "YYYY-MM-dd'T'HH:mm:ss.SSS'Z'"
        return dateFormatter.dateFromString(date)!
    }
    
    ///Trigger fields to be updated in the TourSummary when TourMetadata Arrived
    func triggerTourMetaDataAvailableNotification() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "Tour Update Available:",
            name: "Tour Update Notification",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(TourSummaryMetaDataAvailable, object: self)
        }
        notify()
    }

    ///Called from the tourSummary when the user clikes the updates in the TourSummary
    func triggerUpdate() {
        // GET RID OF NOTIFIER!
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "NotifiedValid", name: validIdNotificationKey, object: nil)
        TourDeleter.sharedInstance.deleteTour(tourCode)
        KeyVerifyConnector.sharedInstance.initiateKeyVerifyConnection(tourCode, isCheckingForUpdate: false)
    }
    
    ///Images re-downloaded only when it's notified to do so
    func NotifiedValid(){
        sleep(1)
        imageHandler.sharedInstance.downloadMediaSet(imageHandler.sharedInstance.imageQueue)
    }
}

///Credit to: Leo Dabus http://stackoverflow.com/questions/27182023/getting-the-difference-between-two-nsdates-in-months-days-hours-minutes-seconds
extension NSDate {
    func daysFrom(date:NSDate) -> Int{
        return NSCalendar.currentCalendar().components(.Day, fromDate: date, toDate: self, options: []).day
    }
    func hoursFrom(date:NSDate) -> Int{
        return NSCalendar.currentCalendar().components(.Hour, fromDate: date, toDate: self, options: []).hour
    }
    func minutesFrom(date:NSDate) -> Int{
        return NSCalendar.currentCalendar().components(.Minute, fromDate: date, toDate: self, options: []).minute
    }
}