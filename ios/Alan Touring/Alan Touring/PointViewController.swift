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
    
    override func viewDidLoad() {
        super.viewDidLoad()

        scrollView.delegate = self
        
        let image1 = imageHandler().loadImageFromPath("http://i.imgur.com/THSPZUv.jpg")
        let width = UIScreen.mainScreen().bounds.size.width
        let height = UIScreen.mainScreen().bounds.size.height
     
        
        let view1 = UIImageView(frame: CGRectMake(0, 0, width, height/2))
        let view2 = UIImageView(frame: CGRectMake(0, view1.frame.height + 5, width, height/2))
        
        scrollView.contentSize = CGSizeMake(self.view.frame.size.width, height)
        
        
        
        
        
        
        
        
        //let view1 = UIImageView(image: image1)
        //view1.contentMode = .ScaleAspectFill
//        let spacer = UIView(frame: CGRectMake(0, view1.frame.minY, width, 20))
//        spacer.opaque = true
        
        //let view2 = UIImageView(image: image1)
        //view2.contentMode = .ScaleAspectFill
        
        view2.image = image1
        view1.image = image1
        
        view1.backgroundColor = UIColor.blackColor()
        view2.backgroundColor = UIColor.blackColor()
        
        view1.contentMode = .ScaleAspectFit
        view2.contentMode = .ScaleAspectFit
        
        view1.setNeedsDisplay()
        view2.setNeedsDisplay()
        
//        view2.autoresizesSubviews = true
//        view1.autoresizesSubviews = true
       // view1.
        //scrollView.autoresizesSubviews = true
        scrollView.addSubview(view1)
        //scrollView.sendSubviewToBack(view1)
//        scrollView.addSubview(spacer)
//        self.scrollView.sendSubviewToBack(spacer)
        scrollView.addSubview(view2)
//        self.scrollView.sendSubviewToBack(view2)
       // scrollView.
//        scrollView.setNeedsDisplay()
       // scrollView.
        // Do any additional setup after loading the view.
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
