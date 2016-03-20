var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var bundle = require('../routes/bundle.js');

//Admin route testing module functions
//Includes all functions necessary to test the admin route
//Checks the POST, PUT, GET, and DELETE functions
//Uses hardcoded checks for data to be expected based on test input
//All functions are called in the route-test.js file

var bundleTest = {
    
  
    //GET function tests
    //third GET test to check objet no longer exists / object was correctly deleted
    GET: function(pointerID, url, callback){
        request(url)
        .get('api/v1/bundle/'+pointerID)
        .expect(404 || 400) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
           // var stringy = JSON.stringify(res.body);
            /*
            while(stringy.indexOf < -1){
                var remove = stringy.indexOf('"objectId"')
                stringy.slice(remove, remove+24);
            }*/
          //  console.log(stringy);
            
            callback();
        });
    } 
}

module.exports = bundleTest;
