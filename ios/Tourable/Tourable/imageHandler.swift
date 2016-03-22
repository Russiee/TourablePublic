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
    static let sharedInstance = imageHandler()

  
    
    func triggerDownloadCompleteNotify() {
        NSNotificationCenter.defaultCenter().addObserver(
            self,
            selector: "NotifiedFinishedDownloading",
            name: "NotifiedFinishedDownloading",
            object: nil)
        func notify() {
            NSNotificationCenter.defaultCenter().postNotificationName(endDownloadKey, object: self)

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
        }
        notify()
    }
    func saveImage(image: UIImage, name: String ) -> Bool{
    
        let fileName1 = String(name.hash)
        //let fileName2 = fileName1.substringToIndex(fileName1.endIndex.advancedBy(-6))
    
        //self.addUrlToFileNameMap(name, fileName: fileName1)
        let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileName1, fileType: ".jpg")
        //TODO when profiling, this was found to be extremely heavy, so we should look at putting this in an async
        //let pngImageData = UIImagePNGRepresentation(image)
        let jpgImageData = UIImageJPEGRepresentation(image, 1.0)
    
        let result = jpgImageData!.writeToFile(path, atomically: true)
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
            let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileName,fileType: ".jpg")
            let image = UIImage(contentsOfFile: path)
            
            return image
        }
    }
    func queueImage(urls: [String]){
        imageQueue.appendContentsOf(urls)
    }

    //called just once in pointOfInterest.swift
    func downloadMediaSet(urls: [String]){
       
        countOfImages = countOfImages + urls.count

        for url in urls {
            if url == " "{
           // triggerDownloadBeginNotify()
                sleep(1)
           // triggerDownloadCompleteNotify()
            }else{
            triggerDownloadBeginNotify()
            if(url.characters.last == "g"){
            
            let imageUrl = url
            let actualURL = NSURL(string: imageUrl)
        
            MediaHelper.sharedInstance.getDataFromUrl(actualURL!) { (data, response, error)  in
                dispatch_async(dispatch_get_main_queue()) { () -> Void in
                    guard let data = data where error == nil else {
                        return
                    }
                    
                    let image = UIImage(data: data)
                    self.saveImage(image!, name: imageUrl)
                }
                }
            }else{
                videoHandler.sharedInstance.downloadVideo(url)
            }
        }
        }
        
        imageQueue.removeAll()
        
    }
    
    
}
