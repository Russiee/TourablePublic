//
//  POITableViewController.swift
//  Tourable
//
//  Created by Daniel Baryshnikov on 07/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit
import AVFoundation
import AVKit

///Deals with the creation of views for POIs, creating thumbnails for videos, playing videos and creating navigation between POIS.
class POITableViewController: UITableViewController {
    
    var poiID = ""
    var superSectionID = ""
    var POIList = [String]()
    var poiViews = [UIView]()
    var nextNavigationView = [UIView]()
    var previousNavigationView = [UIView]()
    var videoList = [NSURL]()
    let recognizer = UITapGestureRecognizer()
    var player = AVPlayer()
    let width = UIScreen.mainScreen().bounds.size.width
    var quizes = [NSDictionary]()
    
    @IBOutlet var PreviousPoiButton: UIBarButtonItem!
    
    @IBOutlet var PreviousSectionButton: UIBarButtonItem!
    
    @IBAction func BackToOverView() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        getPOIS()
        let pointToDisplay = POIParser().getTourSection(poiID)
        self.title = pointToDisplay.title

        createSubviews(pointToDisplay.post)

        createNavigationViews()
        //reloads the tableViewData so that the Views are shown, potential move to viewWillAppear the createSubViews method
        self.tableView.reloadData()
        self.tableView.separatorStyle = UITableViewCellSeparatorStyle.None
        self.tableView.clipsToBounds = true
    }

    ///Returns a list of pois that are in this section
    func getPOIS(){
        POIList = []
        for POIS in (((NSUserDefaults.standardUserDefaults().objectForKey(superSectionID)) as! NSDictionary)["pois"]) as! NSArray{
            
            POIList.append(POIS["objectId"] as! String)
        }
    }

    ///Appends necessary labels to nextNavigationView and previousNavigationView that will display depending if current POI has a next/previous POI.
    func createNavigationViews(){
        //Reset the navigaton view array for every call of this method.
        nextNavigationView = []
        previousNavigationView = []

        //Find positionof current POI in respect to all other POIS in this section.
        let Z = POIList.indexOf(poiID)!

        //if only poi in the list then won't add any labels for navigation
        if(POIList.count > 1){

            //case where the poi is first in the list
            if(POIList.indexOf(poiID) == 0){
                //get the next POI
                let nextPOIID = POIList[Z + 1]
                //get the next POI's title to display
                let nextPOITitle = (NSUserDefaults.standardUserDefaults().objectForKey(nextPOIID))!["title"] as! String
                let nextPOILabel = UILabel(frame: CGRectMake(0,0,UIScreen.mainScreen().bounds.size.width-12, 59))
                nextPOILabel.text = "  Go to next POI (\(nextPOITitle))"
                nextPOILabel.font = UIFont.systemFontOfSize(16)
                nextPOILabel.userInteractionEnabled = true
                nextNavigationView.append(nextPOILabel)
            }
                
                //case where poi is last in the list
            else if(POIList.indexOf(poiID) == (POIList.count - 1)){
                //get the previous POI
                let previousPOIID = POIList[Z - 1]
                //get the previous POI's title to display
                let previousPOITitle = (NSUserDefaults.standardUserDefaults().objectForKey(previousPOIID))!["title"] as! String
                let previousPOILabel = UILabel(frame: CGRectMake(0,0,UIScreen.mainScreen().bounds.size.width-12, 59))
                previousPOILabel.text = "  Go to previous POI (\(previousPOITitle))"
                previousPOILabel.font = UIFont.systemFontOfSize(16)
                previousPOILabel.userInteractionEnabled = true
                previousNavigationView.append(previousPOILabel)
            }
                
                //case where the poi has pois on either side of it in the POIList
            else{
                //get the next POI
                let nextPOIID = POIList[Z + 1]
                //getting next POI's title to display
                let nextPOITitle = (NSUserDefaults.standardUserDefaults().objectForKey(nextPOIID))!["title"] as! String
                let nextPOILabel = UILabel(frame: CGRectMake(0,0,UIScreen.mainScreen().bounds.size.width-12, 59))
                nextPOILabel.text = " Go to next POI (\(nextPOITitle))"
                
                nextPOILabel.font = UIFont.systemFontOfSize(16)
                nextPOILabel.userInteractionEnabled = true
                nextNavigationView.append(nextPOILabel)
                
                //get the previous POI
                let previousPOIID = POIList[Z - 1]
                //get the previous POI's title to display
                let previousPOITitle = (NSUserDefaults.standardUserDefaults().objectForKey(previousPOIID))!["title"] as! String
                let previousPOILabel = UILabel(frame: CGRectMake(0,0,UIScreen.mainScreen().bounds.size.width-12, 59))
                previousPOILabel.text = "  Go to previous POI (\(previousPOITitle))"
                previousPOILabel.font = UIFont.systemFontOfSize(16)
                previousPOILabel.userInteractionEnabled = true
                previousNavigationView.append(previousPOILabel)
                
            }
        }
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        //so we know how many cells we have, this is why the navigation views are in an array.
        return poiViews.count + nextNavigationView.count + previousNavigationView.count
    }
    
    func createSubviews(post: NSArray){
        //deal with text (header/body), media (image/video) types and any desriptions linked with the media.
        let width = UIScreen.mainScreen().bounds.size.width
        
        for row in post{
            let type = (row["type"] as! String).lowercaseString
            switch type{
                
            case "header" :
                
                let label = UITextView(frame: CGRectMake(0, 0, width, 40))
                label.textContainerInset = UIEdgeInsetsMake(10, 12, 0, 12)
                label.editable = false
                label.font = UIFont.boldSystemFontOfSize(18)
                label.text = row["content"] as? String
                
                label.sizeToFit()
                label.textAlignment = NSTextAlignment.Center
                label.contentMode = .ScaleAspectFill
                label.textColor = UIColor(red: 22/255, green: 43/255, blue: 73/255, alpha: 1.0)
                label.backgroundColor = UIColor.groupTableViewBackgroundColor()
                poiViews.append(label)
                label.scrollEnabled = false
                
                
            case "body" :
                //make sure that we get the right amount of lines to textfield is correct size.
                let chars: CGFloat = CGFloat((row["content"] as! String).characters.count)
                var lines: CGFloat = chars/60
                if lines < 2{
                    lines = 2
                }else{
                    lines = lines+1
                }
                let label = UITextView(frame: CGRectMake(0, 0, width, 23 * lines))
                label.textContainerInset = UIEdgeInsetsMake(0, 12, 0, 12)
                label.editable = false
                label.font = UIFont.systemFontOfSize(14)
                label.text = (row["content"] as! String)
                label.scrollEnabled = false
                label.textColor = UIColor(red: 74/255, green: 95/255, blue: 126/255, alpha: 1.0)
                label.backgroundColor = UIColor.groupTableViewBackgroundColor()
                poiViews.append(label)
                
                
            case "image" :
                
                var img: UIImage?
                let imageAtRow : String? = row["url"] as? String
                var imageView1 = UIImageView()
                img = imageHandler.sharedInstance.loadImageFromPath(imageAtRow)
                if img == nil{
                    print(row["url"])
                    ImageLoader.sharedLoader.imageForUrl((row["url"] as? String)!, completionHandler:{(image: UIImage?, url: String) in
                        let img = image
                        let  h_fact = width / (image?.size.width)!
                        let new_height = (image?.size.height)! * h_fact
                        let new_width = (image?.size.width)! * h_fact
                        
                        imageView1 = UIImageView(frame: CGRectMake(0, 0, new_width, new_height))
                        imageView1.image = img
                        imageView1.contentMode = .ScaleAspectFit
                        imageView1.setNeedsDisplay()
                        self.poiViews.append(imageView1)
                        
                        let chars: CGFloat = CGFloat((row["description"] as! String).characters.count)
                        var lines: CGFloat = chars/60
                        if lines < 2{
                            lines = 2
                        }else{
                            lines = lines + 1
                        }
                        let label = UITextView(frame: CGRectMake(0, 0, width, 25 * lines))
                        label.textContainerInset = UIEdgeInsetsMake(5, 12, 0, 12)
                        label.editable = false
                        label.font = UIFont.italicSystemFontOfSize(16)
                        label.text = (row["description"] as! String)
                        
                        label.scrollEnabled = false
                        label.contentMode = .ScaleAspectFill
                        label.textColor = UIColor(red: 74/255, green: 95/255, blue: 126/255, alpha: 1.0)
                        label.backgroundColor = UIColor.groupTableViewBackgroundColor()
                        self.poiViews.append(label)
                        self.tableView.reloadInputViews()
                        self.tableView.reloadData()
                    })
                } else {
                    //calculate the correct size of width and height for imageView based of original images width compared to the devices width.
                    let  h_fact = width / (img?.size.width)!
                    let new_height = (img?.size.height)! * h_fact
                    let new_width = (img?.size.width)! * h_fact
                    
                    imageView1 = UIImageView(frame: CGRectMake(0, 0, new_width, new_height))
                    imageView1.image = img
                    imageView1.contentMode = .ScaleAspectFit
                    imageView1.setNeedsDisplay()
                    self.poiViews.append(imageView1)
                    
                    //deal with the creation of medias description
                    let chars: CGFloat = CGFloat((row["description"] as! String).characters.count)
                    var lines: CGFloat = chars/60
                    if lines < 2{
                        lines = 2
                    }else{
                        lines = lines + 1
                    }
                    let label = UITextView(frame: CGRectMake(0, 0, width, 25 * lines))
                    label.textContainerInset = UIEdgeInsetsMake(5, 12, 0, 12)
                    label.editable = false
                    label.font = UIFont.italicSystemFontOfSize(16)
                    label.text = (row["description"] as! String)
                    
                    label.scrollEnabled = false
                    label.contentMode = .ScaleAspectFill
                    label.textColor = UIColor(red: 74/255, green: 95/255, blue: 126/255, alpha: 1.0)
                    label.backgroundColor = UIColor.groupTableViewBackgroundColor()
                    self.poiViews.append(label)
                }
                
            case "video":
                do {
                    //load videos url from where we saved it
                    let videoURL = videoHandler.sharedInstance.loadVideoPath(row["url"] as? String)!
                    videoList.append(videoURL)
                    let asset = AVURLAsset(URL: videoURL, options: nil)
                    let imgGenerator = AVAssetImageGenerator(asset: asset)
                    let cgImage = try imgGenerator.copyCGImageAtTime(CMTimeMake(0, 1), actualTime: nil)
                    let uiImage = UIImage(CGImage: cgImage)
                    
                    let  h_fact = width / (uiImage.size.width)
                    let new_height = uiImage.size.height * h_fact
                    let new_width = uiImage.size.width * h_fact
                    
                    let previewImage = createPreviewImage(uiImage, Width: new_width, Height: new_height)
                    
                    let imageView = UIImageView(frame: CGRectMake(0, 0, new_width, new_height))
                    imageView.userInteractionEnabled = true
                    
                    //adds a recognizer so that video starts when image clicked.
                    recognizer.addTarget(self, action: "videoThumbnailTapped")
                    imageView.addGestureRecognizer(recognizer)
                    imageView.clipsToBounds = true
                    imageView.image = previewImage
                    imageView.contentMode = .ScaleAspectFit
                    imageView.setNeedsDisplay()
                    poiViews.append(imageView)
                    
                    let chars: CGFloat = CGFloat((row["description"] as! String).characters.count)
                    var lines: CGFloat = chars/52
                    if lines < 2{
                        lines = 2
                    }else{
                        lines = lines + 1
                    }
                    //create TextView to store all our text
                    let text = UITextView(frame: CGRectMake(0, 0, width, 25 * lines))
                    //adds the "padding" you see on left and right hand side
                    text.textContainerInset = UIEdgeInsetsMake(0, 12, 0, 12)
                    //so Users cannot edit the tour text
                    text.editable = false
                    text.text = (row["description"] as! String)
                    //descriptions are in Italics
                    text.font = UIFont.italicSystemFontOfSize(14)
                    text.contentMode = .ScaleAspectFill
                    text.scrollEnabled = false
                    text.textColor = UIColor(red: 74/255, green: 95/255, blue: 126/255, alpha: 1.0)
                    text.backgroundColor = UIColor.groupTableViewBackgroundColor()
                    poiViews.append(text)
                    // lay out this image view, or if it already exists, set its image property to uiImage
                } catch let error as NSError {
                    print("Error generating thumbnail: \(error)")
                }
            case "quiz":
                //create a button to add to the poi to then take the user to take the tour
                quizes.append(row as! NSDictionary)
                let button   = UIButton(type: UIButtonType.Custom) as UIButton
                button.frame = CGRectMake(0 , 0, width, 60)
                button.layer.cornerRadius = 0
                button.titleLabel?.font = UIFont.systemFontOfSize(17)
                button.layer.masksToBounds = true
                button.contentHorizontalAlignment = .Left
                button.contentEdgeInsets = UIEdgeInsets(top: 0, left: 10, bottom: 0, right: 0)
                button.backgroundColor = UIColor.whiteColor()
                button.setTitleColor(UIColor.blackColor(), forState: .Normal)
                
                button.setTitle(row["question"] as? String, forState: UIControlState.Normal)
                button.addTarget(self, action: "quizButtonAction:", forControlEvents: UIControlEvents.TouchUpInside)
                button.tag = quizes.count-1
                poiViews.append(button)
            default:
                print("something is wrong")
            }
        }
    }
    
    ///Takes as parameter image you want play button overlayed on, width and height wanted for view. The returned image has a play button overlayed on top of the thumbnail of the video.
    func createPreviewImage(image: UIImage, Width: CGFloat, Height: CGFloat) -> UIImage{
        let widthScale = Width / image.size.width
        let newWidth = image.size.width * widthScale
        let heightScale = Height / image.size.height
        let newHeight = image.size.height * heightScale
        
        UIGraphicsBeginImageContext(CGSizeMake(newWidth, newHeight))
        image.drawInRect(CGRectMake(0, 0, newWidth, newHeight))
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        let newSize = CGSizeMake(Width, Height) // set this to what you need
        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        
        newImage.drawInRect(CGRect(origin: CGPointZero, size: newSize))
        let playButton = UIImage(named: "PlayButton")
        
        let  h_fact = width / (playButton!.size.width)
        let new_height = playButton!.size.height * h_fact
        let new_width = playButton!.size.width * h_fact
        
        (UIImage(named: "PlayButton"))!.drawInRect(CGRect(origin: CGPoint(x: 0, y: 0), size: CGSize(width: new_width-1, height: new_height-1)))
        
        let finalImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return finalImage
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
        //have to intialise it to empty cell, which we choose will follow on.
        var cell = UITableViewCell()
        
        //gets rid of subviews before adding new ones to make sure no overlaps occur
        if(indexPath.row < poiViews.count){
            //as the row is still in the poiViews we use the poiCells cell. We add the view that corresponds to the view in poiViews.
            cell = tableView.dequeueReusableCellWithIdentifier("poiCells", forIndexPath: indexPath)

            //to stop overlap of views.
            for view in cell.contentView.subviews {
                view.removeFromSuperview()
            }

            //adding the contents of the post into our tableView
            cell.contentView.addSubview(poiViews[indexPath.row])
            cell.selectionStyle = UITableViewCellSelectionStyle.None
        }
        else if (indexPath.row < (poiViews.count + 1)){
            //if one of the navigation views isnt empty it will be added, if there is a view in both then nextPOI will be added here.
            var navigationToAdd: UIView
            if(nextNavigationView.count != 0){
                //now we use our NextPOI cell to load the nagivation view into
                cell = tableView.dequeueReusableCellWithIdentifier("NextPOI", forIndexPath: indexPath)

                for view in cell.contentView.subviews {
                    view.removeFromSuperview()
                }

                //set the tag so we know which cell is for NextPOI navigation.
                cell.tag = 999
                navigationToAdd = nextNavigationView[0]
            }
            else {
                //if no nextPOI navigation then we add a previous poi navigation instead.
                cell = tableView.dequeueReusableCellWithIdentifier("PreviousPOI", forIndexPath: indexPath)

                for view in cell.contentView.subviews {
                    view.removeFromSuperview()
                }

                //different tag to nextPOI, again so we know which cell is for PreviousPOI navigation.
                cell.tag = 998
                navigationToAdd = previousNavigationView[0]
            }
            
            cell.contentView.addSubview(navigationToAdd)
            cell.accessoryType = .DisclosureIndicator
            
        }
        else if (indexPath.row < (poiViews.count + 2)){
            //as if we have 2 navigation cells as know that the first one is always nextPOI, so we need to add previousPOI. This is due to our designs.
            cell = tableView.dequeueReusableCellWithIdentifier("PreviousPOI", forIndexPath: indexPath)
            for view in cell.contentView.subviews {
                view.removeFromSuperview()
                
            }
            cell.contentView.addSubview(previousNavigationView[0])
            cell.tag = 998
        }
        
        return cell
    }

    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
        //here we check for navigation based on the cell's tag. if 999 then nextPOI, if 998 then previous poi. Every other cell doesnt navigate.
        
        let Z = POIList.indexOf(poiID)!
        if(tableView.cellForRowAtIndexPath(indexPath)?.tag == 999){
            //change the poiID form the pois in the list
            poiID = POIList[Z + 1]

            //reset the poiViews before we reloadData
            poiViews=[]

            //reset all views of current POI
            self.tableView.reloadData()

            //changes the current ViewController to change to new POI.
            viewDidLoad()
        }
        
        // same principle here except for previous POI
        else if(tableView.cellForRowAtIndexPath(indexPath)?.tag == 998){
            poiID = (POIList)[Z - 1]
            poiViews = []
            self.tableView.reloadData()
            viewDidLoad()
        }
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        //takes us to new view with the quiz there
        if segue.identifier == "quizSegue"{

            let destination =  segue.destinationViewController as! QuizTableViewController
            destination.quiz = sender as! Quiz
        }
    }

    func quizButtonAction(sender: UIButton) {
        //when a quiz button gets pressed, creates new quiz and then goes to that view by performing segue to it
        let quizData = quizes[sender.tag]
        let quiz = Quiz()

        quiz.question = quizData["question"] as! String
        quiz.options = quizData["options"] as! NSArray
        quiz.correct = quizData["solution"] as! Int

        performSegueWithIdentifier("quizSegue", sender: quiz)
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        //varying heights so deals with padding
        if indexPath.row < poiViews.count {
            return poiViews[indexPath.row].frame.height+5
        }
        return 60
    }
    
    ///Takes as parameter videoUrl and a boolean. Creates an AVPlayerViewController to play the video and loops should the video need to.
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

    ///Loop the video when this is notified by the player.
    func playerDidFinishPlaying() {
        
        //Defines the start of the video and sets the video back there.
        let restartTime : Int64 = 0
        let preferredTimeScale : Int32 = 1
        let timeToGoTo : CMTime = CMTimeMake(restartTime, preferredTimeScale)
        self.player.seekToTime(timeToGoTo)
        self.player.play()
    }
    
}
