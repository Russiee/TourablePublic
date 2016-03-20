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
    POST: function(pointerID, url, callback){
        //takes pointerID to link poi object to given section
            var poi  = {
            "title": "TestPOI",
            "description": "described",
            "post": [{"type": "Header",
                      "content": "Header text"
                     }],		
            "section": ""+pointerI
            };            
            request(url)
            //sends object to API
            .post('api/v1/poi/')
            .send(poi)
            //checks to ensure the created object adheres to format requirements
            .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("post");
              res.body.should.have.property("section");
              res.status.should.be.equal(201);
              var storeID = res.body.objectId;
              //uses callback to ensure tests run synchronously (for the purpose of linking objects through pointers) 
              callback(storeID);
            });
            
        },


    //GET function tests
    //first get function test to check object was added correctly

    GET1: function(pointerID, url, callback){
        //queries the url with given objectID
        request(url)
        .get('api/v1/poi/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.title.should.equal('TestPOI');
            res.body.description.should.equal("described");                    
            res.body.post.should.deepEqual([{"type": "Header", "content": "Header text"}]);
            res.body.section.should.not.equal(null);
            callback();
        });
    },


    //PUT function tests
    //updates the object
    PUT: function(pointerID, originalID, url, callback){
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
        request(url)
        .put('api/v1/poi/'+originalID)
        .send(poi2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("post");
              res.body.should.have.property("section");
              res.status.should.be.equal(200);
              callback();
          });
    },

    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, url, callback){

        request(url)
        .get('api/v1/poi/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.title.should.equal('TestPOI2');
            res.body.description.should.equal("described2");                    
            res.body.post.should.deepEqual([{"type": "Header",
                                                      "content": "Header text2"
                                                     }]);
            res.body.section.should.not.equal(null);
            callback();
        });
    },



    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, url, callback){

        request(url)
        .delete('api/v1/poi/'+pointerID)
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
        .get('api/v1/poi/'+pointerID)
        .expect(404 || 400) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            callback();
        }); 
    }  
}




module.exports = poiTest;