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
    //creates and adds organization object to API database with given values
    POST: function(url, callback) {
        var organizaton  = {
            "key": "KCL",
            "name": "King's College London Tester",
            "color": "Blue",
            "logo": "Lion"
        };
        //connects to the API database
        request(url)
        //sends object to API
       .post('api/v1/organization/')
       .send(organizaton)
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
    GET1: function(pointerID, url, callback){
        //queries the url with given objectID
        request(url)
        //queries database with the given object ID
        .get('api/v1/organization/'+pointerID)
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


    //PUT function tests
    //updates the object 
    PUT: function(pointerID, url, callback) {
        //updates the object with new given values
        var organization2  = {
        "key": "KCL2",
        "name": "King's College London Tester2",
        "color": "Gold2",
        "logo": "Lion2"
        };
        
        //connects to the API database
        request(url)
        
        //prepares the update the object in the database using reference to the object's ID
        .put('api/v1/organization/'+pointerID)
        
        //sends request to database
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
    GET2: function(pointerID, url, callback){
        //connects to the API database
        request(url)
        //queries database with the given object ID
        .get('api/v1/organization/'+pointerID)
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
    DELETE: function(pointerID, url, callback){
        
        //connects to the API database
        request(url)
        //sends delete request for the given object ID
        .delete('api/v1/organization/'+pointerID)
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
    GET3: function(pointerID, url, callback){
        //connects to the API database
        request(url)
        //sends the get query for the given object ID
        .get('api/v1/organization/'+pointerID)
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
