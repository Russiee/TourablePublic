var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var poi = require('../routes/section.js');



describe('Section tests', function() {
var objID;
var url = 'http://touring-api.herokuapp.com/';
this.timeout(10000);

it('should correctly add a section', function(done) {
    var section  = {
        "tour":  "DnPRFaSYEk",
        "superSection": "",
        "subsections": ["eIEi7f2ZsK", "ajh63HuHlj", "cLQGaPIScY"],
        "pois": ["0oNASH84yT"],
        "title": "The Test",
        "description": "This is the main section test"

    };
    request(url)
   .post('api/v1/section/')
   .send(section)

    .end(function(err, res) {
          if (err) {
            throw err;
          }
          res.body.should.have.property("tour");
          res.body.should.have.property("superSection");
          res.body.should.have.property("subsections");
          res.body.should.have.property("pois");
          res.body.should.have.property("title");
          res.body.should.have.property("description");
          res.status.should.be.equal(201);
          objID = res.body.objectId;
          done();
        });
    });




it('should correctly get the added section', function(done){
request(url)
    .get('api/v1/section/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.title.should.equal('The Test');
        res.body.description.should.equal("This is the main section test");                    
        res.body.tour.should.not.equal(null);
        done();
    });
});



it('should correctly update the existing section ', function(done) {
   var section2  = {
    "tour":  {
      "__type": "Pointer",
      "className": "Tour",
      "objectId": "DnPRFaSYEk"
    },
    "superSection": {
      "__type": "Pointer",
      "className": "Section",
      "objectId": "null"
    },
    "subsections": [{
        "__type": "Pointer",
        "className": "Section",
        "objectId": "eIEi7f2ZsK"
      },
      {
        "__type": "Pointer",
        "className": "Section",
        "objectId": "ajh63HuHlj"
      },
      {
        "__type": "Pointer",
        "className": "Section",
        "objectId": "cLQGaPIScY"
      }],
    "pois": [{
        "__type": "Pointer",
        "className": "POI",
        "objectId": "0oNASH84yT"
      }],
    "title": "The Test2",
    "description": "This is the main section test2",

    };
    request(url)
    .put('api/v1/section/'+objID)
    .send(section2)
    .end(function(err, res) {
          if (err) {
            throw err;
          }

          res.body.should.have.property("tour");
          res.body.should.have.property("superSection");
          res.body.should.have.property("subsections");
          res.body.should.have.property("pois");
          res.body.should.have.property("title");
          res.body.should.have.property("description");
          res.status.should.be.equal(200);
          done();
      });
});


it('should correctly get the updated Section', function(done){

request(url)
    .get('api/v1/section/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.title.should.equal('The Test2');
        res.body.description.should.equal("This is the main section test2");                    
        res.body.tour.should.not.equal(null);
        done();
    });
});




it('should correctly delete the added Section', function(done){

request(url)
    .delete('api/v1/section/'+objID)
    .expect(200) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        done();
    });
});  

it('should get null/notfound for the deleted section', function(done){
request(url)
    .get('api/v1/section/'+objID)
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
