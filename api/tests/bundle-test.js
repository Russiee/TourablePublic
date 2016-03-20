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
        .expect(200) //Status code
        .end(function(err,res) {
            if (err) {
                throw err;
            }
            var stringy = JSON.stringify(res.body);
            
            while(stringy.indexOf('"objectId"') > -1){
                var remove = stringy.indexOf('"objectId"');
                var removeTo = remove+24;
                stringy = stringy.replace((stringy.slice(remove, removeTo)), "")
                
                while(stringy.indexOf('"createdAt"')> -1){
                    var remove = stringy.indexOf('"createdAt"');
                    var removeTo = remove+39;
                    stringy = stringy.replace((stringy.slice(remove, removeTo)), "")
                    
                    while(stringy.indexOf('"updatedAt"') > -1){
                        var remove = stringy.indexOf('"updatedAt"');
                        var removeTo = remove+39;
                        stringy = stringy.replace((stringy.slice(remove, removeTo)), "")
                    }
                }
            }
            stringy.should.equal('{"description":"described","title":"TestTour2","estimatedTime":0,"version":0,"sections":[{"title":"The Test2","description":"This is the main section test2","depth":0,"pois":[{"post":[{"type":"Header","content":"Header text2"}],"title":"TestPOI2","description":"described2",]}]}');
            
            callback();
        });
    } 
}

module.exports = bundleTest;
