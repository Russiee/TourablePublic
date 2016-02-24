//
//  PointViewController.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 23/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import UIKit

class PointViewController: UIViewController, UIScrollViewDelegate {


    @IBOutlet weak var scrollView: UIScrollView!
    var poiID = ""
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        scrollView.delegate = self
        
       let pointToDisplay = POIParser().getTourSection(poiID)
        
        self.createSubviews(pointToDisplay.post)

    }
    
    func createSubviews(post: NSArray){
 
        let width = UIScreen.mainScreen().bounds.size.width
        let height = UIScreen.mainScreen().bounds.size.height
        
        var offset: CGFloat = 5
        var totalHeight: CGFloat = 0
        var viewArray = [UIView]()
        var headerArray = [UIView]()
        for row in post{

        //print(row)
        
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
                    print(label.text)
                    label.textAlignment = NSTextAlignment.Center
                    label.center = view1.center
                    view1.addSubview(label)
                       print("header at \(totalHeight)")
                    totalHeight = totalHeight + (view1.frame.height)
                  
                    viewArray.append(view1)
                    headerArray.append(view1)
                    view1.contentMode = .ScaleAspectFit
                    view1.hidden = false
                    view1.setNeedsDisplay()
                    scrollView.addSubview(view1)
                }else{
                    //for all subsequent images
                    //Set the position of the image to start at the bottom of the last image + offset
                    
                    let view2 = UILabel(frame: CGRectMake(0, totalHeight + offset, width, headerArray[0].frame.height))
                    let label = UILabel(frame: view2.bounds)
                    label.font = label.font.fontWithSize(30)
                    label.text = (row["content"] as! String)
                    print(label.text)
                    label.textAlignment = NSTextAlignment.Center
                    label.center = view2.center
                    view2.addSubview(label)
                    print("header at \(totalHeight)")
                    totalHeight = totalHeight + (view2.frame.height) + offset
                    
                    viewArray.append(view2)
                    headerArray.append(view2)
                    view2.contentMode = .ScaleAspectFit
                    view2.hidden = false
                    view2.setNeedsDisplay()
                    view2.userInteractionEnabled = true
                    scrollView.addSubview(view2)
                }
                print("ADDED HEADER \(row["content"])")
            case "body":
                if viewArray.count == 0{
                    print("ADDING BODY AS FIRST ITEM")
                    let chars: CGFloat = CGFloat((row["content"] as! String).characters.count)
                    let lines: CGFloat = chars/30
                    let view1 = UIView(frame: CGRectMake(0, 0, width, 25 * lines))
                    let label = UILabel(frame: view1.bounds)
                    label.text = (row["content"] as! String)
                    //label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                    label.numberOfLines = 0
                    print(label.text)
                    label.textAlignment = NSTextAlignment.Center
                    label.center = view1.center
                    view1.addSubview(label)
                     print("Putting body at\(totalHeight)")
                    totalHeight = totalHeight + (view1.frame.height)
                   
                    viewArray.append(view1)
                    headerArray.append(view1)
                    view1.contentMode = .ScaleAspectFit
                    view1.hidden = false
                    view1.setNeedsDisplay()
                    scrollView.addSubview(view1)
            }else{
                //for all subsequent images
                //Set the position of the image to start at the bottom of the last image + offset
                    print("ADDING BODY AS SECOND ITEM")
                    let offset = offset + 5
                    print(viewArray.count)
                    //TODO FIND A METHOD TO DO THIS BETTER
                    let chars: CGFloat = CGFloat((row["content"] as! String).characters.count)
                    let lines: CGFloat = chars/30
                    let view2 = UILabel(frame: CGRectMake(0, totalHeight/2+offset, width, 25 * lines))
                    let label = UILabel(frame: view2.frame)
                    label.text = (row["content"] as! String)
                   // label.lineBreakMode = .ByWordWrapping // or NSLineBreakMode.ByWordWrapping
                    label.numberOfLines = 0
                    print(label.text)
                    label.textAlignment = NSTextAlignment.Center
                    label.center = view2.center
                    view2.addSubview(label)
                    print("putting body at \(totalHeight)")
                    totalHeight = totalHeight + (view2.frame.height) + offset
                    
                    viewArray.append(view2)
                    headerArray.append(view2)
                    view2.contentMode = .ScaleAspectFit
                    view2.hidden = false
                    view2.setNeedsDisplay()

                    scrollView.addSubview(view2)
                    print("ADDED BODY \(row["content"])")
                }
            default :
                print("THIS CAN NEVER HAPPEN")
                
            }
        }else {
            //Found an image
            print("found url: \(row["url"])")
            
            let img = imageHandler().loadImageFromPath(row["url"] as! String)
            
            if viewArray.count == 0{
                //If the first item is an image
                print("putting image at point \(totalHeight + offset)")
                let imageView1 = UIImageView(frame: CGRectMake(0, 0, width, height/2))
                totalHeight = totalHeight + (imageView1.frame.height)
                viewArray.append(imageView1)
                imageView1.image = img
                imageView1.contentMode = .ScaleAspectFit
                imageView1.setNeedsDisplay()
                scrollView.addSubview(imageView1)
            }else{
                //CLEAN
                print("adding image as second item")
                //for all subsequent images
                //Set the position of the image to start at the bottom of the last image + offset
                let offset = offset + 5
                print("putting image at point \(totalHeight + offset)")
                
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
    

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
