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

        date = Date.now();

        var admin  = {
        "organization": ""+pointerID,
        "username": "Tester Name " + date,
        "email": "test" + date + "@email.com",
        "password": "hi",
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
            res.body.username.should.equal("Tester Name " + date);
            res.body.email.should.equal("test" + date + "@email.com");
            res.body.isSuper.should.equal(true);
            res.body.organization.should.not.equal(null);
            callback();
        });
    }
}

module.exports = adminTest;
