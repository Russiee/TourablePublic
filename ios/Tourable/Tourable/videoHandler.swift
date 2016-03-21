//
//  videoHandler.swift
//  Tourable
//
//  Created by Alex Gubbay on 04/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation
import MediaPlayer

//Deals with the download and saving of video mp4 files for playback in tours.
class videoHandler {
    //This is a singleton class.
     static let sharedInstance = videoHandler()
    
    //Downloads and triggers the save of the video with the specified url (https only)
    func downloadVideo(url: String){
        //creates an NSUrl from the string given.
        let actualURL = NSURL(string: url)
        MediaHelper.sharedInstance.getDataFromUrl(actualURL!) { (data, response, error)  in
            dispatch_async(dispatch_get_main_queue()) { () -> Void in
                guard let data = data where error == nil else {
                    return
                }
                //Save the data from the server as a video, with the url as its name.
                self.saveVideo(data, name: url)
            }
        }
    }
    
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
    
    //Saves the video binary file under the specified name.
    func saveVideo(videoData: NSData, name: String ) -> Bool{
        //The name is hashed to ensure it is uniqe and compatible with iOS filesystem
        let fileHash = String(name.hash)
        //get the full path to save the file to.
        let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileHash, fileType: ".mp4")
        //write the file to disk at the specified path.
        let result = videoData.writeToFile(path, atomically: true)
        //returns success status.
        triggerDownloadCompleteNotify()
        return result
    }
    
    //returns the file location of the video if it exists. If not will url of the web resource for 
    //online streaming.
    func loadVideoPath(url: String?) -> NSURL? {

        if url == nil{
            return nil
        }else{
            let fileName = String(url!.hash)
            let path = MediaHelper.sharedInstance.fileInDocumentsDirectory(fileName,fileType: ".mp4")
            //check if the file has been downloaded and saved.
            if MediaHelper.sharedInstance.checkFileExists(path){
                //yes, return the filepath
                return NSURL(fileURLWithPath: path)
            }else{
                //no, return the online url
                return NSURL(string: url!)
            }
            
        }
    }
    
    
    
    
}