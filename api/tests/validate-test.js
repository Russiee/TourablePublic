var should = require('should');
var request = require('supertest');
var assert = require('assert');
var validate = require('../routes/validate.js');
                       
describe("Tests for validate", function(){
this.timeout(5000);
    
   it("Return false for input data", function(done){
      
       //this test should have validate return false because of incorrect property types of subsections & pois
      var data = {
            "title": "Title",
			"description": "Description",
			"tour": "xIdxx",
			"superSection": "moreIdxx",
			"subsections": "moreIdsxx",
			"pois": "moreIds"  
          
          
      };

      var expectedInput = {
			"title": "",
			"description": "",
			"tour": "",
			"superSection": "",
			"subsections": [],
			"pois": []
      };

      var validInput = validate.validateInput(data, expectedInput);
      validInput.should.equal(false);


     done();
       
   });         
    
    
  it("Return false for input data", function(done){
      
      var data = {
            "SomeString": "Correct type",
			"SomeArray": [12, 123, 1234],
			"SomeInteger": 9210.12,
			"SomeArray2": [],
			"SomeInteger": 12,
			"SomeString": ["Incorrect type"], //should be string not array
            "SomeBoolean": 1010 //should be boolean not int
          
          
      }; //

      var expectedInput = {
            "SomeString": "",
			"SomeArray": ["values"],
			"SomeInteger": 1,
			"SomeArray2": [],
			"SomeInteger": 2,
			"SomeString": "Incorrect type",
            "SomeBoolean": true

      };

      var validInput = validate.validateInput(data, expectedInput);
    
      validInput.should.equal(false);
     done();

    });         
    
    
    
  it("Return true for input data", function(done){
      
      var data = {
            "SomeString": "Correct type",
			"SomeArray": [12, 123, 1234, true, "value"],
			"SomeInteger": 9210.12,
			"SomeArray2": [],
			"SomeInteger": 12,
			"SomeString": ["Incorrect type", 12],
            "SomeBoolean": false
          
      }; //

      var expectedInput = {
            "SomeString": "",
			"SomeArray": [],
			"SomeInteger": 1,
			"SomeArray2": [],
			"SomeInteger": 2,
			"SomeString": ["Incorrect type", 12],
            "SomeBoolean": true

      };

      var validInput = validate.validateInput(data, expectedInput);
      validInput.should.equal(true);
    
    done();
    });    

  it("Return true for input data", function(done){
      
      var data = {
            "SomeString": "Correct type",
			"SomeArray": [12, 123, 1234],
			"SomeInteger": 9210.12,
			"SomeArray2": [],
			"SomeInteger": 12,
			"SomeString": ["Incorrect type"],
            "SomeBoolean": true
          
          
      }; //

      var expectedInput = {
            "SomeString": "",
			"SomeArray": ["values"],
			"SomeInteger": 1,
			"SomeArray2": [],
			"SomeInteger": 2,
			"SomeString": ["Incorrect type"],
            "SomeBoolean": false
      };

      var validInput = validate.validateInput(data, expectedInput);
    
      validInput.should.equal(true);

     done();
    });         
});