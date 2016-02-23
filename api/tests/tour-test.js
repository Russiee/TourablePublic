var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var poi = require('../routes/tour.js');

  describe('Tour tests', function() {
  var objID;
  var url = 'http://touring-api.herokuapp.com/'; 
  this.timeout(10000);

      
    it('should correctly add a tour ', function(done) {
      var tour  = {
            "admin": "vrOcgdMDvO",
            "sections": [ "LDLgQmmZBI", "ImdWEqFcQe"],
            "keys": [],
            "description": "This is a test tour",
            "title": "Ultimate Test Tour",
            "isPublic": "True"
      };
    request(url)
	.post('api/v1/tour/')
	.send(tour)
	
    .end(function(err, res) {
              if (err) {
                console.log("error");
                throw err;
              }
              res.body.should.have.property("title");
              res.body.should.have.property("description");
              res.body.should.have.property("admin");
              res.body.should.have.property("sections");
              res.body.should.have.property("keys");
              res.body.should.have.property("isPublic");
              res.status.should.be.equal(201);
              objID = res.body.objectId;
              done();
          });
    });
 
      
      
      
    it('should correctly get the added tour', function(done){
	request(url)
		.get('api/v1/tour/' + objID)
        .expect('Content-Type', /json/)
        .expect(200 || 304) //Status code
		.end(function(err,res) {
			if (err) {
				throw err;
			}
            res.body.title.should.equal('Ultimate Test Tour');
	        res.body.description.should.equal("This is a test tour");                    
	        res.body.isPublic.should.equal("True");
            res.body.sections.should.not.equal(null);
            res.body.admin.should.not.equal(null);
            res.body.keys.should.not.equal(null);
			done();
		});
	});
      
      
      
    it('should correctly update the added tour', function(done){
	var tour2 = {
			
            "admin": {
              "__type": "Pointer",
              "className": "Admin",
              "objectId": "vrOcgdMDvO"
            },
            "sections": [
              {
                "__type": "Pointer",
                "className": "Section",
                "objectId": "LDLgQmmZBI"
              },
              {
                "__type": "Pointer",
                "className": "Section",
                "objectId": "ImdWEqFcQe"
              }
            ],
            "keys": [],
            "description": "described",
            "title": "TestTour2",
            "isPublic": "True"
      };
	     request(url)
		.put('api/v1/tour/'+objID)
		.send(tour2)
		.end(function(err,res) {
			if (err) {
				throw err;
			}
	       res.body.should.have.property("title");
           res.body.should.have.property("description");
           res.body.should.have.property("admin");
           res.body.should.have.property("sections");
           res.body.should.have.property("keys");
           res.body.should.have.property("isPublic");
           res.status.should.be.equal(200);

			done();
		});
	});  
      
      
      
    it('should correctly get the updated Tour', function(done){

    request(url)
    .get('api/v1/tour/'+objID)
    .expect('Content-Type', /json/)
    .expect(200 || 304) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        res.body.title.should.equal('TestTour2');
        res.body.description.should.equal("described");                     
	    res.body.isPublic.should.equal("True");
        res.body.sections.should.not.equal(null);
        res.body.admin.should.not.equal(null);
        res.body.keys.should.not.equal(null);
        done();
    });
});




it('should correctly delete the added tour', function(done){

request(url)
    .delete('api/v1/tour/'+objID)
    .expect(200) //Status code
    .end(function(err,res) {
        if (err) {
            throw err;
        }
        done();
    });
});  

it('should get null/notfound for the deleted tour', function(done){
request(url)
    .get('api/v1/tour/'+objID)
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
