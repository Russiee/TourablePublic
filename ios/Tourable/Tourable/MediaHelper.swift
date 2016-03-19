//
//  fileManager.swift
//  Tourable
//
//  Created by Alex Gubbay on 04/03/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

//A helper class for the download and management of media binary files.
class MediaHelper {
    
    static let sharedInstance = MediaHelper()
    
    //Downloads data from the internet, and takes a custom completion handler to deal with it.
    func getDataFromUrl(url:NSURL, completion: ((data: NSData?, response: NSURLResponse?, error: NSError? ) -> Void)) {
        NSURLSession.sharedSession().dataTaskWithURL(url) { (data, response, error) in
            completion(data: data, response: response, error: error)
            }.resume()
    }
    
    //gets the formal url for the folder containing the media binaries
    func getDocumentsURL() -> NSURL {
        let documentsURL = NSFileManager.defaultManager().URLsForDirectory(.DocumentDirectory, inDomains: .UserDomainMask)[0]
        return documentsURL
    }
    
    //Gets the full file path of a file given its name and file type as strings.
    func fileInDocumentsDirectory(filename: String, fileType: String) -> String {
        let fileURL = getDocumentsURL().URLByAppendingPathComponent(filename+fileType)
        return fileURL.path!
    }
    
    //check if the file exists at the specified path
    func checkFileExists(path: String) -> Bool{
        if (NSFileManager.defaultManager().fileExistsAtPath(path)){
            //file exists
            return true
        }
        else{
            //file does not exist
            return false
        }
    }
    

}