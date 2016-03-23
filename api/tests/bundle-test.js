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
    GET: function(pointerID, server, callback){

        //connects to the API database
        request(server)

        //sends bundle get query to the database
        .get('/api/v1/bundle/'+pointerID)

        //the test expects 200 status code to be returned
        .expect(200) //Status code

        //function called when all other parts of the test are done
        .end(function(err,res) {
            if (err) {
                throw err;
            }

            //converts the bundle response to a String
            var stringy = JSON.stringify(res.body);


            //Loops to remove objectId, createdAT and updatedAt properties and values, as they change for each test
            while(stringy.indexOf('"objectId"') > -1){

                //finds the index of the next objectId
                var remove = stringy.indexOf('"objectId"');

                //finds the index of where the objectId property and value ends
                var removeTo = remove+24;

                //replaces the substring containing objectId property and value with a blank string (deletion)
                stringy = stringy.replace((stringy.slice(remove, removeTo)), "")

                while(stringy.indexOf('"createdAt"')> -1){
                    var remove = stringy.indexOf('"createdAt"');

                    //finds the index of where the createdAt property and value ends
                    var removeTo = remove+39;

                    //replaces the substring containing createdAt property and value with a blank string (deletion)
                    stringy = stringy.replace((stringy.slice(remove, removeTo)), "")

                    while(stringy.indexOf('"updatedAt"') > -1){
                        var remove = stringy.indexOf('"updatedAt"');

                        //finds the index of where the updatedAt property and value ends
                        var removeTo = remove+39;

                        //replaces the substring containing updatedAt property and value with a blank string (deletion)
                        stringy = stringy.replace((stringy.slice(remove, removeTo)), "")
                    }
                }
            }
            //checks that the value returned for the value is equal to what is the expected correct value.
            stringy.should.equal('{"description":"described","title":"TestTour2","estimatedTime":0,"version":0,"sections":[{"title":"The Test2","description":"This is the main section test2","depth":0,"pois":[{"post":[{"type":"Header","content":"Header text2"}],"title":"TestPOI2","description":"described2",]}]}');

            //calls the callback to end the test
            callback();
        });
    }
}


//exports this module
module.exports = bundleTest;
