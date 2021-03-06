var should = require('should');
var assert = require('assert');
var request = require('supertest');
var section = require('../routes/section.js');

//Section route testing module functions
//Includes all functions necessary to test the section   route
//Checks the POST, PUT, GET, and DELETE functions
//Uses hardcoded checks for data to be expected based on test input
//All functions are called in the route-test.js file

var sectionTest = {

    //POST function tests
    //creates and adds secction object to server with given values
    POST: function(pointerID, server, callback){
        //takes pointerID to link section object to given tour / supersection
        var section  = {
            "tour":  ""+pointerID,
            "superSection": "",
            "title": "The Test",
            "description": "This is the main section test",
            "depth": 0
        };
        //connects to the server
        request(server)
        //sends object to API
       .post('/api/v1/section/')
       .send(section)
        //checks to ensure the created object adheres to format requirements
        .end(function(err, res) {
              if (err) {
                throw err;
              }
              //statements checking the expected properties of the response
              //determines if the API is working as expected
              res.body.should.have.property("tour");
              res.body.should.have.property("depth")
              res.body.should.have.property("superSection");
              res.body.should.have.property("title");
              res.body.should.have.property("description");
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
        .get('/api/v1/section/'+pointerID)
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
            res.body.title.should.equal('The Test');
            res.body.description.should.equal("This is the main section test");
            res.body.tour.should.not.equal(null);
            res.body.depth.should.equal(0);
            //calls the callback function and returns with it the created object's ID
            callback();
        });
    },
    
    //GET ALL function tests
    //test whether the get all route includes the created section
    GET_ALL: function(pointerID, server, callback){
        //queries the server
        request(server)
        //queries server for the section
        .get('/api/v1/sections')
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //a Boolean checking if the section exists 
            var exists = false;
            
            //a loop to go through all the section returned
            for (var index in res.body) {
                //checks if the ID of the created section is found amongst the sections returned
                //sets the exists Boolean to true if found
                if (res.body[index].objectId === pointerID) {
                    exists = true;
                }
            }
            exists.should.equal(true);
            //check whether the section we created exists

            callback();
        });
    },


    //PUT function tests
    //updates the object
    PUT: function(tourID, pointerID, server, callback){
        //updates the object with new given values
       var section2  = {
        "tour":  {
          "__type": "Pointer",
          "className": "Tour",
          "objectId": ""+tourID
        },
        "superSection": {
          "__type": "Pointer",
          "className": "Section",
          "objectId": "null"
        },

        "title": "The Test2",
        "description": "This is the main section test2",
        "depth": 0

        };
        //connects to the server
        request(server)

        //prepares the update the object in the server using reference to the object's ID
        .put('/api/v1/section/'+pointerID)

        //sends request to server
        .send(section2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }

              //statements checking the expected properties of the response
              //determines if the API is working as expected
              res.body.should.have.property("tour");
              res.body.should.have.property("superSection");
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("depth");
              res.status.should.be.equal(200);
            //calls the callback function and returns with it the created object's ID
              callback();
          });
    },

    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, server, callback){
        //connects to the server
        request(server)
        //queries server with the given object ID
        .get('/api/v1/section/'+pointerID)
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //statements checking the expected properties of the response
            //determines if the API is working as expected
            res.body.title.should.equal('The Test2');
            res.body.description.should.equal("This is the main section test2");
            res.body.tour.should.not.equal(null);
            res.body.depth.should.equal(0);
            //calls the callback function and returns with it the created object's ID
            callback();
        });
    },



    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, server, callback){
        //connects to the server
        request(server)
        //sends delete request for the given object ID
        .delete('/api/v1/section/'+pointerID)
        //expected status code to be returned
        .expect(200) //Status code
        //function to be called at the end of the test
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //calls the callback function and returns with it the created object's ID
            callback();
        });
    },

    //GET function tests
    //third GET test to check objet no longer exists / object was correctly deleted
    GET3: function(pointerID, server, callback){
        //connects to the server
         request(server)
        //sends the get query for the given object ID
        .get('/api/v1/section/'+pointerID)
        //expected status code to be returned
        .expect(404 || 400) //Status code
        //function to be called at the end of the test
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //calls the callback function and returns with it the created object's ID
            callback();
        });
    }
}

//export this module
module.exports = sectionTest;
