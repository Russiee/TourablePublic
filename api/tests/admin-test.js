var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var admin = require('../routes/admin.js');

//Admin route testing module functions
//Includes all functions necessary to test the admin route
//Checks the POST, PUT, GET, and DELETE functions
//Uses hardcoded checks for data to be expected based on test input
//All functions are called in the route-test.js file

var adminTest = {
    
    //POST function tests
    //creates and adds admin object to API database with given values
    POST: function(pointerID, url, callback){
        //takes pointerID to link admin object to given organization
        var admin  = {
        "organization": ""+pointerID,
        "username": "Tester Name",
        "email": "test mail",
        "isSuper": true
        };
        //sends object to API
        request(url)
       .post('api/v1/admin/')
       .send(admin)
        //checks to ensure the created object adheres to format requirements
       .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("organization");
              res.body.should.have.property("username");
              res.body.should.have.property("email");
              res.body.should.have.property("isSuper");
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
        .get('api/v1/admin/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        //expected response, test fails if response is not the expected value
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.username.should.equal('Tester Name');
            res.body.email.should.equal("test mail");                    
            res.body.isSuper.should.equal(true);
            res.body.organization.should.not.equal(null);
            callback();
        });
    },

    //PUT function tests
    //updates the object 
    PUT: function(pointID, pointerID, url, callback){
        //updates the object with new given values
       var admin2  =  {
        "organization": {
          "__type": "Pointer",
          "className": "Organization",
          "objectId": ""+pointID
        },
        "username": "New Tester Name",
        "email": "tester mail",
        "isSuper": true,
      };
        request(url)
        .put('api/v1/admin/'+pointerID)
        .send(admin2)
        //ensures response is correct by checking against expected values
        .end(function(err, res) {
              if (err) {
                throw err;
              }

              res.body.should.have.property("organization");
              res.body.should.have.property("username");
              res.body.should.have.property("email");
              res.body.should.have.property("isSuper");
              res.status.should.be.equal(200);
              callback();
          });
    },

    //GET function tests
    //second GET test to check object values were correctly updated
    GET2: function(pointerID, url, callback){

        request(url)
        .get('api/v1/admin/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.username.should.equal('New Tester Name');
            res.body.email.should.equal("tester mail");                    
            res.body.isSuper.should.not.equal(null);
            res.body.organization.should.not.equal(null);
            callback();
        });
    },



    //DELETE function tests
    //Deletes the test object
    DELETE: function(pointerID, url, callback){

        request(url)
        .delete('api/v1/admin/'+pointerID)
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
        .get('api/v1/admin/'+pointerID)
        .expect(404 || 400) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            callback();
        });
    } 
}

module.exports = adminTest;
