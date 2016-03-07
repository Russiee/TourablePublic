//
//  MainTableTableViewController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 06/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit
import Foundation


class MainTableTableViewController: UITableViewController, UIAlertViewDelegate {
    
   
    var models = NSMutableArray()
    var tourParser = TourIdParser()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        models = tourParser.getAllTours()
        
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Notified", name: TableUpdateNotificationKey, object: nil)
    
        self.clearsSelectionOnViewWillAppear = false
        

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()


        checkStateOfScreen()
        //TODO remove this, for demo use only
        let connection: Bool = ApiConnector.sharedInstance.isConnectedToNetwork()
        print("internet connection status: \(connection)")
        
    }
    
    //to check if should be emptry screen when cancelling a tour download
    override func viewWillAppear(animated: Bool) {
        //makes sure Tool bar is visible again after coming back from Tour
        self.navigationController?.setToolbarHidden(false, animated: false)
        checkStateOfScreen()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func Notified() {

        models = tourParser.getAllTours()
        tableView.reloadData()
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func viewDidAppear(animated: Bool) {
        models = tourParser.getAllTours()
        tableView.reloadData()
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
        
        return models.count
    }

    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("tableCell", forIndexPath: indexPath)

        // Configure the cell...
        cell.textLabel?.text = models.objectAtIndex(indexPath.row) as? String
        return cell
    }


    // a function to tell change the background image when loading the app AND when deleting a cell results in no tours left
    func checkStateOfScreen(){
        if models.count == 0 {
            let  empty_state_image = UIImage(named: "empty_tv_placeholder")
            let empty_state_label = UIImageView(image: empty_state_image)
            empty_state_label.contentMode = .ScaleAspectFit
            
            // style it as necessary
            
            tableView.backgroundView = empty_state_label
            tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        } else {
            tableView.backgroundView = nil
            tableView.separatorStyle = UITableViewCellSeparatorStyle.SingleLine
        }
        
    }
    
    @IBAction func cancelToAddNewTourController(segue:UIStoryboardSegue) {
    }
    
    @IBAction func saveTourDetail(segue:UIStoryboardSegue) {
        
        self.models = self.tourParser.getAllTours()
        
        tableView.reloadData()
        //lastAddedCell = tableView.cellForRowAtIndexPath(NSIndexPath(index: tableView.numberOfRowsInSection(0)-1))!
    }
    
    @objc func TableChanged(notification: NSNotification){
        //do stuff
    }
    
    @IBAction func plussPressed(sender: UIBarButtonItem) {
        showTourKeyAlert()
    }
    
    func checkToursToDelete() {
        //TODO 1: access tour list with tour codes (therefore not the cached "array" as we will change the codes with the titles)
        
        //TODO 2: iterate throught the list and for each one check if it is outdated throught a TourUpdateManager
    }
    
    
    //prompt user for tour code input
    func showTourKeyAlert(){
        let alert = UIAlertView(title: "Add New Tour", message: "Enter the key you have recieved", delegate: self, cancelButtonTitle:"Cancel")
        alert.alertViewStyle = UIAlertViewStyle.PlainTextInput
        alert.addButtonWithTitle("Add")
        let textField = alert.textFieldAtIndex(0)
        textField!.placeholder = "Enter Tour ID"
        alert.show()
    }
    //Alert user that the tour they are trying to add already exists.

    //controls the behavior of the alerts for user tour code entry
    func alertView(alertView: UIAlertView, clickedButtonAtIndex buttonIndex: Int) {
        switch buttonIndex{
            case 1: //gets text field and hides keyboard in preperation for segue
                let Field = alertView.textFieldAtIndex(0)
                alertView.textFieldAtIndex(0)?.resignFirstResponder()
                
                //checks if the tour already exists. If not:
                // passes the entered tourId into the tourParser
                let tours = TourIdParser.sharedInstance.getAllTours()
                if tours.containsObject(Field!.text!){
                    //Tour already exists
                    AlertViewBuilder.sharedInstance.showWarningAlert("Tour Add Error", message: "A tour with that key already exists")
                }else{
                    if ApiConnector.sharedInstance.isConnectedToNetwork(){
                        print("\(ApiConnector.sharedInstance.isConnectedToNetwork()) network status")
                    //Tour does not exist. Procede.
                    ApiConnector.sharedInstance.initateConnection(Field!.text!, isCheckingForUpdate: false)
                    // goes to the AddNewTourPage
                    performSegueWithIdentifier("goToAddTour", sender: self)
                    // to change the background image
                    tableView.backgroundView = nil
                    }else{
                        AlertViewBuilder.sharedInstance.showWarningAlert("No Internet Connection", message: "No internet connection detected. Please check and retry.")
                    }
                }
            case 0: break  //Cancel pressed, unwind segue executed automatically
            
            default: print("This is here because Swift")
        }
    }

    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

   //Deletes data from the table and updates the cache to reflect his.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            
            TourDeleter.sharedInstance.deleteMediaInTour(indexPath.row)
            TourDeleter.sharedInstance.deleteTour(indexPath.row)
            models = tourParser.getAllTours()
            checkStateOfScreen()
            //Reload Table
            tableView.reloadData()

            
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    

    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        if segue.identifier == "toTableSummary" {
            if let destination = segue.destinationViewController as? TourSummaryController {
                if let tourIndex = tableView.indexPathForSelectedRow?.row {
                    destination.tourId = models[tourIndex] as! String
                    destination.tableRow = tourIndex
                    tableView.deselectRowAtIndexPath(tableView.indexPathForSelectedRow!, animated: true)
                }
            }
        }
        
        if segue.identifier == "goToAddTour" {
            if let destination = (segue.destinationViewController as! UINavigationController).topViewController as? addNewTourViewController {
                destination.tourIndex = models.count
                
            }
        }
    }


}
