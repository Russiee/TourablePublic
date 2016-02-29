//  imageHandler.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 20/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
// with help from: http://stackoverflow.com/questions/24231680/loading-image-from-url

import Foundation
import UIKit

private let fileManager = NSFileManager.defaultManager()

class imageHandler {

    init() {
        if NSUserDefaults.standardUserDefaults().objectForKey("imageKeys") == nil{
            let dictonary = Dictionary<String,String>()
            NSUserDefaults.standardUserDefaults().setObject(dictonary, forKey: "imageKeys")
            NSUserDefaults.standardUserDefaults().synchronize()
            do {
                //2
                try fileManager.createDirectoryAtPath(getDocumentsURL().absoluteString,
                    withIntermediateDirectories: false, attributes: nil)
            } catch {
                print("An Error was generated creating directory")
            }
        }
    }
    
    
    func getDataFromUrl(url:NSURL, completion: ((data: NSData?, response: NSURLResponse?, error: NSError? ) -> Void)) {
        NSURLSession.sharedSession().dataTaskWithURL(url) { (data, response, error) in
            completion(data: data, response: response, error: error)
            }.resume()
    }

    func getDocumentsURL() -> NSURL {
        let documentsURL = NSFileManager.defaultManager().URLsForDirectory(.DocumentDirectory, inDomains: .UserDomainMask)[0]
        return documentsURL
    }

    func fileInDocumentsDirectory(filename: String) -> String {
        let fileURL = getDocumentsURL().URLByAppendingPathComponent(filename+".jpg")
        return fileURL.path!
    }

    func saveImage(image: UIImage, name: String ) -> Bool{
    
        let fileName1 = String(name.hash)
        //let fileName2 = fileName1.substringToIndex(fileName1.endIndex.advancedBy(-6))
    
        //self.addUrlToFileNameMap(name, fileName: fileName1)
        let path = fileInDocumentsDirectory(fileName1)
        //TODO when profiling, this was found to be extremely heavy, so we should look at putting this in an async
        //let pngImageData = UIImagePNGRepresentation(image)
        let jpgImageData = UIImageJPEGRepresentation(image, 1.0)
    
        let result = jpgImageData!.writeToFile(path, atomically: true)
        print("if true, saved image: \(result)")
        return result
    }

    func loadImageFromPath(name: String?) -> UIImage? {
        //let fileName = self.getFileNameFromUrl(name)
        if name == nil{
            return nil
        }else{
            let fileName = String(name!.hash)
            let path = fileInDocumentsDirectory(fileName)
            let image = UIImage(contentsOfFile: path)
            
            if image == nil {
                print("missing image at: \(path)")
            } else {
                // this is just for you to see the path in case you want to go to the directory, using Finder.
                print("Loading image from path: \(path)")
            }
            return image
        }
    }

    //called just once in pointOfInterest.swift
    func downloadImageSet(urls: [String]){
        for url in urls{
            let actualURL = NSURL(string: url)
            
            getDataFromUrl(actualURL!) { (data, response, error)  in
                dispatch_async(dispatch_get_main_queue()) { () -> Void in
                    guard let data = data where error == nil else {
                        return
                    }
                    print(response?.suggestedFilename ?? "")
                    
                    let image = UIImage(data: data)
                    self.saveImage(image!, name: url)
                    //HOW TO GET HERE THE FINAL DOWNLOADED IMAGE
                }
            }
        }
    }

}
