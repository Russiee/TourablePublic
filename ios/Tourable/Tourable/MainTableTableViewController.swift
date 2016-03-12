//
//  MainTableTableViewController.swift
//  Tourable
//
//  Created by Alex Gubbay on 06/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit
import Foundation


class MainTableTableViewController: UITableViewController, UIAlertViewDelegate {
   
    var models = NSMutableArray()
    var tourParser = TourIdParser()
    
    @IBOutlet weak var addTourButton: UIButton!
    @IBOutlet weak var addTourButtonView: UIView!
    override func viewDidLoad() {
        
        super.viewDidLoad()
        models = tourParser.getAllTours()
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Notified", name: TableUpdateNotificationKey, object: nil)
        self.clearsSelectionOnViewWillAppear = false
        checkStateOfScreen()
        //TODO remove this, for demo use only
        let connection: Bool = ApiConnector.sharedInstance.isConnectedToNetwork()
        print("internet connection status: \(connection)")
        // checkToursToDelete()
        tableView.tableFooterView = addTourButtonView
        tableView.contentInset = UIEdgeInsetsMake(50, 0, 0, 0)
        
    }
    
    //to check if should be emptry screen when cancelling a tour download
    override func viewWillAppear(animated: Bool) {
        //makes sure Tool bar is visible again after coming back from Tour
        self.navigationController?.setToolbarHidden(true, animated: false)
        checkStateOfScreen()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func Notified() {
        checkStateOfScreen()
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
         tableView.rowHeight = 60.0
        if models.count == 0 {
         
            addTourButtonView.frame = CGRectMake(0 , 0, self.view.frame.width, self.view.frame.height * 0.7)
            let empty_state_button_UI = UIImage(named: "empty_state_button")
            addTourButton.setBackgroundImage(empty_state_button_UI, forState: .Normal)
            addTourButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
            self.view.backgroundColor = UIColor(red: 7/255, green: 62/255, blue: 117/255, alpha: 1.0)
           // addTourButton.layer.cornerRadius = 3
           // addTourButton.layer.borderWidth = 1
            let  empty_state_image = UIImage(named: "empty_tv_placeholder")
            let empty_state_label = UIImageView(image: empty_state_image)
            empty_state_label.contentMode = .ScaleAspectFit
            tableView.backgroundView = empty_state_label
            tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        } else {
         
            addTourButtonView.frame = CGRectMake(0 , 0, self.view.frame.width, self.view.frame.height*0.8-tableView.bounds.height)
            let add_button_UI = UIImage(named: "generic_button")
            addTourButton.setBackgroundImage(add_button_UI, forState: .Normal)
            addTourButton.setTitleColor(UIColor(red: 7/255, green: 62/255, blue: 117/255, alpha: 1.0), forState: .Normal)
           // addTourButton.layer.cornerRadius = 3
            //addTourButton.layer.borderWidth = 1
            self.view.backgroundColor = UIColor.groupTableViewBackgroundColor()
            tableView.backgroundView = nil
            tableView.separatorStyle = UITableViewCellSeparatorStyle.SingleLine
        }
        
    }

    @IBAction func cancelToAddNewTourController(segue:UIStoryboardSegue) {
    }

    @IBAction func addTourPressed(sender: AnyObject) {
        showTourKeyAlert()
        
    }
    @IBAction func saveTourDetail(segue:UIStoryboardSegue) {
        self.models = self.tourParser.getAllTours()
        tableView.reloadData()
    }
    
    @objc func TableChanged(notification: NSNotification){
        //do stuff
    }
    
    @IBAction func plussPressed(sender: UIBarButtonItem) {
        
    }
    
    // triggerd in ViewDidLoad, it iterates the list of tours and deletes the outdated one.
    func checkToursToDelete() {
        for var indexRow = 0; indexRow < models.count; indexRow++ {
            TourUpdateManager.sharedInstance.getCurrentData(models[indexRow] as! String, tableRow: indexRow)
            //Need to find a better way to do this, it is causing the tour to be downloaed twice.
           // TourUpdateManager.sharedInstance.checkIfOutdatedAndDeleteProject()
        }
    }


    //prompt user for tour code input
    func showTourKeyAlert(){
        let alert = UIAlertView(title: "Add New Tour", message: "Enter the key you have recieved", delegate: self, cancelButtonTitle:"Cancel")
        alert.alertViewStyle = UIAlertViewStyle.PlainTextInput
        
        alert.addButtonWithTitle("Add")
        let textField = alert.textFieldAtIndex(0)
        textField?.keyboardAppearance = UIKeyboardAppearance.Alert
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
                    //Tour does not exist. Procede.
                        print("here")
                    ApiConnector.sharedInstance.initiateConnection(Field!.text!, isCheckingForUpdate: false)
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
