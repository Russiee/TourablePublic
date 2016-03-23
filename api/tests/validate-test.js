//require the necessary modules for this file
var should = require('should');
var request = require('supertest');
var assert = require('assert');
var validate = require('../routes/validate.js');

//Validate module testing
//Includes a variety of scenarios to test validate module
//Ensures that validate correctly identifies input as being correctly or incorrectly formatted
//Function is called in terminal (when in project directory) by typing 'gulp validate tests'
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
       
      //calls the validate module's validateInput function
      var validInput = validate.validateInput(data, expectedInput);
       
      //test to check the value returned by the validateInput function and the expecte output
      validInput.should.equal(false);
    
     //informs the test suite the test is complete
      done();      
   });         
    

  //this test should return false, as the input format fails to match the expected input format
  it("Return false for input data", function(done){
      var data = {
            "SomeString": "Correct type",
			"SomeArray": [12, 123, 1234],
			"SomeInteger": 9210.12,
			"SomeArray2": [],
			"SomeInteger": 12,
			"SomeString": ["Incorrect type"], //should be string not array
            "SomeBoolean": 1010 //should be boolean not int
      }; 
      var expectedInput = {
            "SomeString": "",
			"SomeArray": ["values"],
			"SomeInteger": 1,
			"SomeArray2": [],
			"SomeInteger": 2,
			"SomeString": "Incorrect type",
            "SomeBoolean": true
      };
      
      //calls the validate module's validateInput function
      var validInput = validate.validateInput(data, expectedInput);
      
      //test to check the value returned by the validateInput function and the expecte output
      validInput.should.equal(false);
      
      //informs the test suite the test is complete
      done();
    });         
    
    
   //this test should return true, as the input format successfully matches the expected input format 
  it("Return true for input data", function(done){
      var data = {
            "SomeString": "Correct type",
			"SomeArray": [12, 123, 1234, true, "value"],
			"SomeInteger": 9210.12,
			"SomeArray2": [],
			"SomeInteger": 12,
			"SomeString": ["Incorrect type", 12],
            "SomeBoolean": false
      }; 
      var expectedInput = {
            "SomeString": "",
			"SomeArray": [],
			"SomeInteger": 1,
			"SomeArray2": [],
			"SomeInteger": 2,
			"SomeString": ["Incorrect type", 12],
            "SomeBoolean": true
      };
      
      //calls the validate module's validateInput function
      var validInput = validate.validateInput(data, expectedInput);
      
      //test to check the value returned by the validateInput function and the expecte output
      validInput.should.equal(true);  
      
      //informs the test suite the test is complete
      done();
   });    

    //this test should return true, as the input format successfully matches the expected input format 
  it("Return true for input data", function(done){      
      var data = {
            "SomeString": "Correct type",
			"SomeArray": [12, 123, 1234],
			"SomeInteger": 9210.12,
			"SomeArray2": [],
			"SomeInteger": 12,
			"SomeString": ["Incorrect type"],
            "SomeBoolean": true          
      }; 
      var expectedInput = {
            "SomeString": "",
			"SomeArray": ["values"],
			"SomeInteger": 1,
			"SomeArray2": [],
			"SomeInteger": 2,
			"SomeString": ["Incorrect type"],
            "SomeBoolean": false
      };
      
      //calls the validate module's validateInput function
      var validInput = validate.validateInput(data, expectedInput);
      
      //test to check the value returned by the validateInput function and the expecte output
      validInput.should.equal(true);
      
      //informs the test suite the test is complete
      done();
    });         
});