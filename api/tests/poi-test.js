var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var poi = require('../routes/poi.js');


    var objID;

var poiTest = {
// this is referencing the Alex's Room Section  

    
    POST: function(pointerID, url, callback){
            var poi  = {
            "title": "TestPOI",
            "description": "described",
            "post": [{"type": "Header",
                      "content": "Header text"
                     }],		
            "section": ""+pointerID

            };
            
            request(url)
            .post('api/v1/poi/')
            .send(poi)
            
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
                
              callback(storeID);
            });
            
        },




    GET1: function(pointerID, url, callback){
        request(url)
        .get('api/v1/poi/'+pointerID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
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



    PUT: function(pointerID, originalID, url, callback){
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