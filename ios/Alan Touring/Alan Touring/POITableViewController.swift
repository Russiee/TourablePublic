//
//  POITableViewController.swift
//  Alan Touring
//
//  Created by Daniel Baryshnikov on 07/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit
import AVFoundation
import AVKit

class POITableViewController: UITableViewController {

    var poiID = ""
    var superSectionID = ""
    var POIList = [String]()
    var poiViews = [UIView]()
    var videoList = [NSURL]()
    let recognizer = UITapGestureRecognizer()
    var player = AVPlayer()

    @IBOutlet var PreviousPoiButton: UIBarButtonItem!
    
    @IBOutlet var PreviousSectionButton: UIBarButtonItem!
    
    @IBOutlet var NextPOIButton: UIBarButtonItem!
    
    @IBAction func PreviousPOI(sender: UIBarButtonItem) {
        print("clicked prevousPOI")
        let Z = POIList.indexOf(poiID)!
        //print(Z)
        
        poiID = (POIList)[Z - 1]
        poiViews = []
        self.tableView.reloadData()
        viewDidLoad()
        print("previous POi now displaying")
    }
    
    @IBAction func PreviousSection(sender: UIBarButtonItem) {
            self.navigationController?.popViewControllerAnimated(true)
    }
    
    @IBAction func NextPOI(sender: UIBarButtonItem) {
        print("clicked next POI")
        let Z = POIList.indexOf(poiID)!
        
        poiID = (POIList)[Z + 1]
        poiViews=[]
        self.tableView.reloadData()
        viewDidLoad()
        print("next poi now displaying")
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        getPOIS()
        createToolBar()
        let pointToDisplay = POIParser().getTourSection(poiID)
        print(pointToDisplay.post)
        createSubviews(pointToDisplay.post)
        //reloads the tableViewData so that the Views are shown, potential move to viewWillAppear the createSubViews method
        self.tableView.reloadData()
        self.tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        self.tableView.clipsToBounds = true
        
        self.navigationController?.setToolbarHidden(false, animated: false)
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }
    
    func getPOIS(){
        POIList = []
        for POIS in (((NSUserDefaults.standardUserDefaults().objectForKey(superSectionID)) as! NSDictionary)["pois"]) as! NSArray{
            
            POIList.append(POIS["objectId"] as! String)
        }
    }
    
    func createToolBar(){
        PreviousPoiButton.enabled = true; NextPOIButton.enabled = true; PreviousSectionButton.enabled = true
        
        if(POIList.count > 1){
            if(POIList.indexOf(poiID) == 0){
                PreviousPoiButton.enabled = false
            }
            else if(POIList.indexOf(poiID) == (POIList.count - 1)){
                NextPOIButton.enabled = false
            }
            else{
                PreviousSectionButton.enabled = false
            }
        }
        else{
            PreviousPoiButton.enabled = false
            NextPOIButton.enabled = false
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
                        
                        let view2 = UILabel(frame: CGRectMake(0, 0, width, 40))
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
                    
                case "image" :

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
                            
                            let chars: CGFloat = CGFloat((row["description"] as! String).characters.count)
                            let lines: CGFloat = chars/30
                            let view2 = UILabel(frame: CGRectMake(0, 0, width-10, 35 * lines))
                            let label = UILabel(frame: view2.frame)
                            label.text = (row["description"] as! String)
                            // label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                            label.numberOfLines = 0
                            
                            label.textAlignment = NSTextAlignment.Left
                            label.center = view2.center
                            view2.addSubview(label)
                            
                            viewArray.append(view2)
                            headerArray.append(view2)
                            view2.contentMode = .ScaleAspectFit
                            view2.hidden = false
                            view2.setNeedsDisplay()
                            poiViews.append(label)
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
                            
                            let chars: CGFloat = CGFloat((row["description"] as! String).characters.count)
                            let lines: CGFloat = chars/30
                            let view2 = UILabel(frame: CGRectMake(0, 0, width-10, 35 * lines))
                            let label = UILabel(frame: view2.frame)
                            label.text = (row["description"] as! String)
                            // label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                            label.numberOfLines = 0
                            
                            label.textAlignment = NSTextAlignment.Left
                            label.center = view2.center
                            view2.addSubview(label)
                            
                            viewArray.append(view2)
                            headerArray.append(view2)
                            view2.contentMode = .ScaleAspectFit
                            view2.hidden = false
                            view2.setNeedsDisplay()
                            poiViews.append(label)
                        }
                 case "video":
                    print("video m8")
                    var err: NSError? = nil
                    do {
                        let videoURL = videoHandler.sharedInstance.loadVideoPath(row["url"] as! String)!
                        videoList.append(videoURL)
                        let asset = AVURLAsset(URL: videoURL, options: nil)
                        let imgGenerator = AVAssetImageGenerator(asset: asset)
                        let cgImage = try imgGenerator.copyCGImageAtTime(CMTimeMake(0, 1), actualTime: nil)
                        let uiImage = UIImage(CGImage: cgImage)
                       // let imageView = UIImageView(image: uiImage)
                        let imageView = UIImageView(frame: CGRectMake(0, 0, width, height/2))
                        imageView.userInteractionEnabled = true
                        

                        recognizer.addTarget(self, action: "videoThumbnailTapped")
                        
                        //finally, this is where we add the gesture recognizer, so it actually functions correctly
                        imageView.addGestureRecognizer(recognizer)
                        
                        imageView.image = uiImage
                        viewArray.append(imageView)
                        imageView.contentMode = .ScaleAspectFit
                        imageView.setNeedsDisplay()
                        poiViews.append(imageView)
                        
                        let chars: CGFloat = CGFloat((row["description"] as! String).characters.count)
                        let lines: CGFloat = chars/30
                        let view2 = UILabel(frame: CGRectMake(0, 0, width-10, 35 * lines))
                        let label = UILabel(frame: view2.frame)
                        label.text = (row["description"] as! String)
                        // label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                        label.numberOfLines = 0
                        
                        label.textAlignment = NSTextAlignment.Left
                        label.center = view2.center
                        view2.addSubview(label)
                        
                        viewArray.append(view2)
                        headerArray.append(view2)
                        view2.contentMode = .ScaleAspectFit
                        view2.hidden = false
                        view2.setNeedsDisplay()
                        poiViews.append(label)
                        // lay out this image view, or if it already exists, set its image property to uiImage
                    } catch let error as NSError {
                        print("Error generating thumbnail: \(error)")
                    }
                    
                default:
                    print("something is wrong")
            }
        }
    }
    func videoThumbnailTapped(){
        let url = videoList[0]
        do{
            try self.playVideo(url.absoluteString, loop: true)
        }catch{
            print("error playing video")
        }
    }
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("poiCells", forIndexPath: indexPath)

        // Configure the cell...
        //gets rid of subviews before adding new ones to make sure no overlaps occur
        for view in cell.contentView.subviews {
            view.removeFromSuperview()
            
        }
        //adding the contents of the post into our tableView
        cell.contentView.addSubview(poiViews[indexPath.row])
        cell.selectionStyle = UITableViewCellSelectionStyle.None
        
        return cell
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return poiViews[indexPath.row].frame.height
    }
   
    //used to display a video when it is tapped on screen.
    //videoUrl: file url or online url of video to display
    //loop: should the video repeat
    func playVideo(videoUrl: String, loop: Bool) throws {
        //path of video to play
        let path = videoHandler.sharedInstance.loadVideoPath(videoUrl)
        //Create a new player with the path given to it.
        self.player = AVPlayer(URL: path!)
        //create a new fullscreen controller for the video
        let playerController = AVPlayerViewController()
        //add the videoplyer to the controller
        playerController.player = player
        //Notify when the video has finished so we can loop it if required.
        NSNotificationCenter.defaultCenter().addObserver(self, selector: "playerDidFinishPlaying",
            name: AVPlayerItemDidPlayToEndTimeNotification, object: player.currentItem)
        self.presentViewController(playerController, animated: true) {
            //Start the video
            self.player.play()
        }
    }
    //Loop the video when this is notified by the player.
    func playerDidFinishPlaying() {
        
        //Defines the start of the video and sets the video back there.
        let restartTime : Int64 = 0
        let preferredTimeScale : Int32 = 1
        let timeToGoTo : CMTime = CMTimeMake(restartTime, preferredTimeScale)
        self.player.seekToTime(timeToGoTo)
        self.player.play()
        
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
