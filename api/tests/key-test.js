var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var key = require('../routes/key.js');


//Key route testing module functions
//Includes all functions necessary to test the key route
//Checks the POST, PUT, GET, and DELETE functions
//Uses hardcoded checks for data to be expected based on test input
//All functions are called in the route-test.js file

var keyTest = {
    
    //POST function test
    POST: function(pointerID, url, callback){
        //creates a test key with code KCL-1000
        //also takes a pointerID to link to test key to test tour
        var key  = {
            "code": "KCL-1000",
			"tour": ""+pointerID,
			"expiresAt": "20-03-2018"
        };
        request(url)
        //sends object to API
       .post('api/v1/key/')
       .send(key)
        
        //checks that 
        .end(function(err, res) {
              if (err) {
                throw err;
              }
            //checks that the object to be added to the API follows the required format
              res.body.should.have.property("code");
              res.body.should.have.property("tour");
              res.body.should.have.property("expiresAt");
              res.status.should.be.equal(201);
              objID = res.body.objectId;
            //uses callback to ensure tests run synchronously (for the purpose of linking objects through pointers)
              callback(objID);
        });
    },



    //GET function tests
    //first GET test to check object was added properly
    GET1: function(pointerID, url, callback){
        //queries the url with the given objectID
        request(url)
        .get('api/v1/key/'+pointerID)
        
        //expected response, test fails if response is not the expected value
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.code.should.equal('KCL-1000');
            res.body.expiresAt.should.not.equal(null);
            callback();
        });
    },


    //PUT function tests
    //updates the object
    PUT: function(pointID, pointerID, url, callback){
        //updates the object with new values
       var key2  =  {
        "tour": {
          "__type": "Pointer",
          "className": "Tour",
          "objectId": ""+pointID
        },
        "code": "KCL-1001",
        "expiresAt": "21-03-2016",
      };
        //updates the object with given objectID
        request(url)
        .put('api/v1/key/'+pointerID)
        .send(key2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }

              res.body.should.have.property("expiresAt");
              res.body.should.have.property("code");
              res.status.should.be.equal(200);
              callback();
          });
    },

    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, url, callback){

        request(url)
        .get('api/v1/key/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.expiresAt.should.not.equal(null);
            res.body.code.should.equal("KCL-1001");
            res.body.tour.should.not.equal(null);
            callback();
        });
    },



    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, url, callback){

        request(url)
        .delete('api/v1/key/'+pointerID)
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
        .get('api/v1/key/'+pointerID)
        .expect(404 || 400) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            callback();
        });
    } 
}

module.exports =keyTest;
