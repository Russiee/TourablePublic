//require the necessary modules for this file
var should = require('should');
var assert = require('assert');
var request = require('supertest');
var organization = require('../routes/organization.js');

//organization route testing module functions
//Includes all functions necessary to test the organization route
//Checks the POST, PUT, GET, and DELETE functions
//Uses hardcoded checks for data to be expected based on test input
//All functions are called in the route-test.js file
var organizationTest = {

    //POST function tests
    //creates and adds organization object to server with given values
    POST: function(server, callback) {
        var organization  = {
            "key": "KCL",
            "name": "King's College London Tester",
            "color": "Blue",
            "logo": "Lion"
        };
        //connects to the server
        request(server)
        //sends object to API
       .post('/api/v1/organization')
       .send(organization)
        //checks to ensure the created object adheres to format requirements
       .end(function(err, res) {
              if (err) {
                throw err;
              }
              //statements checking the expected properties of the response
              //determines if the API is working as expected
              res.body.should.have.property("key");
              res.body.should.have.property("name");
              res.body.should.have.property("color");
              res.body.should.have.property("logo");

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
        //queries the server with given objectID
        request(server)
        //queries server with the given object ID
        .get('/api/v1/organization/'+pointerID)
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
            res.body.logo.should.equal("Lion");
            res.body.key.should.equal("KCL");
            res.body.color.should.equal("Blue");
            res.body.name.should.equal("King's College London Tester");
            //calls the callback function to finish the test
            callback();
        });
    },

    //GET ALL function tests
    //test whether the get all route includes the created organization
    GET_ALL: function(pointerID, server, callback){
        //queries the server
        request(server)
        //queries server with the given object ID
        .get('/api/v1/organizations')
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }

            var exists = false;
            for (var index in res.body) {
                if (res.body[index].objectId === pointerID) {
                    exists = true;
                }
            }
            exists.should.equal(true);
            //check whether the organization we created exists

            callback();
        });
    },


    //PUT function tests
    //updates the object
    PUT: function(pointerID, server, callback) {
        //updates the object with new given values
        var organization2  = {
        "key": "KCL2",
        "name": "King's College London Tester2",
        "color": "Gold2",
        "logo": "Lion2"
        };

        //connects to the server
        request(server)

        //prepares the update the object in the server using reference to the object's ID
        .put('/api/v1/organization/'+pointerID)

        //sends request to server
        .send(organization2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }
            //statements checking the expected properties of the response
            //determines if the API is working as expected
              res.body.should.have.property("key");
              res.body.should.have.property("name");
              res.body.should.have.property("color");
              res.body.should.have.property("logo");
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
        .get('/api/v1/organization/'+pointerID)
        //expected status codes and content type to be returned
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            //statements checking the expected properties of the response
            //determines if the API is working as expected
            res.body.logo.should.equal("Lion2");
            res.body.color.should.equal("Gold2");
            res.body.key.should.equal("KCL2");
            res.body.name.should.equal("King's College London Tester2");
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
        .delete('/api/v1/organization/'+pointerID)
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
        .get('/api/v1/organization/'+pointerID)
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
module.exports = organizationTest;
