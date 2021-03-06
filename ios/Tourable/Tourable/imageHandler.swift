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


///ImageHandler deals with the downloading and saving images from various POIs for offline use. Also triggers the download and saving of videos. Norifiers in thie class are used by the UI to give download status information to the user.
class imageHandler: NSObject {
    lazy var imageQueue = [String]()
    
    static let sharedInstance = imageHandler()
    
    //Notifies observers that an image from the queue has been successfully downloaded.
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
    
    //Notifies observers that in image from the queue is currently being downloaded
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
    
    ///Stores an image for offline viewing, using the hash function of its name as the file name. This is to avoid invalid save names causing the save to fail. Returns a boolean that indicates the success of the save operation.
    func saveImage(image: UIImage, name: String ) -> Bool{
        
        let fileName1 = String(name.hash)
        let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileName1, fileType: ".jpg")
        let jpgImageData = UIImageJPEGRepresentation(image, 1.0)
        
        let result = jpgImageData!.writeToFile(path, atomically: true)
        //At this point the image has been downloaded so notify observers that this is the case.
        countOfImages--
        triggerDownloadCompleteNotify()
        return result
    }
    
    //Takes a url of an image as a paramenter and returns the UIImage at that location. Can return nil
    func loadImageFromPath(name: String?) -> UIImage? {
        //let fileName = self.getFileNameFromUrl(name)
        if name == nil{
            return nil
        }else{
            //get the save name of the image.
            let fileName = String(name!.hash)
            //get the full file path of the image to return.
            let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileName,fileType: ".jpg")
            let image = UIImage(contentsOfFile: path)
            
            return image
        }
    }
    
    //Adds an image to the queue of images to be downloaded asyncronusly.
    func queueImage(urls: [String]){
        imageQueue.appendContentsOf(urls)
    }
    
    ///Called just done in pointOfInterest.swift, this is the method that goes online and downloads the images. It triggers video downloads too if there are. It will notify observers that the image handler has started downloading an image by calling triggerDonwloadBegin notify
    func downloadMediaSet(urls: [String]){
        
        countOfImages = countOfImages + urls.count
        
        for url in urls {
            if url == " "{
                // triggerDownloadBeginNotify()
                sleep(1)
                // triggerDownloadCompleteNotify()
            }else{
                triggerDownloadBeginNotify()
                //Must be a PNG or JPG as these are the only ones the CMS will allow upload of.
                if(url.characters.last == "g"){
                    
                    let imageUrl = url
                    //Create an NSURL object from the url string.
                    let actualURL = NSURL(string: imageUrl)
                    //uses the media helper method with an overriden callback to download image and then trigger save.
                    MediaHelper.sharedInstance.getDataFromUrl(actualURL!) { (data, response, error)  in
                        dispatch_async(dispatch_get_main_queue()) { () -> Void in
                            guard let data = data where error == nil else {
                                return
                            }
                            //creates an image object from the data and passes it out to be saved.
                            let image = UIImage(data: data)
                            self.saveImage(image!, name: imageUrl)
                        }
                    }
                }else{
                    //MP4 found, trigger the video download.
                    videoHandler.sharedInstance.downloadVideo(url)
                }
            }
        }
        //once complete empty the queue.
        imageQueue.removeAll()
        
    }
    
    
}
