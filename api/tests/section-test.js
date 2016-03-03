var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var section = require('../routes/section.js');



var sectionTest = {
    
    POST: function(pointerID, url, callback){
        var section  = {
            "tour":  ""+pointerID,
            "superSection": "",
            "title": "The Test",
            "description": "This is the main section test",
            "depth": 0

        };
        request(url)
       .post('api/v1/section/')
       .send(section)

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
              callback(objID);
        });
    },




    GET1: function(pointerID, url, callback){
        request(url)
        .get('api/v1/section/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
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



    PUT: function(tourID, pointerID, url, callback){
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
