//
//  TourSectionsController.swift
//  Tourable
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit
///TourSectionsController is responsible for displaying the tour sections. Display Subsections and POIS.
class TourSectionsController: UITableViewController {

    var models = [String: String]()
    var superTableId = ""
    var poiArray = []
    var sectionKeys = [String]()
    var poiKeys = [String]()

    @IBOutlet weak var tourSummaryTextView: UITextView!


    override func viewDidLoad() {
        super.viewDidLoad()
        //tourSummaryLabel.removeFromSuperview()
        self.clearsSelectionOnViewWillAppear = false
        let tour = tourDataParser().getTourSection(superTableId)
        self.title = tour.title as String 
        let subsectionArray = tour.subsections
        tourSummaryTextView.text = tour.description
         tourSummaryTextView.textContainerInset = UIEdgeInsetsMake(12, 12, 12, 12)
        tourSummaryTextView.scrollEnabled = false
        poiArray = tour.pointsOfInterest
        var tourTitles = [String: String]()
        let tdp = tourDataParser.init()
        tableView.tableHeaderView = tourSummaryTextView
        
        //Get all the Subsections.
        for subsectionPointer in subsectionArray{
            if subsectionPointer["objectId"] != nil {
                let subsectionData = tdp.getTourSection((subsectionPointer["objectId"] as! String))
                tourTitles[subsectionData.title as String] =  subsectionData.sectionId
                //appends the Names of subsections in the order they appear
                sectionKeys.append(subsectionData.title as String)
            }
          
 
        }

        tourSummaryTextView.sizeToFit()
        
        //Get all the POIS.
        let poip = POIParser.init()
        for poiPointer in poiArray{
            let poiData = poip.getTourSection((poiPointer["objectId"] as? String)!)
            tourTitles[poiData.title as String] = poiData.objectId
            //appends the Names of subsections in the order they appear
            poiKeys.append(poiData.title as String)
        }

        models = tourTitles
        checkStateOfScreen()
        tableView.reloadInputViews()
        tableView.reloadData()
        self.reloadInputViews()
           }
    
    //to check if should be emptry screen when cancelling a tour download
    override func viewWillAppear(animated: Bool) {
        checkStateOfScreen()
    }
    override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return tourSummaryTextView.frame.height// return height which is greater than your image's height.
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func tableView(tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        
        if let view = view as? UITableViewHeaderFooterView {
            view.textLabel!.backgroundColor = UIColor.clearColor()
            view.textLabel!.font =  UIFont.systemFontOfSize(14.0)
            view.textLabel!.textColor = UIColor(red: 21/255, green: 42/255, blue: 74/255, alpha: 1.0)
          //  view.frame.height = view.frame.height+10
        }
        
    }

    // MARK: - Table view data source
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        //Depending on existance of Subsections and POIs will determine amount of sections we have.
        if poiKeys.isEmpty || sectionKeys.isEmpty{
            return 1
        }else{
            return 2
        }
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        //Get the amount of rows based on amount of Subsections and POIs.
        if section == 0 && poiKeys.isEmpty{
        return sectionKeys.count
        }else if section == 0 && sectionKeys.isEmpty{
            return poiKeys.count
        }else if section == 0 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
            return sectionKeys.count
        }else if section == 1 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
            return poiKeys.count
        }else{
            return 0
        }
    }


    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let cell = tableView.dequeueReusableCellWithIdentifier("tableCell2", forIndexPath: indexPath)
        
        //Set the cell text to be that of the Subection or POI.
        if indexPath.section == 0 && poiKeys.isEmpty{
            cell.textLabel?.text = sectionKeys[indexPath.row]
        }else if indexPath.section == 0 && sectionKeys.isEmpty{
            cell.textLabel?.text = poiKeys[indexPath.row]
        }else if indexPath.section == 0 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
            cell.textLabel?.text = sectionKeys[indexPath.row]
        }else if indexPath.section == 1 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
            cell.textLabel?.text = poiKeys[indexPath.row]
        }
        return cell
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        //Code to go to a Subsection or display POI
        let row = tableView.indexPathForSelectedRow!.row
        var rowTitle = ""
     
        if indexPath.section == 0 && poiKeys.isEmpty{
            rowTitle = sectionKeys[row]
        }else if indexPath.section == 0 && sectionKeys.isEmpty{
             rowTitle = poiKeys[row]
        }else if indexPath.section == 0 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
            rowTitle = sectionKeys[row]
        }else if indexPath.section == 1 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
             rowTitle = poiKeys[row]
        }
    

        let objectForSegue = models[rowTitle]

        let tourSections = tourDataParser.init().getTourSection(superTableId).subsections
        let tourPOIS = tourDataParser.init().getTourSection(superTableId).pointsOfInterest


        for poi in tourPOIS{
            if (poi["objectId"] as! String) == objectForSegue{
                //self.performSegueWithIdentifier("PointOfInterestSegue", sender: self)
                self.performSegueWithIdentifier("POISegue", sender: self)
                break
            }
        }

        for subsection in tourSections{
            if (subsection["objectId"] as! String) == objectForSegue{
                self.performSegueWithIdentifier("showNextPage", sender: self)
                break
            }
        }
    }

    // a function to tell change the background image when loading the app AND when deleting a cell results in no tours left
    func checkStateOfScreen() {
         tableView.rowHeight = 60.0
        if models.count == 0 {
            let  empty_state_image = UIImage(named: "empty_ts_placeholder")
            let empty_state_label = UIImageView(image: empty_state_image)
            empty_state_label.contentMode = .ScaleAspectFit
            self.view.backgroundColor = UIColor(red: 21/255, green: 42/255, blue: 74/255, alpha: 1.0)
            tableView.separatorStyle = UITableViewCellSeparatorStyle.None
                    } else {
            tableView.backgroundView = nil
            tableView.separatorStyle = UITableViewCellSeparatorStyle.SingleLine
        }

    }

    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        
        var title = ""
        
        if tableView.indexPathForSelectedRow?.section == 0 && poiKeys.isEmpty{
             title = sectionKeys[self.tableView.indexPathForSelectedRow!.row]
        }else if tableView.indexPathForSelectedRow?.section == 0 && sectionKeys.isEmpty{
             title = poiKeys[self.tableView.indexPathForSelectedRow!.row]
        }else if tableView.indexPathForSelectedRow?.section == 0 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
             title = sectionKeys[self.tableView.indexPathForSelectedRow!.row]
        }else if tableView.indexPathForSelectedRow?.section == 1 && !sectionKeys.isEmpty && !poiKeys.isEmpty{
             title = poiKeys[self.tableView.indexPathForSelectedRow!.row]
        }

        let objectId = models[title]
        
        //Choose which segue to perform depending on which cell was tapped.
        if (segue.identifier == "showNextPage") {
            let newViewController = segue.destinationViewController as! TourSectionsController
            newViewController.superTableId = objectId!
            
        } else if (segue.identifier == "PointOfInterestSegue") {
            let newViewController = segue.destinationViewController as! POITableViewController
            newViewController.poiID = objectId!
            newViewController.superSectionID = superTableId
        }
        else if (segue.identifier == "POISegue") {
            let newViewController = segue.destinationViewController as! POITableViewController
            newViewController.poiID = objectId!
            newViewController.superSectionID = superTableId
        }
        tableView.deselectRowAtIndexPath(tableView.indexPathForSelectedRow!, animated: true)

        let backButton = UIBarButtonItem()
        backButton.title = "Back"
        navigationItem.backBarButtonItem = backButton
    }
    
    
 override func tableView(tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
    //Set the headerTitles depending on exiatance of Subsections and POIs.
    let headerTitles = ["SUBSECTIONS","POINTS OF INTEREST"]
    if tableView.numberOfSections == 1 && sectionKeys.isEmpty{
        return headerTitles[1]
    }else if tableView.numberOfSections == 1 && poiKeys.isEmpty{
        return headerTitles[0]
    }else if section < headerTitles.count {
        return headerTitles[section]
    }else{
        return nil
    }
}


}



    
 
    

