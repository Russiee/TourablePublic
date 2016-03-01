var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var organization = require('../routes/organization.js');


var organizationTest = {
    
    POST: function(url, callback) {
        var organizaton  = {
            "key": "KCL",
            "name": "King's College London Tester",
            "color": "Blue",
            "logo": "Lion"
        };
        request(url)
       .post('api/v1/organization/')
       .send(organizaton)

        .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("key");
              res.body.should.have.property("name");
              res.body.should.have.property("color");
              res.body.should.have.property("logo");

              res.status.should.be.equal(201);
              objID = res.body.objectId;
              callback(objID);
            });
        },




    GET1: function(pointerID, url, callback){
        request(url)
        .get('api/v1/organization/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.logo.should.equal("Lion");
            res.body.key.should.equal("KCL");
            res.body.color.should.equal("Blue");
            res.body.name.should.equal("King's College London Tester");
            callback();
        });
    },



    PUT: function(pointerID, url, callback) {
       var organization2  = {

        "key": "KCL2",
        "name": "King's College London Tester2",
        "color": "Gold2",
        "logo": "Lion2"
        };
        request(url)
        .put('api/v1/organization/'+pointerID)
        .send(organization2)
        .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("key");
              res.body.should.have.property("name");
              res.body.should.have.property("color");
              res.body.should.have.property("logo");
              res.status.should.be.equal(200);
              callback();
          });
    },


    GET2: function(pointerID, url, callback){

        request(url)
        .get('api/v1/organization/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.logo.should.equal("Lion2");
            res.body.color.should.equal("Gold2");
            res.body.key.should.equal("KCL2");
            res.body.name.should.equal("King's College London Tester2");
            callback();
        });
    },




    DELETE: function(pointerID, url, callback){

        request(url)
        .delete('api/v1/organization/'+pointerID)
        .expect(200) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            callback();
        });
    },  

    GET3: function(pointerID, url, callback){
        request(url)
        .get('api/v1/organization/'+pointerID)
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

module.exports = organizationTest;
