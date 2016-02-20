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
    
    init(objectId: String, description: String, createdAt: String){
        self.objectId = objectId
        self.description = description
        self.createdAt = createdAt
    }
    
    var post: NSDictionary{
        get{
            return self.post
        }
        set{
            self.post = newValue
        }
    }
    
    var section: NSDictionary{
        get{
            return self.section
        }
        set{
            self.section = newValue
        }
    }
}
