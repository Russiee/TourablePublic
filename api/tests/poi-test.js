var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var poi = require('../routes/poi.js');



describe('POI tests', function() {
var objID;
var url = 'http://touring-api.herokuapp.com/';

// this is referencing the Alex's Room Section  
it('should correctly add a POI ', function(done) {
  var poi  = {
        "title": "TestPOI",
        "description": "described",
        "post": [{"type": "header"}],		
        "section": "eIEi7f2ZsK"

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
          objID = res.body.objectId;
          done();
        });
    });




it('should correctly get the added POI', function(done){
request(url)
    .get('api/v1/poi/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.title.should.equal('TestPOI');
        res.body.description.should.equal("described");                    
        res.body.post.should.deepEqual([{type: "header"}]);
        res.body.section.should.not.equal(null);
        done();
    });
});



it('should correctly update the existing POI ', function(done) {
  var poi2  = {
        "title": "TestPOI2",
        "description": "described2",
        "post": [{"type": "header2"}],		
        "section": {
            "__type": "Pointer",
            "className": "Section",
            "objectId": "eIEi7f2ZsK"
        }
    };
    request(url)
    .put('api/v1/poi/'+objID)
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
          done();
      });
});


it('should correctly get the updated POI', function(done){

request(url)
    .get('api/v1/poi/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.title.should.equal('TestPOI2');
        res.body.description.should.equal("described2");                    
        res.body.post.should.deepEqual([{type: "header2"}]);
        res.body.section.should.not.equal(null);
        done();
    });
});




it('should correctly delete the added POI', function(done){

request(url)
    .delete('api/v1/poi/'+objID)
    .expect(200) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        done();
    });
});  

it('should get null/notfound for the deleted poi', function(done){
request(url)
    .get('api/v1/poi/'+objID)
    .expect(404 || 400) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
       ;
        done();
    });
});  
});
