//require the necessary files for this module, and initialize Parse
var config = require('../config.js');
var validate = require('./validate.js');
var http = require('http');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';

//instantiate key and tour object prototypes
var Key = Parse.Object.extend("Key");
var Tour = Parse.Object.extend("Tour");

//key module object
//contains functions for all REST api routes for the key object
var key = {
    
    //GET route function
    //returns a single key object
    //required param(s) in req(uest): id of the key object to be fetched
    //optional param(s) in req: none
    GET: function(req, res) {
        //server log for debugging
        console.log("GET KEY");
        
        //isolates id from request params
        var id = req.params.id;
        
        //instantiates a new query to Parse (database mount) for the Key prototype
        var query = new Parse.Query(Key);
        
        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(key) {
                //server log for debugging
                console.log("Key " + id + " retrieved succesfully");
                //send an https response with status code 200 and the key data in JSON format
                res.status(200).send(key);
            },
            //error callback, executed if an error occurs
            error: function(object, error) {
                //server log for debugging
                console.log("Error retrieving " + id);
                //send an https response with status code 404
                //in the future, it would be better to analyze the error code to provide more semantic error codes
                res.send(404);
            }
        });
    },
    
    //GET_ALL route function
    //returns as many key objects as requested
    //required param(s) in req: none
    //optional param(s) in req: "limit" - number of key to be fetched, otherwise set to 20,
    //                          "orderBy" - 'descending' or 'ascending' order
    GET_ALL: function(req, res) {
        //server log for debugging
        console.log("GET ALL KEYS");
        
        //isolates limit and orderBy from request params

        //default limit to 20 if no limit is passed
        var limit = req.query.limit || 20;
        //TO DO: FIX IT
        //default orderBy to null if no value is passed
        var orderBy = req.query.limit || null;

        //instantiates a new query to Parse (database mount) for the Key prototype
        var query = new Parse.Query(Key);
        
        //set the limit to the appropriate value in the query
        query.limit(parseInt(limit));
        
        //execute 'find' query and pass success and error callbacks as parameters
        query.find({
            //success callback, executed if the query is successful
            success: function(results) {
                //server logs for debugging
                console.log(results.length + " keys retrieved");
                //send an https response with status code 200 and the queried data in JSON format
                res.status(200).send(results);
            },
            //error callback, executed if an error occurs
            error: function(error) {
                //server logs for debugging
                console.log("Failed to retrieve keys");
                console.log(error);
                //send an https response with status code 500
                res.send(500);
            }
        });
    },

    //POST route function
    //creates a new key object
    //required param(s) in req: none
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
    POST: function(req, res) {
        //server logs for debugging
        console.log("POST KEY:\n", req.body);
        
        //isolates relevant data from request
        var data = req.body;
        
        //expected input is the expected format and data of the request, in type application/json
        //it does not represent actual data values, however it checks the value types (eg. is the data value a String, Boolean?)
        var expectedInput = {
            "code": "", //expected input is a String
            "tour": "",
            "expiry": ""
        };
        
        //this runs the validate module function to check the request input format against the expected input format
        //checking that each data value is of the correct type for the database
        //returns false if the request input format is not correct
        var validInput = validate.validateInput(data, expectedInput);
        //runs through the request data and removes any unexpected properties
        //this cleans the data and ensures it adheres to the correct format
        var parseData = validate.parseData(data, expectedInput);
        
        //server logs for debugging
        console.log("Parsed Data: ", parseData);

        //instantiates a new query to Parse (database mount) for the Key prototype
        var query = new Parse.Query(Key);
        //query checks if any keys in database exist with same code as new entry
        query.equalTo("code", req.body.code);
        
        //responses if key with same code is found
        query.find({
            //if a key is found with the same code (Key's code must be unique) and result is not empty, send back an https response with code 400 (bad request) 
            success: function(results) {
                if (results.length !== 0)
                    res.status(400).send({error: "Keys must be unique"});
                //if no key with the same code is found, check if the request input format is correct
                else {
                    //if request format is incorrect, return https response 400 (bad request)
                    if (!validInput) {
                        res.sendStatus(400);
                    }
                    //the key code is unique and request is formatted correctly, call the createKey function (separate logic function) and pass it a callback function
                    else {
                        createKey(parseData, function(result) {
                            //checks the status code sent back, if there is no server error (status 500)
                            //then send an https response with status code 201 (Created) as well as the data in JSON format.
                            if (result.status !== 500) {
                                res.status(201).send(result);
                            }
                            //else send an https response with the error status code and data returned by the createKey function
                            else
                                res.status(result.status).send(result.data);
                        });
                    }
                }
            }
        });
    },
    
    //PUT route function
    //updates an key object
    //required param(s) in req: id of the key object to be updated
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
    PUT: function(req, res) {
        //server log for debuggin
        console.log("PUT KEY:\n", req.body);
        
        //isolates relevant data from request
        var data = req.body;
        //isolates id from request params
        var id = req.params.id;
        
        //check expected input with validate functions, see POST route for more detailed documentation
        var expectedInput = {
            "code": "",
            "tour": "",
            "expiry": ""
        };

        var validInput = validate.validateInput(data, expectedInput);
        var parseData = validate.parseData(data, expectedInput);
        
        //server log for debuggin
        console.log("Parsed Data: ", parseData);
        
        //instantiates a new query to Parse (database mount) for the Key prototype
        var query = new Parse.Query(Key);
        
        //first GET the key object to be updated
        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(key) {
                //server log for debuggin
                console.log("Key " + id + " retrieved succesfully");
                
                //iterate over the properties in the parsed data
                for (var prop in parseData) {
                    //override the current data in the key object with our parsed data
                    key.set(prop.toString(), parseData[prop]);
                }
                
                //execute the save function on the object on the database with Parse, passing success and error callbacks
                key.save(null, {
                    //success callback, executed if the save is successful
                    success: function(key) {
                        //server log for debuggin
                        console.log("Key " + id + " updated succesfully");
                        //send an https response with status code 200 and the updated key data in JSON format
                        res.status(200).send(key);
                    },
                    //error callback, executed if an error occurs during the sav
                    error:  function(key, error) {
                        //server log for debuggin
                        console.log("Failed to update key " + id);
                        console.log(error);
                        //send an https response with status code 500, as well as the error data in JSON format
                        res.status(500).send(error);
                    }
                });

            },
            //error callback, executed if an error occurs
            error: function(object, error) {
                //server log for debuggin
                console.log("Error retrieving " + id);
                console.log(error);
                //send an https response with status code 500
                res.sendStatus(404);
            }
        });
    },

    //DELETE route function
    //deletes an key object
    //required param(s) in req: id of the key object to be deleted
    //optional param(s) in req: none
    DELETE: function(req, res) {
        //server log for debugging
        console.log("DELETE KEY");
        
        //isolates id from request params
        var id = req.params.id;
        
        //instantiates a new query to Parse (database mount) for the Key prototype
        var query = new Parse.Query(Key);
        
        //first GET the key object to be deleted
        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(key) {
                //server log for debugging
                console.log("Key " + id + " retrieved succesfully");
                
                //execute the destroy function on the object on the database with Parse, passing success and error callbacks
                key.destroy({
                    //success callback, executed if the destroy is successful
                    success: function(key) {
                        //server log for debugging
                        console.log("Deleted key " + id);
                        //send an https response with status code 200 to confirm the deletion
                        res.sendStatus(200);
                    },
                    //error callback, executed if an occurs during deletion (delete not successful)
                    error: function(error) {
                        //server log for debugging
                        console.log("Failed to delete " + id);
                        console.log(error);
                        //send an https response with status code 500
                        res.sendStatus(500);
                    }
                });
            },
            //error callback, executed if an error occurs
            error: function(object, error) {
                //server log for debugging
                console.log("Error retrieving " + id);
                console.log(error);
                //send an https response with status code 500, as well as the error data in JSON format
                res.sendStatus(404);
            }
        });

    },

    //VERIFY route function
    //verifies a key code
    //required param(s) in req: key code of the key objet to be verified
    //optional param(s) in req: none
    verify: function(req, res) {
        //server log for debugging
        console.log("VERIFYING KEY");
        
        //isolates  key code from request params
        var code = req.params.code;

        //instantiates a new query to Parse (database mount) for the Key prototype
        var query = new Parse.Query(Key
                                    
        //sets database query param to find a key with a code equal to the code passed by user
        query.equalTo("code", code);
        
        //execute 'find' query and pass success and error callbacks as parameters
        query.find({
            //success callback, executed if the query is successful
            success: function(results) {
                //server log for debugging
                console.log(results.length + " keys retrieved");
                //check if one result is returned
                //if true, send back an https response with status code 200, as well as the key data in JSON format
                if (results.length === 1)
                    res.status(200).send(results[0]);
                //check if result array is empty
                //if true, send back an https response with status code 404 (bad request)
                else if (results.length === 0)
                    res.sendStatus(404);
                //send an https response with status code 500
                else
                    res.sendStatus(500);
            },
            //error callback, executed if an error occurs
            error: function(error) {
                //server log for debugging
                console.log("Failed to retrieve key");
                console.log(error);
                //send an https response with status code 500
                res.send(500);
            }
        });
    }
}


//this function called when using the POST function
//creates an key object on the database via Parse
function createKey (data, callback) {

    //create a new instance of the Key object prototype
    var key = new Key();
    
    //temporarily save the tour (ID) string
    var tourID = data.tour;
    delete data.tour;

    //add the tour property to data, but in the form of a pointer object to the Tour prototype
    key.set("tour",  {"__type":"Pointer","className":"Tour","objectId":tourID});

    //execute the key save function on the object on the database with Parse, passing success and error callbacks
    key.save(data, {
        //success callback, executed if the save is successful
        success: function(key) {
            //server logs for debugging
            console.log("Created key with ID " + key.id + " at time " + key.createdAt);
            console.log(key);
            
            //return the key object to the callback function passed by the POST route function
            callback(key);
        },
        error: function(key, error) {
            //server logs for debugging
            console.log("Failed to create key.");
            console.log("Error: ", error);
            
            //return the error status and data to the callback function passed by the POST route function
            callback({status: 500, data: error});
        }
    });
}

//export this module
module.exports = key;
