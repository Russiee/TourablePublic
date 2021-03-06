//require the necessary modules for this file
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
    POST: function(pointerID, server, callback) {
        //takes pointerID to link tour object to given admin
        var tour  = {
                "admin": ""+pointerID,
                "description": "This is a test tour",
                "title": "Ultimate Test Tour",
                "isPublic": true,
                "estimatedTime": 10,
                "version": 10
        };
        //connects to the server
        request(server)
        //sends object to API
        .post('/api/v1/tour/')
        .send(tour)
        //checks to ensure the created object adheres to format requirements
        .end(function(err, res) {
              if (err) {
                throw err;
              }

              //statements checking the expected properties of the response
              //determines if the API is working as expected
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("admin");
              res.body.should.have.property("isPublic");
              res.body.should.have.property("version");
              res.body.should.have.property("estimatedTime");
              res.status.should.be.equal(201);
              objID = res.body.objectId;
            //uses callback to ensure tests run synchronously (for the purpose of linking objects through pointers)

            //calls the callback function and returns with it the created object's ID
              callback(objID);
          });
    },

    //GET function tests
    //first get function test to check object was added correctly
    GET1: function(pointerID, server, callback){
        //connects to the server
        request(server)
        //queries server with the given object ID
        .get('/api/v1/tour/' + pointerID)
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //statements checking the expected properties of the response
            //determines if the API is working as expected
            res.body.title.should.equal('Ultimate Test Tour');
            res.body.description.should.equal("This is a test tour");
            res.body.isPublic.should.equal(true);
            res.body.admin.should.not.equal(null);
            res.body.estimatedTime.should.equal(10);
            res.body.version.should.equal(10);
            //calls the callback function to finish the test
            callback();
        });
    },
    
    //GET ALL function tests
    //test whether the get all route includes the created tour
    GET_ALL: function(pointerID, server, callback){
        //queries the server
        request(server)
        //queries server for the tour
        .get('/api/v1/tours')
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //a Boolean checking if the tour exists 
            var exists = false;
            
            //a loop to go through all the tour returned
            for (var index in res.body) {
                //checks if the ID of the created tour is found amongst the keys returned
                //sets the exists Boolean to true if found
                if (res.body[index].objectId === pointerID) {
                    exists = true;
                }
            }
            exists.should.equal(true);
            //check whether the tour we created exists

            callback();
        });
    },


    //PUT function tests
    //updates the object
    PUT: function(pointerID, pointID, server, callback){
        //updates the object with new given values
        var tour2 = {
            "admin": {
              "__type": "Pointer",
              "className": "Admin",
              "objectId": ""+pointerID
            },
            "description": "described",
            "title": "TestTour2",
            "isPublic": true,
            "estimatedTime": 0,
            "version": 0
        };
        //connects to the server
         request(server)

        //prepares the update the object in the server using reference to the object's ID
        .put('/api/v1/tour/'+pointID)
        //sends request to server
        .send(tour2)
        //function to be called at the end of the test
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //statements checking the expected properties of the response
            //determines if the API is working as expected
           res.body.should.have.property("title");
           res.body.should.have.property("description");
           res.body.should.have.property("admin");
           res.body.should.have.property("isPublic");
           res.body.should.have.property("version");
           res.body.should.have.property("estimatedTime");
           res.status.should.be.equal(200);
            //calls the callback function to finish the test
           callback();
        });
    },


    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, server, callback){
        //connects to the server
        request(server)
        //queries server with the given object ID
        .get('/api/v1/tour/'+pointerID)
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //function to be called at the end of the test
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //statements checking the expected properties of the response
            //determines if the API is working as expected
            res.body.title.should.equal('TestTour2');
            res.body.description.should.equal("described");
            res.body.isPublic.should.equal(true);
            res.body.admin.should.not.equal(null);
            res.body.estimatedTime.should.equal(0);
            res.body.version.should.equal(0);
            //calls the callback function to finish the test
            callback();
        });
    },

    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, server, callback){
        //connects to the server
        request(server)
        //sends delete request for the given object ID
        .delete('/api/v1/tour/'+pointerID)
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
        //connects to the server
        request(server)
        //sends the get query for the given object ID
        .get('/api/v1/tour/'+pointerID)
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
module.exports = tourTest;
