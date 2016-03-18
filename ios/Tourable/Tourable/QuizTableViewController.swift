//
//  TableViewController.swift
//  Tourable
//
//  Created by Alex Gubbay on 16/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit


class QuizTableViewController: UITableViewController{
    
    var quiz : Quiz!
   // @IBOutlet weak var questionTitle: UILabel!
    
    @IBOutlet weak var questionTitle: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        questionTitle.text = quiz.question as String
        print(quiz.options)
        self.title = "Quiz Question"
        self.tableView.rowHeight = 60
        print("here in view didLoad")
        self.tableView.dataSource = self
        self.tableView.delegate = self
        // Uncomment the following line to preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = true
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
        self.tableView.reloadData()
        // tableView.tableFooterView = UIView(frame: CGRectZero)
        
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
        return quiz.options.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("test", forIndexPath: indexPath)
        cell.textLabel?.text = (quiz.options[indexPath.row] as! String)
        // Configure the cell...
        return cell
    }
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
        let indexPath = tableView.indexPathForSelectedRow
        let currentCell = tableView.cellForRowAtIndexPath(indexPath!)! as UITableViewCell
        let correctCell = tableView.cellForRowAtIndexPath(NSIndexPath(forRow: quiz.correct, inSection: 0))
        correctCell!.backgroundColor = UIColor(red: 178/255, green: 255/255, blue: 177/255, alpha: 1.0)
        correctCell!.contentView.backgroundColor = UIColor(red: 178/255, green: 255/255, blue: 177/255, alpha: 1.0)
        
        if indexPath?.row != quiz.correct{
            currentCell.backgroundColor = UIColor(red: 255/255, green: 178/255, blue: 177/255, alpha: 1.0)
            currentCell.contentView.backgroundColor = UIColor(red: 255/255, green: 178/255, blue: 177/255, alpha: 1.0)
        }
        tableView.userInteractionEnabled = false
    }
    
    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
    // Return false if you do not want the specified item to be editable.
    return true
    }
    */
    
    /*
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
    if editingStyle == .Delete {
    // Delete the row from the data source
    tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
    } else if editingStyle == .Insert {
    // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
    }
    }
    */
    
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