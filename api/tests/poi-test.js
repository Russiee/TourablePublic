//require the necessary modules for this file
var should = require('should');
var assert = require('assert');
var request = require('supertest');
var poi = require('../routes/poi.js');


//Point of Interest route testing module functions
//Includes all functions necessary to test the POI route
//Checks the POST, PUT, GET, and DELETE functions
//Uses hardcoded checks for data to be expected based on test input
//All functions are called in the route-test.js file
var poiTest = {

    //POST function tests
    //creates and adds poi object to API database with given values
    POST: function(pointerID, server, callback){
        //takes pointerID to link poi object to given section
            var poi  = {
            "title": "TestPOI",
            "description": "described",
            "post": [{"type": "Header",
                      "content": "Header text"
                     }],
            "section": ""+pointerID
            };
            //connects to the API database
            request(server)
            //sends post request with the object to the API
            .post('/api/v1/poi/')
            .send(poi)
            //checks to ensure the created object adheres to format requirements
            .end(function(err, res) {
              if (err) {
                throw err;
              }
              //statements checking the expected properties of the response
              //determines if the API is working as expected
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("post");
              res.body.should.have.property("section");
              res.status.should.be.equal(201);
              var storeID = res.body.objectId;
              //uses callback to ensure tests run synchronously (for the purpose of linking objects through pointers)

              //calls the callback function and returns with it the created object's ID
              callback(storeID);
            });

        },


    //GET function tests
    //first get function test to check object was added correctly

    GET1: function(pointerID, server, callback){
        //connects to the API database
        request(server)
        //sends the get query for the given object ID
        .get('/api/v1/poi/'+pointerID)
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
            res.body.title.should.equal('TestPOI');
            res.body.description.should.equal("described");
            res.body.post.should.deepEqual([{"type": "Header", "content": "Header text"}]);
            res.body.section.should.not.equal(null);
            //calls the callback function to finish the test
            callback();
        });
    },


    //PUT function tests
    //updates the object
    PUT: function(pointerID, originalID, server, callback){
            //updates the object with new given values
            var poi2  = {
            "title": "TestPOI2",
            "description": "described2",
            "post": [{"type": "Header",
                      "content": "Header text2"
                     }],
            "section": {
                "__type": "Pointer",
                "className": "Section",
                "objectId": ""+pointerID
            }
        };
        //connects to the API database
        request(server)
        //sends the update request for the given object ID
        .put('/api/v1/poi/'+originalID)
        .send(poi2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }
              //statements checking the expected properties of the response
              //determines if the API is working as expected
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("post");
              res.body.should.have.property("section");
              res.status.should.be.equal(200);
              //calls the callback function to finish the test
              callback();
          });
    },

    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, server, callback){
        //connects to the API database
        request(server)
        //sends the get query for the given object ID
        .get('/api/v1/poi/'+pointerID)
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
            res.body.title.should.equal('TestPOI2');
            res.body.description.should.equal("described2");
            res.body.post.should.deepEqual([{"type": "Header",
                                                      "content": "Header text2"
                                                     }]);
            res.body.section.should.not.equal(null);
            //calls the callback function to finish the test
            callback();
        });
    },



    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, server, callback){
        //connects to the API database
        request(server)
        //sends the delete request for the given object ID
        .delete('/api/v1/poi/'+pointerID)
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
        //connects to the API database
        request(server)
        //sends the get query for the given object ID
        .get('/api/v1/poi/'+pointerID)
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
module.exports = poiTest;
