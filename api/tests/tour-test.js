var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var tour = require('../routes/tour.js');

//tour route testing module functions
//Includes all functions necessary to test the tour route
//Checks the POST, PUT, GET, and DELETE functions
//Uses hardcoded checks for data to be expected based on test input
//All functions are called in the route-test.js file
var tourTest = {
    POST: function(pointerID, url, callback) {
        //takes pointerID to link tour object to given admin
        var tour  = {
                "admin": ""+pointerID,
                "description": "This is a test tour",
                "title": "Ultimate Test Tour",
                "isPublic": true
        };
        request(url)
        //sends object to API
        .post('api/v1/tour/')
        .send(tour)
        //checks to ensure the created object adheres to format requirements
        .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("admin");
              res.body.should.have.property("isPublic");
              res.status.should.be.equal(201);
              objID = res.body.objectId;
            //uses callback to ensure tests run synchronously (for the purpose of linking objects through pointers)
              callback(objID);
          });
    },  
      
    //GET function tests
    //first get function test to check object was added correctly  
    GET1: function(pointerID, url, callback){
	    //queries the url with given objectID
        request(url)
		.get('api/v1/tour/' + pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
		.end(function(err,res) {
			if (err) {
				throw err;
			}
            res.body.title.should.equal('Ultimate Test Tour');
	        res.body.description.should.equal("This is a test tour");                    
	        res.body.isPublic.should.equal(true);
            res.body.admin.should.not.equal(null);
			callback();
		});
	},
      
      
    //PUT function tests
    //updates the object  
    PUT: function(pointerID, pointID, url, callback){
	    //updates the object with new given values
        var tour2 = {			
            "admin": {
              "__type": "Pointer",
              "className": "Admin",
              "objectId": ""+pointerID
            },
            "description": "described",
            "title": "TestTour2",
            "isPublic": true
        };
	     request(url)
		.put('api/v1/tour/'+pointID)
		.send(tour2)
         //ensures response is correct by checking against expected values
		.end(function(err,res) {
			if (err) {
				throw err;
			}
	       res.body.should.have.property("title");
           res.body.should.have.property("description");
           res.body.should.have.property("admin");
           res.body.should.have.property("isPublic");
           res.status.should.be.equal(200);
           callback();
		});
	},  
      
      
    //GET function tests
    //second GET test to check object values were correctly updated  
    GET2: function(pointerID, url, callback){
        request(url)
        .get('api/v1/tour/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.title.should.equal('TestTour2');
            res.body.description.should.equal("described");                     
            res.body.isPublic.should.equal(true);
            res.body.admin.should.not.equal(null);
            callback();
        });
    },

    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, url, callback){

        request(url)
        .delete('api/v1/tour/'+pointerID)
        .expect(200) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            callback();
        });
    }, 
    
    //GET function tests
    //third GET test to check objet no longer exists / object was correctly deleted
    GET3: function(pointerID, url, callback){
        request(url)
        .get('api/v1/tour/'+pointerID)
        .expect(404 || 400) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
           ;
            callback();
        });
    }
}

module.exports = tourTest;