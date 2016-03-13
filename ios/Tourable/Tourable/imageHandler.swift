//  imageHandler.swift
//  Tourable
//
//  Created by Alex Gubbay on 20/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//  with help from: http://stackoverflow.com/questions/24231680/loading-image-from-url

import Foundation
import UIKit

let beginDownloadKey = "downloadInProgress"
let endDownloadKey = "DownloadComplete"
var countOfImages = 0;


class imageHandler: NSObject {
    lazy var imageQueue = [String]()
    
    //making the TourIdParser a singleton to parse all tours from the API
    //in order to access TourIdParser methods call TourIdParser.shardInstance.METHOD()
    class var sharedInstance: imageHandler {
        struct Static {
            static var onceToken: dispatch_once_t = 0
            static var instance: imageHandler? = nil
        }
        dispatch_once(&Static.onceToken) {
            Static.instance = imageHandler()
            
        }
        return Static.instance!
    }

  
    
    func triggerDownloadCompleteNotify() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "NotifiedFinishedDownloading",
            name: "NotifiedFinishedDownloading",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(endDownloadKey, object: self)
            print("image download complete notify called")
        }
        notify()
    }
    func triggerDownloadBeginNotify() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "NotifiedDownloading:",
            name: "NotifiedDownloading",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(beginDownloadKey, object: self)
            print("image download notify called")
        }
        notify()
    }
    func saveImage(image: UIImage, name: String ) -> Bool{
    
        let fileName1 = String(name.hash)
        //let fileName2 = fileName1.substringToIndex(fileName1.endIndex.advancedBy(-6))
    
        //self.addUrlToFileNameMap(name, fileName: fileName1)
        let path = mediaHelper.sharedInstance.fileInDocumentsDirectory(fileName1, fileType: ".jpg")
        //TODO when profiling, this was found to be extremely heavy, so we should look at putting this in an async
        //let pngImageData = UIImagePNGRepresentation(image)
        let jpgImageData = UIImageJPEGRepresentation(image, 1.0)
    
        let result = jpgImageData!.writeToFile(path, atomically: true)
        //print("if true, saved image: \(result)")
        countOfImages--
        triggerDownloadCompleteNotify()
        return result
    }

    func loadImageFromPath(name: String?) -> UIImage? {
        //let fileName = self.getFileNameFromUrl(name)
        if name == nil{
            return nil
        }else{
            let fileName = String(name!.hash)
            let path = mediaHelper.sharedInstance.fileInDocumentsDirectory(fileName,fileType: ".jpg")
            var image = UIImage(contentsOfFile: path)
            
            if image == nil {
                print("missing image at: \(path)")
                if let url  = NSURL(string: name!),
                    data = NSData(contentsOfURL: url)
                {
                    image = UIImage(data: data)
                }
            } else {
                // this is just for you to see the path in case you want to go to the directory, using Finder.
                print("Loading image from path: \(path)")
            }
            return image
        }
    }
    func queueImage(urls: [String]){
        imageQueue.appendContentsOf(urls)
    }
    func savePlaceholders(urls: [String]){
        for url in urls {
            print("save placeholder of create logic for online viewing here for url \(url)")
            print("****DO NOT DELETE THIS MESSAGE UNTIL IMPLEMENTED****")
        }
    }
    
    //called just once in pointOfInterest.swift
    func downloadMediaSet(urls: [String]){
       
        countOfImages = countOfImages + urls.count

        for url in urls {
            
            if(url.characters.last == "g"){
            triggerDownloadBeginNotify()
            let imageUrl = url
            let actualURL = NSURL(string: imageUrl )
        
            mediaHelper.sharedInstance.getDataFromUrl(actualURL!) { (data, response, error)  in
                dispatch_async(dispatch_get_main_queue()) { () -> Void in
                    guard let data = data where error == nil else {
                        return
                    }
                    //print(response?.suggestedFilename ?? "")
                    
                    let image = UIImage(data: data)
                    self.saveImage(image!, name: imageUrl)
                    //HOW TO GET HERE THE FINAL DOWNLOADED IMAGE
                }
                }
            }else{
                videoHandler.sharedInstance.downloadVideo(url)
            }
        }
        
        imageQueue.removeAll()
        
    }
    
    
}
