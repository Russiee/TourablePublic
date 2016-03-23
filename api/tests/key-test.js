//require the necessary modules for this file
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
    POST: function(pointerID, server, callback){
        //creates a test key with code KCL-1000
        //also takes a pointerID to link to test key to test tour
        var key  = {
            "code": "KCL-1000",
            "tour": ""+pointerID,
            "expiry": "20-03-2018"
        };
        //connects to the server
        request(server)
        //sends object to server
       .post('/api/v1/key/')
       .send(key)

        .end(function(err, res) {
              if (err) {
                throw err;
              }
            //checks that the object to be added to the server follows the required format
              res.body.should.have.property("code");
              res.body.should.have.property("tour");
              res.body.should.have.property("expiry");
              res.status.should.be.equal(201);
              objID = res.body.objectId;
            //uses callback to ensure tests run synchronously (for the purpose of linking objects through pointers)
              callback(objID);
        });
    },



    //GET function tests
    //first GET test to check object was added properly
    GET1: function(pointerID, server, callback){
        //connects to the server database
        request(server)
        .get('/api/v1/key/'+pointerID)
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.code.should.equal('KCL-1000');
            res.body.expiry.should.not.equal(null);
            //calls the callback function to finish the test
            callback();
        });
    },
    
    //GET ALL function tests
    //test whether the get all route includes the created key
    GET_ALL: function(pointerID, server, callback){
        //queries the server
        request(server)
        //queries database for the keys
        .get('/api/v1/keys')
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //a Boolean checking if the key exists 
            var exists = false;
            
            //a loop to go through all the keys returned
            for (var index in res.body) {
                //checks if the ID of the created key is found amongst the keys returned
                //sets the exists Boolean to true if found
                if (res.body[index].objectId === pointerID) {
                    exists = true;
                }
            }
            exists.should.equal(true);
            //check whether the key we created exists

            callback();
        });
    },

    //PUT function tests
    //updates the object
    PUT: function(pointID, pointerID, server, callback){
        //updates the object with new values
       var key2  =  {
        "tour": {
          "__type": "Pointer",
          "className": "Tour",
          "objectId": ""+pointID
        },
        "code": "KCL-1001",
        "expiry": "21-03-2016",
      };
        //connects to the server database
        request(server)
        .put('/api/v1/key/'+pointerID)
        .send(key2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }
              //statements checking the expected properties of the response
              //determines if the server is working as expected
              res.body.should.have.property("expiry");
              res.body.should.have.property("code");
              res.status.should.be.equal(200);
              //calls the callback function to finish the test
              callback();
          });
    },

    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, server, callback){
        //connects to the server database
        request(server)
        .get('/api/v1/key/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.expiry.should.not.equal(null);
            res.body.code.should.equal("KCL-1001");
            res.body.tour.should.not.equal(null);
            //calls the callback function to finish the test
            callback();
        });
    },



    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, server, callback){
        //connects to the server database
        request(server)
        //sends delete request for the given object ID
        .delete('/api/v1/key/'+pointerID)
        //expected status code to be returned
        .expect(200) //Status code
        //function to be called at the end of the test
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //calls the callback function to finish the test
            callback();
        });
    },

    //GET function tests
    //third GET test to check objet no longer exists / object was correctly deleted
    GET3: function(pointerID, server, callback){
        //connects to the server database
        request(server)
        //sends the get query for the given object ID
        .get('/api/v1/key/'+pointerID)
        //expected status code to be returned
        .expect(404 || 400) //Status code
        //function to be called at the end of the test
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //calls the callback function to finish the test
            callback();
        });
    }
}

//export this module
module.exports =keyTest;
