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
    //creates and adds admin object to API database with given values
    POST: function(pointerID, url, callback){
        //takes pointerID to link admin object to given organization
        var section  = {
            "tour":  ""+pointerID,
            "superSection": "",
            "title": "The Test",
            "description": "This is the main section test",
            "depth": 0
        };
        request(url)
        //sends object to API
       .post('api/v1/section/')
       .send(section)
        //checks to ensure the created object adheres to format requirements
        .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("tour");
              res.body.should.have.property("depth")
              res.body.should.have.property("superSection");
              res.body.should.have.property("title");
              res.body.should.have.property("description");
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
        .get('api/v1/section/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.title.should.equal('The Test');
            res.body.description.should.equal("This is the main section test");                    
            res.body.tour.should.not.equal(null);
            res.body.depth.should.equal(0);
            callback();
        });
    },


    //PUT function tests
    //updates the object
    PUT: function(tourID, pointerID, url, callback){
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
        request(url)
        .put('api/v1/section/'+pointerID)
        .send(section2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }

              res.body.should.have.property("tour");
              res.body.should.have.property("superSection");
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("depth");
              res.status.should.be.equal(200);
              callback();
          });
    },

    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, url, callback){
        request(url)
        .get('api/v1/section/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.title.should.equal('The Test2');
            res.body.description.should.equal("This is the main section test2");                    
            res.body.tour.should.not.equal(null);
            res.body.depth.should.equal(0);
            callback();
        });
    },



    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, url, callback){
        request(url)
        .delete('api/v1/section/'+pointerID)
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
        .get('api/v1/section/'+pointerID)
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

module.exports = sectionTest;
