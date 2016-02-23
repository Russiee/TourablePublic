var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var admin = require('../routes/admin.js');



describe('Admin tests', function() {
var objID;
var url = 'http://touring-api.herokuapp.com/';

it('should correctly add a admin', function(done) {
    var admin  = {
    "organization": "n3okIRt0k3",
    "tours": [],
    "username": "Tester Name",
    "email": "test mail",
    "isSuper": "True"
  };
    request(url)
   .post('api/v1/admin/')
   .send(admin)

    .end(function(err, res) {
          if (err) {
            throw err;
          }
          res.body.should.have.property("organization");
          res.body.should.have.property("tours");
          res.body.should.have.property("username");
          res.body.should.have.property("email");
          res.body.should.have.property("isSuper");
          res.status.should.be.equal(201);
          objID = res.body.objectId;
          done();
    });
});




it('should correctly get the added admin', function(done){
request(url)
    .get('api/v1/admin/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.username.should.equal('Tester Name');
        res.body.email.should.equal("test mail");                    
        res.body.isSuper.should.not.equal(null);
        res.body.organization.should.not.equal(null);
        res.body.tours.should.not.equal(null);
        done();
    });
});



it('should correctly update the existing admin ', function(done) {
   var admin2  =  {
    "organization": {
      "__type": "Pointer",
      "className": "Organization",
      "objectId": "n3okIRt0k3"
    },
    "tours": [],
    "username": "New Tester Name",
    "email": "tester mail",
    "isSuper": "True",
  };
    request(url)
    .put('api/v1/admin/'+objID)
    .send(admin2)
    .end(function(err, res) {
          if (err) {
            throw err;
          }

          res.body.should.have.property("organization");
          res.body.should.have.property("tours");
          res.body.should.have.property("username");
          res.body.should.have.property("email");
          res.body.should.have.property("isSuper");
          res.status.should.be.equal(200);
          done();
      });
});


it('should correctly get the updated admin', function(done){

request(url)
    .get('api/v1/admin/'+objID)
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
        res.body.tours.should.not.equal(null);
        done();
    });
});




it('should correctly delete the added admin', function(done){

request(url)
    .delete('api/v1/admin/'+objID)
    .expect(200) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        done();
    });
});  

it('should get null/notfound for the deleted admin', function(done){
request(url)
    .get('api/v1/admin/'+objID)
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
