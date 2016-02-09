//
//  MainTableTableViewController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 06/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class MainTableTableViewController: UITableViewController, UIAlertViewDelegate {
    
   
    var models = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tourIdParser.addNewTourId("hello")
        tourIdParser.confirmTourId(true)
        models = tourIdParser.getAllTours()

        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        
        
        // Load empty state view if necessary.
        if models.count == 0 {
          let  empty_state_image = UIImage(named: "empty_tv_placeholder")
            let empty_state_label = UIImageView(image: empty_state_image)
            empty_state_label.contentMode = .ScaleAspectFit

            
            // style it as necessary
            
            tableView.backgroundView = empty_state_label
            tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        } else {
            tableView.backgroundView = nil
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
        cell.textLabel?.text = models[indexPath.row]
        return cell
    }
    
    @IBAction func cancelToAddNewTourController(segue:UIStoryboardSegue) {
    }
    
    @IBAction func saveTourDetail(segue:UIStoryboardSegue) {
        
        models = tourIdParser.getAllTours()
        tableView.reloadData()
        
        
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
            
        case 1: print("Blue")
            let Field = alertView.textFieldAtIndex(0)
            alertView.textFieldAtIndex(0)?.resignFirstResponder()
            tourIdParser.addNewTourId(Field!.text!)
            performSegueWithIdentifier("goToAddTour", sender: self)
            
            
        case 0: print("Red")
            
        default: print("Is this part even possible?")
        
        }
    }
    
    
    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

   
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
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

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
