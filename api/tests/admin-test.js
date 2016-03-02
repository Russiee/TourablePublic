var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var admin = require('../routes/admin.js');



var adminTest = {

    POST: function(pointerID, url, callback){
        var admin  = {
        "organization": ""+pointerID,
        "username": "Tester Name",
        "email": "test mail",
        "isSuper": true
        };
        request(url)
       .post('api/v1/admin/')
       .send(admin)

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
              callback(objID);
        });
    },




    GET1: function(pointerID, url, callback){
        request(url)
        .get('api/v1/admin/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
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



    PUT: function(pointID, pointerID, url, callback){
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
