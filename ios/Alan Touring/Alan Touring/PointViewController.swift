
//
//  PointViewController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 23/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

import Foundation

class PointViewController: UIViewController, UIScrollViewDelegate {
    
    
    @IBOutlet weak var scrollView: UIScrollView!
    var poiID = ""
    var superSectionID = ""
    var POIList = [String]()
    
    @IBOutlet weak var exampleVideoButton: UIBarButtonItem!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        getPOIS()
        
        
        //adding a tool bar with two buttons for the previous and next POI in the tour
        let toolbar = createToolBar()
        self.view.addSubview(toolbar)
        
        scrollView.delegate = self
        scrollView.contentSize = CGSize(width: self.view.frame.size.width, height: self.view.frame.size.height)
        //      scrollView.contentInset = UIEdgeInsetsMake(0.00, 0.00, 44.0, 0.00)
        //      scrollView.scrollIndicatorInsets = UIEdgeInsetsMake(0.00, 0.00, 44.0, 0.00)
        
        let pointToDisplay = POIParser().getTourSection(poiID)
        
        self.createSubviews(pointToDisplay.post)
        
    }
    
    @IBAction func exmapleVideoButton(sender: AnyObject) {
        performSegueWithIdentifier("segueToVideo", sender: self)
    }
    // navigate to previous POI
    
    func getPOIS(){
        POIList = []
        for POIS in (((NSUserDefaults.standardUserDefaults().objectForKey(superSectionID)) as! NSDictionary)["pois"]) as! NSArray{
            
            POIList.append(POIS["objectId"] as! String)
        }
    }
    
    func createToolBar() -> UIToolbar{
        
        let toolbar: UIToolbar = UIToolbar()
            if(POIList.count > 1){
                toolbar.frame = CGRectMake(0, self.view.frame.size.height - 44, self.view.frame.size.width, 44)
                
                if(POIList.indexOf(poiID) == 0){
                    let items = [UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.FlexibleSpace, target: nil, action: nil) ,UIBarButtonItem(title: "Next", style: .Plain , target: self, action: "nextPOI"), exampleVideoButton!]
                    toolbar.setItems(items, animated: true)
                }
                else if(POIList.indexOf(poiID) == (POIList.count - 1)){
                    let items = [UIBarButtonItem(title: "Previous", style: .Plain , target: self, action: "previousPOI") , UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.FlexibleSpace, target: nil, action: nil), exampleVideoButton!]
                    toolbar.setItems(items, animated: true)
                }
                else{
                    let items = [UIBarButtonItem(title: "Previous", style: .Plain , target: self, action: "previousPOI") , UIBarButtonItem(barButtonSystemItem: UIBarButtonSystemItem.FlexibleSpace, target: nil, action: nil) ,UIBarButtonItem(title: "Next", style: .Plain , target: self, action: "nextPOI"), exampleVideoButton!]
                    toolbar.setItems(items, animated: true)
                }
            }
        
        return toolbar
        
    }
    func previousPOI(){
        
        let Z = POIList.indexOf(poiID)!
        //print(Z)
        
        poiID = (POIList as! NSArray)[Z - 1] as! String
        let subViews = scrollView.subviews
        for views in subViews{
            views.removeFromSuperview()
        }
        viewDidLoad()
    }
    
    // navigate to next POI
    func nextPOI(){
        
        let Z = POIList.indexOf(poiID)!
        //print(Z)
        
        poiID = (POIList as! NSArray)[Z + 1] as! String
        let subViews = scrollView.subviews
        for views in subViews{
            views.removeFromSuperview()
        }
        viewDidLoad()
    }

    
    
    func createSubviews(post: NSArray){
        
        let width = UIScreen.mainScreen().bounds.size.width
        let height = UIScreen.mainScreen().bounds.size.height
        
        let offset: CGFloat = 5
        var totalHeight: CGFloat = 0
        var viewArray = [UIView]()
        var headerArray = [UIView]()
        for row in post{
            
            var types = Array((row as! NSDictionary).allKeys)
            if types[0] as! String == "type"{
                
                switch row["type"] as! String{
                    
                case "Header" :
                    if viewArray.count == 0{
                        //If the first item is an image
                        let view1 = UIView(frame: CGRectMake(0, 0, width, 40))
                        
                        let label = UILabel(frame: view1.bounds)
                        label.font = label.font.fontWithSize(30)
                        label.text = (row["content"] as! String)
                        
                        label.textAlignment = NSTextAlignment.Center
                        label.center = view1.center
                        view1.addSubview(label)
                        
                        totalHeight = totalHeight + (view1.frame.height)
                        
                        viewArray.append(view1)
                        headerArray.append(view1)
                        view1.contentMode = .ScaleAspectFit
                        view1.hidden = false
                        view1.setNeedsDisplay()
                        scrollView.addSubview(view1)
                        
                    } else {
                        //for all subsequent images
                        //Set the position of the image to start at the bottom of the last image + offset
                        
                        let view2 = UILabel(frame: CGRectMake(0, totalHeight + offset, width, headerArray[0].frame.height))
                        let label = UILabel(frame: view2.bounds)
                        label.font = label.font.fontWithSize(30)
                        label.text = (row["content"] as! String)
                        
                        label.textAlignment = NSTextAlignment.Center
                        label.center = view2.center
                        view2.addSubview(label)
                        
                        totalHeight = totalHeight + (view2.frame.height) + offset
                        
                        viewArray.append(view2)
                        headerArray.append(view2)
                        view2.contentMode = .ScaleAspectFit
                        view2.hidden = false
                        view2.setNeedsDisplay()
                        view2.userInteractionEnabled = true
                        scrollView.addSubview(view2)
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
                        
                        totalHeight = totalHeight + (view1.frame.height)
                        
                        viewArray.append(view1)
                        headerArray.append(view1)
                        view1.contentMode = .ScaleAspectFit
                        view1.hidden = false
                        view1.setNeedsDisplay()
                        scrollView.addSubview(view1)
                    } else {
                        //for all subsequent images
                        //Set the position of the image to start at the bottom of the last image + offset
                        
                        let offset = offset + 5
                        
                        // TODO FIND A METHOD TO DO THIS BETTER
                        let chars: CGFloat = CGFloat((row["content"] as! String).characters.count)
                        let lines: CGFloat = chars/30
                        let view2 = UILabel(frame: CGRectMake(0, totalHeight/2+offset, width, 25 * lines))
                        let label = UILabel(frame: view2.frame)
                        label.text = (row["content"] as! String)
                        // label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                        label.numberOfLines = 0
                        
                        label.textAlignment = NSTextAlignment.Center
                        label.center = view2.center
                        view2.addSubview(label)
                        
                        totalHeight = totalHeight + (view2.frame.height) + offset
                        
                        viewArray.append(view2)
                        headerArray.append(view2)
                        view2.contentMode = .ScaleAspectFit
                        view2.hidden = false
                        view2.setNeedsDisplay()
                        
                        scrollView.addSubview(view2)
                        
                    }
                    
                default :
                    print("THIS CAN NEVER HAPPEN")
                    
                }
            } else {
                
                var img: UIImage?
                
                if let imageAtRow : String? = row["url"] as? String{
                    img = imageHandler.sharedInstance.loadImageFromPath(imageAtRow)
                    
                } else {
                    img = UIImage()
                }
                
                if viewArray.count == 0{
                    //If the first item is an image
                    
                    let imageView1 = UIImageView(frame: CGRectMake(0, 0, width, height/2))
                    totalHeight = totalHeight + (imageView1.frame.height)
                    viewArray.append(imageView1)
                    imageView1.image = img
                    imageView1.contentMode = .ScaleAspectFit
                    imageView1.setNeedsDisplay()
                    scrollView.addSubview(imageView1)
                } else {
                    //CLEAN
                    
                    //for all subsequent images
                    //Set the position of the image to start at the bottom of the last image + offset
                    let offset = offset + 5
                    
                    let imageView2 = UIImageView(frame: CGRectMake(0, totalHeight + offset, width, height/2))
                    totalHeight = totalHeight + imageView2.frame.height + offset
                    viewArray.append(imageView2)
                    imageView2.image = img
                    imageView2.contentMode = .ScaleAspectFit
                    imageView2.setNeedsDisplay()
                    
                    scrollView.addSubview(imageView2)
                }
            }
        }

        scrollView.contentSize = CGSizeMake(self.view.frame.size.width, totalHeight+offset)

    }
    
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
    // Get the new view controller using segue.destinationViewController.
    // Pass the selected object to the new view controller.
        if segue.identifier == "segueToVideo"{
            if let destination = segue.destinationViewController as? VideoViewController {
                destination.videoUrl =
                    "https://clips.vorwaerts-gmbh.de/VfE_html5.mp4"
                //Heres one I made ealier
                
            }
    }
    
    }
}
