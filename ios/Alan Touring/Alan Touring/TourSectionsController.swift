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
    var superTableId = ""
    var poiArray = []
    var sectionKeys = [String]()
    var poiKeys = [String]()

    
    @IBOutlet weak var tourSummaryLabel: UILabel!
    @IBOutlet weak var tourSummaryView: UIView!

    override func viewDidLoad() {
        super.viewDidLoad()
        //tourSummaryLabel.removeFromSuperview()
        self.clearsSelectionOnViewWillAppear = false
        let tour = tourDataParser().getTourSection(superTableId)
        let subsectionArray = tour.getSubSections()
        print(tour.description)
        tourSummaryLabel.text = tour.description
        poiArray = tour.getPointsOfInterest()
        var tourTitles = [String: String]()
        let tdp = tourDataParser.init()
        tableView.tableHeaderView = tourSummaryView
        for subsectionPointer in subsectionArray{
            let subsectionData = tdp.getTourSection((subsectionPointer["objectId"] as? String)!)
            tourTitles[subsectionData.title as String] =  subsectionData.sectionId
            //appends the Names of subsections in the order they appear
            sectionKeys.append(subsectionData.title as String)
 
        }
        tourSummaryLabel.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
        tourSummaryLabel.numberOfLines = 0
        
        //tourSummaryView.addSubview(tourSummaryLabel)
        tourSummaryView.setNeedsLayout()
        tourSummaryView.layoutIfNeeded()
        
        let poip = POIParser.init()
        for poiPointer in poiArray{
            let poiData = poip.getTourSection((poiPointer["objectId"] as? String)!)
            tourTitles[poiData.title as String] = poiData.objectId
            //appends the Names of subsections in the order they appear
            poiKeys.append(poiData.title as String)
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
    
    override func tableView(tableView: UITableView, willDisplayHeaderView view: UIView, forSection section: Int) {
        
        if let view = view as? UITableViewHeaderFooterView {
            view.textLabel!.backgroundColor = UIColor.clearColor()
            view.textLabel!.font =  UIFont.systemFontOfSize(17.0)
            view.textLabel!.textColor = UIColor(red: 21/255, green: 42/255, blue: 74/255, alpha: 1.0)

        }
        
    }

    // MARK: - Table view data source
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        if poiKeys.isEmpty || sectionKeys.isEmpty{
            return 1
        }else{
            return 2
        }
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
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
        
        // Configure the cell...
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
        //CODE TO GO TO THE NEXT LEVEL OF TOUR OR DISPLAY POINT OF INTEREST
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

        let tourSections = tourDataParser.init().getTourSection(superTableId).getSubSections()
        let tourPOIS = tourDataParser.init().getTourSection(superTableId).getPointsOfInterest()


        for poi in tourPOIS{
            if (poi["objectId"] as! String) == objectForSegue{
                self.performSegueWithIdentifier("PointOfInterestSegue", sender: self)
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

            // style it as necessary

            tableView.backgroundView = empty_state_label
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

        if (segue.identifier == "showNextPage") {
            let newViewController = segue.destinationViewController as! TourSectionsController
            newViewController.superTableId = objectId!
            
        } else if (segue.identifier == "PointOfInterestSegue") {
            let newViewController = segue.destinationViewController as! PointViewController
            newViewController.poiID = objectId!
            newViewController.superSectionID = superTableId
        }
        tableView.deselectRowAtIndexPath(tableView.indexPathForSelectedRow!, animated: true)
    }
    
 override func tableView(tableView: UITableView, titleForHeaderInSection section: Int) -> String? {
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



    
 
    

