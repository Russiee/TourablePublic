//
//  pointOfInterest.swift
//  Tourable
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class pointOfInterest {
    //DateTime item was created at
    var createdAt: String!
    //Point of interest description
    var description: String!
    //Unique objectId
    var objectId: String!
    //Actual point of interest contents
    var post: NSArray!
    //the section the point of interest belongs to.
    var section: NSDictionary!
    //Title of the point of interest
    var title: String!
    //Last time update was made to the poi
    var updatedAt: String!
    
    init(objectId: String, description: String, createdAt: String, post: NSArray, section: NSDictionary, title: String, updatedAt: String) {
        self.objectId = objectId
        self.description = description
        self.createdAt = createdAt
        self.post = post
        self.section = section
        self.title = title
        self.updatedAt = updatedAt
    }
    
    init(){
        
    }
    
    
    func downloadContent() {
        var urlsToDownload = [String]()
        
        for item in post {
            if let url: String? = item["url"] as? String{
                if  url != nil{
                    urlsToDownload.append(url!)
                }
            }
        }
        imageHandler.sharedInstance.queueImage(urlsToDownload)
        
        
    }

}
