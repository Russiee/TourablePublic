//
//  pointOfInterest.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 19/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//

import Foundation

class pointOfInterest {
    var createdAt: String
    var description: String
    var objectId: String
    var post: NSArray
    var section: NSDictionary
    var title: String
    var updatedAt: String
    
    init(objectId: String, description: String, createdAt: String, post: NSArray, section: NSDictionary, title: String, updatedAt: String){
        self.objectId = objectId
        self.description = description
        self.createdAt = createdAt
        self.post = post
        self.section = section
        self.title = title
        self.updatedAt = updatedAt
        
    }
    
    
    
}
