//
//  ApiConnector.swift
//  Alan Touring
//
//  Created by Alex Gubbay on 12/02/2016.
//  Copyright © 2016 Hobbyte. All rights reserved.
//
//
import Foundation

class ApiConnector{
    
    init(){
        
    }
    
    
    func requestComplete(){
        print("result")
        
    }
    
func newConnection(){
    
 let url = NSURL(string: "https://touring-api.herokuapp.com/api/v1/key/verify/RBH-the-key")
  
    let task = NSURLSession.sharedSession().dataTaskWithURL(url!) {(data, response, error) in
       print(NSString(data: data!, encoding: NSUTF8StringEncoding)!



    )}
   
   task.resume()
    
         }
 
    
}