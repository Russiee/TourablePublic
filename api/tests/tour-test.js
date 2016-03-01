var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var tour = require('../routes/tour.js');


var tourTest = {
    POST: function(pointerID, url, callback) {
        var tour  = {
                "admin": ""+pointerID,
                "description": "This is a test tour",
                "title": "Ultimate Test Tour",
                "isPublic": true
        };
        request(url)
        .post('api/v1/tour/')
        .send(tour)

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
              callback(objID);
          });
    },  
      
      
    GET1: function(pointerID, url, callback){
	    request(url)
		.get('api/v1/tour/' + pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
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
      
      
      
    PUT: function(pointerID, pointID, url, callback){
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