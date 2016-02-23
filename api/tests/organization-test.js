var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var poi = require('../routes/organization.js');



describe('Organization tests', function() {
var objID;
var url = 'http://touring-api.herokuapp.com/';
this.timeout(10000);

it('should correctly add an organization', function(done) {
    var organizaton  = {
        "superAdmins": [],
        "admins": ["vrOcgdMDvO", "lYJAzTBADI"],
        "key": "KCL-9999",
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
          res.body.should.have.property("superAdmins");
          res.body.should.have.property("admins");
          res.body.should.have.property("key");
          res.body.should.have.property("name");
          res.body.should.have.property("color");
          res.body.should.have.property("logo");
          
          res.status.should.be.equal(201);
          objID = res.body.objectId;
          done();
        });
    });




it('should correctly get the added organization', function(done){
request(url)
    .get('api/v1/organization/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.logo.should.equal("Lion");
        res.body.color.should.equal("Blue");
        res.body.name.should.equal("King's College London Tester");
        res.body.admins.should.not.equal(null);
        done();
    });
});



it('should correctly update the existing organization ', function(done) {
   var organization2  = {
    
    "superAdmins": [],
    "admins": [
      {
        "__type": "Pointer",
        "className": "_User",
        "objectId": "vrOcgdMDvO"
      },
      {
        "__type": "Pointer",
        "className": "_User",
        "objectId": "lYJAzTBADI"
      }
    ],
    "key": "KCL-8190",
    "name": "King's College London Tester2",
    "color": "Gold2",
    "logo": "Lion2"
    };
    request(url)
    .put('api/v1/organization/'+objID)
    .send(organization2)
    .end(function(err, res) {
          if (err) {
            throw err;
          }
          res.body.should.have.property("superAdmins");
          res.body.should.have.property("admins");
          res.body.should.have.property("key");
          res.body.should.have.property("name");
          res.body.should.have.property("color");
          res.body.should.have.property("logo");
          res.status.should.be.equal(200);
          done();
      });
});


it('should correctly get the updated organization', function(done){

request(url)
    .get('api/v1/organization/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.logo.should.equal("Lion2");
        res.body.color.should.equal("Gold2");
        res.body.name.should.equal("King's College London Tester2");
        res.body.admins.should.not.equal(null);
        done();
    });
});




it('should correctly delete the added organization', function(done){

request(url)
    .delete('api/v1/organization/'+objID)
    .expect(200) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        done();
    });
});  

it('should get null/notfound for the deleted organization', function(done){
request(url)
    .get('api/v1/organization/'+objID)
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
