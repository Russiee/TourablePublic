//
//  MainTableTableViewController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 06/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit



class MainTableTableViewController: UITableViewController, UIAlertViewDelegate {
    
   
    var models = NSMutableArray()
    var tourParser = TourIdParser()
    var API = ApiConnector.init()
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        models = tourParser.getAllTours()
        
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "Notified", name: TableUpdateNotificationKey, object: nil)
    
        self.clearsSelectionOnViewWillAppear = false
        

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()


        checkStateOfScreen()
        //TODO remove this, for demo use only
        //videoHandler.sharedInstance.downloadVideo("https://clips.vorwaerts-gmbh.de/VfE_html5.mp4")
        
    }
    

  


    //to check if should be emptry screen when cancelling a tour download
    override func viewWillAppear(animated: Bool) {
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
        showAlert()
    }
    
    func showAlert(){
        let alert = UIAlertView(title: "Add New Tour", message: "Enter the key you have recieved", delegate: self, cancelButtonTitle:"Cancel")
        alert.alertViewStyle = UIAlertViewStyle.PlainTextInput
        alert.addButtonWithTitle("Add")
        let textField = alert.textFieldAtIndex(0)
        textField!.placeholder = "Enter Tour ID"
        alert.show()
    }
    
    func alertView(alertView: UIAlertView, clickedButtonAtIndex buttonIndex: Int) {
        switch buttonIndex{
            case 1: //gets text field and hides keyboard in preperation for segue
                let Field = alertView.textFieldAtIndex(0)
                alertView.textFieldAtIndex(0)?.resignFirstResponder()
        
                // passes the entered tourId into the tourParser
                
                
                API.initateConnection(Field!.text!)
                
                // goes to the AddNewTourPage
                performSegueWithIdentifier("goToAddTour", sender: self)
                // to change the background image
                tableView.backgroundView = nil
            
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
            // Delete the row from the data source
            tourParser.deleteTourIdAtRow(indexPath.row)
            //Update copy of data to display
            
            models = tourParser.getAllTours()
            checkStateOfScreen()
            //Reload Table
            tableView.reloadData()
            
            //Don't touch. Magic.
            //tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
            
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
