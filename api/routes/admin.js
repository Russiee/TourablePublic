var config = require('../config.js');

Parse = require('parse/node').Parse;
Parse.initialize(config.database().appID, config.database().masterKey);
Parse.serverURL = config.database().serverURL;

//require the necessary files for this module, and initialize Parse
var validate = require('./validate.js');

//instantiate admin and organization object prototypes
var Admin = Parse.Object.extend("_User");
var Organization = Parse.Object.extend("Organization");

//admin module object
//contains functions for all REST api routes for the admin object
var admin = {

    //GET route function
    //returns a single admin object
    //required param(s) in req(uest): id of the admin object to be fetched
    //optional param(s) in req: none
    GET: function(req, res) {
        //server log for debugging
        console.log("GET ADMIN");

        //isolates id from request params
        var id = req.params.id;

        //instantiates a new query to Parse (database mount) for the Admin prototype
        var query = new Parse.Query(Admin);

        //for debug purposes
        console.log(query);

        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(admin) {
                //server log for debugging
                console.log("Admin " + id + " retrieved succesfully");
                //send an https response with status code 200 and the admin data in JSON format
                res.status(200).send(admin);
            },
            //error callback, executed if an error occurs
            error: function(admin, error) {
                //server log for debugging
                console.log("Error retrieving " + id);
                //send an https response with status code 500
                //in the future, it would be better to analyze the error code to provide more semantic error codes
                res.sendStatus(500);
            }
        });
    },

    //GET_ALL route function
    //returns as many admin objects as requested
    //required param(s) in req: none
    //optional param(s) in req: "limit" - number of admins to be fetched, otherwise set to 20,
    //                          "orderBy" - the 'column title' that the query results should be ordered by
    //                          "organization" - the id of the organization that the query results should be filtered for
    GET_ALL: function(req, res) {
        //server log for debugging
        console.log("GET ALL ADMINS");

        //isolates limit, orderBy, and organization from query parameters

        //default limit to 20 if no limit is passed
        var limit = req.query.limit || 20;
        //default orderBy to null if no value is passed
        var orderBy = req.query.orderBy || null;
        //default organization to null if no value is passed
        var organization = req.query.organization || null;

        //instantiates a new query to Parse (database mount) for the Admin prototype
        var query = new Parse.Query(Admin);

        //set the limit to the appropriate value in the query
        query.limit(parseInt(limit));

        //default to createdAt
        query.descending('updatedAt');

        //if orderBy was passed as a param, sort the query by that value
        if (orderBy) {
            query.ascending(orderBy);
        }

        //if organization was passed as a param, sort the query by that value
        if (organization) {
            query.equalTo("organization",{"__type":"Pointer","className":"Organization","objectId":organization})
        }

        //execute 'find' query and pass success and error callbacks as parameters
        query.find({
            //success callback, executed if the query is successful
            success: function(results) {
                //server log for debugging
                console.log(results.length + " admins retrieved");
                //send an https response with status code 200 and the queried data in JSON format
                res.status(200).send(results);
            },
            //error callback, executed if an error occurs
            error: function(error) {
                //server logs for debugging
                console.log("Failed to retrieve admins");
                console.log(error);
                //send an https response with status code 500
                res.sendStatus(500);
            }
        });
    },

    //POST route function
    //creates a new admin object
    //required param(s) in req: none
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
    POST: function(req, res) {
        //server log for debugging
        console.log("POST ADMIN:", req.body);

        //isolates relevant data from request
        var data = req.body;

        //expected input is the expected format and data of the request, in type application/json
        //it does not represent actual data values, however it checks the value types (eg. is the data value a String, Boolean?)
        var expectedInput = {
            "username": "", //expected input is a String
            "email": "",
            "firstname": "",
            "lastname": "",
            "password": "",
            "organization": "",
            "isSuper": false //expected input is a Boolean
        };

        //this runs the validate module function to check the request input format against the expected input format
        //checking that each data value is of the correct type for the database
        //returns false if the request input format is not correct
        var validInput = validate.validateInput(data, expectedInput);
        //runs through the request data and removes any unexpected properties
        //this cleans the data and ensures it adheres to the correct format
        var parseData = validate.parseData(data, expectedInput);

        //server log for debugging
        console.log("Parsed Data: ", parseData);

        //if the input is not valid, send back an https response with code 400 (bad request)
        if (!validInput) {
            res.sendStatus(400);
        }
        //else the input is valid, call the createAdmin function (separate logic function) and pass it a callback function
        else {
            createAdmin(parseData, function(result) {
                //checks the status code sent back, if there is no server error (status 500)
                //then send an https response with status code 201 (Created) as well as the data in JSON format.
                if (result.status !== 500) {
                    res.status(201).send(result);
                }
                //else send an https response with the error status code and data returned by the createAdmin function
                else {
                    res.status(result.status).send(result.data);
                }
            });
        }
    }
}

//this function called when using the POST function
//creates an admin object on the database via Parse
function createAdmin (data, callback) {

    //create a new instance of the Admin object prototype
    var admin = new Admin();

    //temporarily save the organization (ID) string
    var orgID = data.organization;
    //delete the organization string from the data
    delete data.organization;

    //add the organization property to data, but in the form of a pointer object to the Organization prototype
    admin.set("organization",  {"__type":"Pointer","className":"Organization","objectId":orgID});

    //execute the admin sign up function on the object on the database with Parse, passing success and error callbacks
    admin.signUp(data, {
        //success callback, executed if the sign up is successful
        success: function(admin) {
            //server logs for debugging
            console.log("Created admin with ID " + admin.id + " at time " + admin.createdAt);
            console.log(admin);

            //return the admin object to the callback function passed by the POST route function
            callback(admin);
        },
        //error callback, executed if an error occurs during the sign up
        error: function(admin, error) {
            //server logs for debugging
            console.log("Failed to create admin.");
            console.log("Error: ", error);

            //return the error status and data to the callback function passed by the POST route function
            callback({status: 500, data: error});
        }
    });
}

//export this module
module.exports = admin;
