var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var key = require('../routes/key.js');



var keyTest = {

    POST: function(pointerID, url, callback){
        var key  = {
            "code": "KCL-1000",
			"tour": ""+pointerID,
			"expiresAt": "20-03-2016"
        };
        request(url)
       .post('api/v1/key/')
       .send(key)

        .end(function(err, res) {
              if (err) {
                throw err;
              }
              res.body.should.have.property("code");
              res.body.should.have.property("tour");
              res.body.should.have.property("expiresAt");
              res.status.should.be.equal(201);
              objID = res.body.objectId;
              callback(objID);
        });
    },




    GET1: function(pointerID, url, callback){
        request(url)
        .get('api/v1/key/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.code.should.equal('KCL-1000');
            res.body.expiresAt.should.not.equal(null);
            callback();
        });
    },



    PUT: function(pointID, pointerID, url, callback){
       var key2  =  {
        "tour": {
          "__type": "Pointer",
          "className": "Tour",
          "objectId": ""+pointID
        },
        "code": "KCL-1001",
        "expiresAt": "21-03-2016",
      };
        request(url)
        .put('api/v1/key/'+pointerID)
        .send(key2)
        .end(function(err, res) {
              if (err) {
                throw err;
              }

              res.body.should.have.property("expiresAt");
              res.body.should.have.property("code");
              res.status.should.be.equal(200);
              callback();
          });
    },


    GET2: function(pointerID, url, callback){

        request(url)
        .get('api/v1/key/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            res.body.expiresAt.should.not.equal(null);
            res.body.code.should.equal("KCL-1001");
            res.body.tour.should.not.equal(null);
            callback();
        });
    },




    DELETE: function(pointerID, url, callback){

        request(url)
        .delete('api/v1/key/'+pointerID)
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
        .get('api/v1/key/'+pointerID)
        .expect(404 || 400) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            callback();
        });
    } 
}

module.exports =keyTest;
