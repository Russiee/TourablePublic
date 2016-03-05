//
//  videoHandler.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 04/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation
import MediaPlayer

class videoHandler {
    
    class var sharedInstance: videoHandler {
        struct Static {
            static var onceToken: dispatch_once_t = 0
            static var instance: videoHandler? = nil
        }
        dispatch_once(&Static.onceToken) {
            Static.instance = videoHandler()
            
        }
        return Static.instance!
    }


func downloadVideo(url: String){
    let actualURL = NSURL(string: url)
    print("starting video dl from \(actualURL)")
    mediaHelper.sharedInstance.getDataFromUrl(actualURL!) { (data, response, error)  in
        dispatch_async(dispatch_get_main_queue()) { () -> Void in
            guard let data = data where error == nil else {
                return
            }
            print("finished video dl")
            self.saveVideo(data, name: url)
            //HOW TO GET HERE THE FINAL DOWNLOADED IMAGE
        }
    }
}
    
    func saveVideo(videoData: NSData, name: String ) -> Bool{
        
        let fileName1 = String(name.hash)
        
        var fileSize : UInt64 = 0
        let path = mediaHelper.sharedInstance.fileInDocumentsDirectory(fileName1, fileType: ".mp4")
        print("\(path) url at point of save")
        do {
            let attr : NSDictionary? = try NSFileManager.defaultManager().attributesOfItemAtPath(path)
            
            if let _attr = attr {
                fileSize = _attr.fileSize();
                print(fileSize)
            }
        } catch {
            print("Error: \(error)")
        }
        //let fileName2 = fileName1.substringToIndex(fileName1.endIndex.advancedBy(-6))
        
        //self.addUrlToFileNameMap(name, fileName: fileName1)
                //TODO when profiling, this was found to be extremely heavy, so we should look at putting this in an async
        //let pngImageData = UIImagePNGRepresentation(image)
       
        let result = videoData.writeToFile(path, atomically: true)
        //print("if true, saved image: \(result)")
        print("save of video worked! \(result)")
        return result
    }
    
    
    func loadVideoPath(name: String?) -> String? {
        //let fileName = self.getFileNameFromUrl(name)
        if name == nil{
            return nil
        }else{
            let fileName = String(name!.hash)
            let path = mediaHelper.sharedInstance.fileInDocumentsDirectory(fileName,fileType: ".mp4")
            return path
        }
    }
    
   
        

}