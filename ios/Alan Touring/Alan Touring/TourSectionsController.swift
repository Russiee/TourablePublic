//
//  TourSectionsController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class TourSectionsController: UITableViewController {

    
    
    var models = [String: String]()
    var programVar = ""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.clearsSelectionOnViewWillAppear = false
       let tour = tourDataParser.init().getTourSection(programVar)
    
      let subsectionArray = tour.getSubSections();
        var tourTitles = [String: String]()
        
        let tdp = tourDataParser.init()
        for subsectionPointer in subsectionArray{
            var subsectionData = tdp.getTourSection((subsectionPointer["objectId"] as? String)!)
           
           //print("\(subsectionData.title) DATA RECOVERED FROM ID")
            tourTitles[subsectionData.title as String] =  subsectionData.sectionId
            
        }
      models = tourTitles
        
        checkStateOfScreen()
    }
    
    //to check if should be emptry screen when cancelling a tour download
    override func viewWillAppear(animated: Bool) {
        checkStateOfScreen()
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
        
              let cell = tableView.dequeueReusableCellWithIdentifier("tableCell2", forIndexPath: indexPath)
        
        // Configure the cell...
        let keys = Array(models.keys)
        
        cell.textLabel?.text = keys[indexPath.row] 
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
   
    }
    
    
    
    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
    // Return false if you do not want the specified item to be editable.
    return true
    }
    */
    
    //Deletes data from the table and updates the cache to reflect his.
    func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
           
           // tableView.reloadData()
            
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
    
    func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        
    }
    
    
 
    

