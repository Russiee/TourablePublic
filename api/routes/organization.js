//require the necessary files for this module, and initialize Parse
var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';

//instantiate Organization object prototype
var Organization = Parse.Object.extend("Organization");

//organization module object
//contains functions for all REST api routes for the organization object
var organization = {

    //GET route function
    //returns a single organization object
    //required param(s) in req(uest): id of the organization object to be fetched
    //optional param(s) in req: none
    GET: function(req, res) {
        //server log for debugging
        console.log("GET ORGANIZATION");

        //isolates id from request params
        var id = req.params.id;

        //instantiates a new query to Parse (database mount) for the Organization prototype
        var query = new Parse.Query(Organization);

        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(org) {
                //server log for debugging
                console.log("Organization " + id + " retrieved succesfully");
                //send an https response with status code 200 and the organization data in JSON format
                res.status(200).send(org);
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
    //returns as many organization objects as requested
    //required param(s) in req: none
    //optional param(s) in req: "limit" - number of organizations to be fetched, otherwise set to 20,
    //                          "orderBy" - 'descending' or 'ascending' order
    GET_ALL: function(req, res) {
        //server log for debugging
        console.log("GET ALL ORGANIZATIONS");

        //isolates limit and orderBy from request query parameters

        //default limit to 20 if no limit is passed
        var limit = req.query.limit || 20;
        //default orderBy to null if no value is passed
        var orderBy = req.query.orderBy || null;

        //instantiates a new query to Parse (database mount) for the Organization prototype
        var query = new Parse.Query(Organization);

        //set the limit to the appropriate value in the query
        query.limit(parseInt(limit));

        //if orderBy was passed as a param, sort the query by that value
        if (orderBy) {
            query.ascending(orderBy);
        }

        //execute 'find' query and pass success and error callbacks as parameters
        query.find({
            //success callback, executed if the query is successful
            success: function(results) {
                //server log for debugging
                console.log(results.length + " organizations retrieved");
                //send an https response with status code 200 and the queried data in JSON format
                res.status(200).send(results);
            },
            //error callback, executed if an error occurs
            error: function(error) {
                //server log for debugging
                console.log("Failed to retrieve organizations");
                console.log(error);
                //send an https response with status code 500
                res.send(500);
            }
        });
    },

    //POST route function
    //creates a new organization object
    //required param(s) in req: none
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
    POST: function(req, res) {
        //server log for debugging
        console.log("POST ORGANIZATION:\n", req.body);

        //isolates relevant data from request
        var data = req.body;

        //expected input is the expected format and data of the request, in type application/json
        //it does not represent actual data values, however it checks the value types (eg. is the data value a String, Boolean?)
        var expectedInput = {
            "key": "", //expected input is a String
            "name": "",
            "color": "",
            "logo": ""
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

        //if request format is incorrect, return https response 400 (bad request)
        if (!validInput) {
            res.sendStatus(400);
        } else {
            //the request is formatted correctly, call the createOrganization function (separate logic function) and pass it a callback function
            createOrganization(parseData, function(result) {
                //checks the status code sent back, if there is no server error (status 500)
                //then send an https response with status code 201 (Created) as well as the data in JSON format.
                if (result.status !== 500){
                    res.status(201).send(result);
                }
                //else send an https response with the error status code and data returned by the createOrganization function
                else
                    res.status(result.status).send(result.data);
            });
        }
    },

    //PUT route function
    //updates an organization object
    //required param(s) in req: id of the organization object to be updated
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
    PUT: function(req, res) {
        //server log for debugging
        console.log("PUT ORGANIZATION:\n", req.body);

        //isolates relevant data from request
        var data = req.body;
        //isolates id from request params
        var id = req.params.id;

        //check expected input with validate functions, see POST route for more detailed documentation
        var expectedInput = {
            key: "",
            name: "",
            color: "",
            logo: ""
        };

        var validInput = validate.validateInput(data, expectedInput);
        var parseData = validate.parseData(data, expectedInput);

        //server log for debugging
        console.log("Parsed Data: ", parseData);

        //instantiates a new query to Parse (database mount) for the Organization prototype
        var query = new Parse.Query(Organization);

        //first GET the organization     object to be updated
        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(org) {
                //server log for debugging
                console.log("Organization " + id + " retrieved succesfully");

                //iterate over the properties in the parsed data
                for (var prop in parseData) {
                    //override the current data in the organization object with our parsed data
                    org.set(prop.toString(), parseData[prop]);
                }

                //execute the save function on the object on the database with Parse, passing success and error callbacks
                org.save(null, {
                    //success callback, executed if the save is successful
                    success: function(org) {
                        //server log for debugging
                        console.log("Organization " + id + " updated succesfully");
                        //send an https response with status code 200 and the updated organization data in JSON format
                        res.status(200).send(org);
                    },
                    error:  function(org, error) {
                        //server log for debugging
                        console.log("Failed to update organization " + id);
                        console.log(error);
                        //send an https response with status code 500, as well as the error data in JSON format
                        res.status(500).send(error);
                    }
                });

            },
            //error callback, executed if an error occurs
            error: function(object, error) {
                //server log for debugging
                console.log("Error retrieving " + id);
                console.log(error);
                //send an https response with status code 404 (not found)
                res.sendStatus(404);
            }
        });
    },

    //DELETE route function
    //deletes an organization object
    //required param(s) in req: id of the organization object to be deleted
    //optional param(s) in req: none
    DELETE: function(req, res) {
        //server log for debugging
        console.log("DELETE ORGANIZATION");

        //isolates id from request params
        var id = req.params.id;

        //instantiates a new query to Parse (database mount) for the Organization prototype
        var query = new Parse.Query(Organization);

        //first GET the organization object to be deleted
        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(org) {
                //server log for debugging
                console.log("Organization " + id + " retrieved succesfully");

                //execute the destroy function on the object on the database with Parse, passing success and error callbacks
                org.destroy({
                    //success callback, executed if the destroy is successful
                    success: function(org) {
                        //server log for debugging
                        console.log("Deleted organization " + id);
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

    }
}

//this function called when using the POST function
//creates an organization object on the database via Parse
function createOrganization (data, callback) {

    //create a new instance of the Organization object prototype
    var org = new Organization();

    //execute the organization save function on the object on the database with Parse, passing success and error callbacks
    org.save(data, {
        //success callback, executed if the save is successful
        success: function(org) {
            console.log("Created organization with ID " + org.id + " at time " + org.createdAt);
            console.log(org);

            //return the organization object to the callback function passed by the POST route function
            callback(org);
        },
        //error callback, executed if the save fails (is unsuccessful)
        error: function(org, error) {
            console.log("Failed to create organization.");
            console.log("Error: ", error);

            //return the error status and data to the callback function passed by the POST route function
            callback({status: 500, data: error});
        }
    });
}

//export this module
module.exports = organization;
