//
//  POITableViewController.swift
//  Alan Touring
//
//  Created by Daniel Baryshnikov on 07/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class POITableViewController: UITableViewController {

    var poiID = "ZUiJcyXQyG"
    var superSectionID = "xI21AHATXD"
    var POIList = [String]()
    var poiViews = [UIView]()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        getPOIS()
        //print(POIList)
        let pointToDisplay = POIParser().getTourSection(poiID)
        print(pointToDisplay.post)
        createSubviews(pointToDisplay.post)
        self.tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        self.tableView.clipsToBounds = true
        print(poiViews)
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }
    
    override func viewWillAppear(animated: Bool) {

        self.tableView.reloadData()
    }
    
    func getPOIS(){
        POIList = []
        for POIS in (((NSUserDefaults.standardUserDefaults().objectForKey(superSectionID)) as! NSDictionary)["pois"]) as! NSArray{
            
            POIList.append(POIS["objectId"] as! String)
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
        return poiViews.count
    }
    
    func createSubviews(post: NSArray){
        
        let width = UIScreen.mainScreen().bounds.size.width
        let height = UIScreen.mainScreen().bounds.size.height
        
        var viewArray = [UIView]()
        var headerArray = [UIView]()
        for row in post{
            
            let types = (row as! NSDictionary).allKeys as! [NSString]
            if types.contains("type"){
                
                switch row["type"] as! String{
                    
                case "Header" :
                    if viewArray.count == 0{
                        //If the first item is an image
                        let view1 = UIView(frame: CGRectMake(0, 0, width, 40))
                        
                        let label = UILabel(frame: view1.bounds)
                        label.font = label.font.fontWithSize(30)
                        label.text = (row["content"] as! String)
                        label.sizeToFit()
                        print("LOLOLOLOLOL \(label.frame.height)")
                        
                        label.textAlignment = NSTextAlignment.Center
                        label.contentMode = .ScaleAspectFill
                        label.center = view1.center
                        view1.addSubview(label)
                        
                        viewArray.append(view1)
                        headerArray.append(view1)
                        view1.contentMode = .ScaleAspectFit
                        view1.hidden = false
                        view1.setNeedsDisplay()
                        poiViews.append(label)
                        
                    } else {
                        //for all subsequent images
                        //Set the position of the image to start at the bottom of the last image + offset
                        
                        let view2 = UILabel(frame: CGRectMake(0, 0, width, headerArray[0].frame.height))
                        let label = UILabel(frame: view2.bounds)
                        label.font = label.font.fontWithSize(30)
                        label.text = (row["content"] as! String)
                        
                        label.textAlignment = NSTextAlignment.Center
                        label.center = view2.center
                        view2.addSubview(label)
                        
                        
                        viewArray.append(view2)
                        headerArray.append(view2)
                        view2.contentMode = .ScaleAspectFit
                        view2.hidden = false
                        view2.setNeedsDisplay()
                        view2.userInteractionEnabled = true
                        poiViews.append(view2)
                    }
                    
                case "body":
                    if viewArray.count == 0{
                        
                        let chars: CGFloat = CGFloat((row["content"] as! String).characters.count)
                        let lines: CGFloat = chars/30
                        let view1 = UIView(frame: CGRectMake(0, 0, width, 25 * lines))
                        let label = UILabel(frame: view1.bounds)
                        label.text = (row["content"] as! String)
                        //label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                        label.numberOfLines = 0
                        
                        label.textAlignment = NSTextAlignment.Center
                        label.center = view1.center
                        view1.addSubview(label)
                        
                        viewArray.append(view1)
                        headerArray.append(view1)
                        view1.contentMode = .ScaleAspectFit
                        view1.hidden = false
                        view1.setNeedsDisplay()
                        poiViews.append(label)
                        
                    } else {
                        //for all subsequent images
                        //Set the position of the image to start at the bottom of the last image + offset
                        
                        // TODO FIND A METHOD TO DO THIS BETTER
                        let chars: CGFloat = CGFloat((row["content"] as! String).characters.count)
                        let lines: CGFloat = chars/30
                        let view2 = UILabel(frame: CGRectMake(0, 0, width, 25 * lines))
                        let label = UILabel(frame: view2.frame)
                        label.text = (row["content"] as! String)
                        // label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                        label.numberOfLines = 0
                        
                        label.textAlignment = NSTextAlignment.Center
                        label.center = view2.center
                        view2.addSubview(label)
                        
                        viewArray.append(view2)
                        headerArray.append(view2)
                        view2.contentMode = .ScaleAspectFit
                        view2.hidden = false
                        view2.setNeedsDisplay()
                        poiViews.append(label)
                        
                    }
                    
                default :
                    print("THIS CAN NEVER HAPPEN")
                    
                }
            } else {
                
                var img: UIImage?
                
                if let imageAtRow : String? = row["url"] as? String{
                    img = imageHandler.sharedInstance.loadImageFromPath(imageAtRow)
                    //print("!!!!!!!!!!the loaded image \(row["url"])!!!!!!!!!!!!")
                    
                } else {
                    img = UIImage()
                }
                
                if viewArray.count == 0{
                    //If the first item is an image
                    
                    let imageView1 = UIImageView(frame: CGRectMake(0, 0, width, height/2))
                    viewArray.append(imageView1)
                    imageView1.image = img
                    imageView1.contentMode = .ScaleAspectFit
                    imageView1.setNeedsDisplay()
                    poiViews.append(imageView1)
                } else {
                    //CLEAN
                    
                    //for all subsequent images
                    //Set the position of the image to start at the bottom of the last image + offset
                    
                    let imageView2 = UIImageView(frame: CGRectMake(0, 0, width, height/2))
                    viewArray.append(imageView2)
                    imageView2.image = img
                    imageView2.contentMode = .ScaleAspectFit
                    imageView2.setNeedsDisplay()
                    poiViews.append(imageView2)
                    
                }
            }
        }
    }


    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("poiCells", forIndexPath: indexPath)

        // Configure the cell...
            cell.contentView.addSubview(poiViews[indexPath.row])
            cell.selectionStyle = UITableViewCellSelectionStyle.None
            cell.contentView.clipsToBounds = true
        
        

        return cell
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return poiViews[indexPath.row].frame.height
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
