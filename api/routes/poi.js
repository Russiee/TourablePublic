//require the necessary files for this module, and initialize Parse
var validate = require('./validate.js');

//instantiate Section and POI object prototypes
var POI = Parse.Object.extend("POI");
var Section = Parse.Object.extend("Section");

//POI module object
//contains functions for all REST api routes for the POI object
var poi = {

    //GET route function
    //returns a single POI object
    //required param(s) in req(uest): id of the POI object to be fetched
    //optional param(s) in req: none
    GET: function(req, res) {
        //server log for debugging
        console.log("GET POINT OF INTEREST");

        //isolates id from request params
        var id = req.params.id;

        //instantiates a new query to Parse (database mount) for the POI prototype
        var query = new Parse.Query(POI);

        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(poi) {
                //server log for debugging
                console.log("POI " + id + " retrieved succesfully");
                //send an https response with status code 200 and the POI data in JSON format
                res.status(200).send(poi);
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
    //returns as many POI objects as requested
    //required param(s) in req: none
    //optional param(s) in req: "limit" - number of POIs to be fetched, otherwise set to 20,
    //                          "orderBy" - 'descending' or 'ascending' order
    //                          "section" - the id of the section that the query results should be filtered for

    GET_ALL: function(req, res) {
        //server log for debugging
        console.log("GET ALL POINTS OF INTEREST");

        //isolates limit and orderBy from query parameters

        //default limit to 20 if no limit is passed
        var limit = req.query.limit || 20;
        //default orderBy to null if no value is passed
        var orderBy = req.query.orderBy || null;
        //default section to null if no value is passed
        var section = req.query.section || null;

        //instantiates a new query to Parse (database mount) for the POI prototype
        var query = new Parse.Query(POI);

        //set the limit to the appropriate value in the query
        query.limit(parseInt(limit));

        //if orderBy was passed as a param, sort the query by that value
        if (orderBy) {
            query.ascending(orderBy);
        }

        //if section was passed as a param, sort the query by that value
        if (section) {
            query.equalTo("section",{"__type":"Pointer","className":"Section","objectId":section})
        }

        //execute 'find' query and pass success and error callbacks as parameters
        query.find({
            //success callback, executed if the query is successful
            success: function(results) {
                //server log for debugging
                console.log(results.length + " pois retrieved");
                //send an https response with status code 200 and the queried data in JSON format
                res.status(200).send(results);
            },
            //error callback, executed if an error occurs
            error: function(error) {
                //server log for debugging
                console.log("Failed to retrieve pois");
                console.log(error);
                //send an https response with status code 500
                res.send(500);
            }
        });
    },

    //POST route function
    //creates a new POI object
    //required param(s) in req: none
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
    POST: function(req, res) {
        //server log for debugging
        console.log("POST POINT OF INTEREST:\n", req.body);

        //isolates relevant data from request
        var data = req.body;

        //expected input is the expected format and data of the request, in type application/json
        //it does not represent actual data values, however it checks the value types (eg. is the data value a String, Boolean?)
        var expectedInput = {
            "title": "", //expected input is a String
            "description": "",
            "post": [], //expected input is an Array
            "section": ""
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
        }
        //the request is formatted correctly, call the createPOI function (separate logic function) and pass it a callback function
        else {
            createPOI(parseData, function(result) {
                //checks the status code sent back, if there is no server error (status 500)
                //then send an https response with status code 201 (Created) as well as the data in JSON format.
                if (result.status !== 500) {
                    res.status(201).send(result);
                }
                //else send an https response with the error status code and data returned by the createPOI function
                else
                    res.status(result.status).send(result.data);
            });
        }
    },

    //PUT route function
    //updates an POI object
    //required param(s) in req: id of the POI object to be updated
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
    PUT: function(req, res) {
        //server log for debugging
        console.log("PUT POINT OF INTEREST:\n", req.body);

        //isolates relevant data from request
        var data = req.body;
        //isolates id from request params
        var id = req.params.id;

        //check expected input with validate functions, see POST route for more detailed documentation
        var expectedInput = {
            "title": "",
            "description": "",
            "post": [],
            "section": ""
        };

        var validInput = validate.validateInput(data, expectedInput);
        var parseData = validate.parseData(data, expectedInput);

        //server log for debugging
        console.log("Parsed Data: ", parseData);

        //instantiates a new query to Parse (database mount) for the POI prototype
        var query = new Parse.Query(POI);

        //first GET the POI object to be updated
        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(poi) {
                //server log for debugging
                console.log("POI " + id + " retrieved succesfully");

                //iterate over the properties in the parsed data
                for (var prop in parseData) {
                    //override the current data in the POI object with our parsed data
                    poi.set(prop.toString(), parseData[prop]);
                }

                //execute the save function on the object on the database with Parse, passing success and error callbacks
                poi.save(null, {
                    //success callback, executed if the save is successful
                    success: function(poi) {
                        //server log for debugging
                        console.log("POI " + id + " updated succesfully");
                        //send an https response with status code 200 and the updated POI data in JSON format
                        res.status(200).send(poi);
                    },
                    //error callback, executed if an error occurs during the save
                    error:  function(poi, error) {
                        //server log for debugging
                        console.log("Failed to update poi " + id);
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
                //send an https response with status code 404
                res.sendStatus(404);
            }
        });
    },

    //DELETE route function
    //deletes an POI object
    //required param(s) in req: id of the POI object to be deleted
    //optional param(s) in req: none
    DELETE: function(req, res) {
        //server log for debugging
        console.log("DELETE POINT OF INTEREST");

        //isolates id from request params
        var id = req.params.id;

        //instantiates a new query to Parse (database mount) for the POI prototype
        var query = new Parse.Query(POI);

        //first GET the POI object to be deleted
        //execute query and pass the id as a parameter, as well as success and error callbacks
        query.get(id, {
            //success callback, executed if the query is successful
            success: function(poi) {
                //server log for debugging
                console.log("POI " + id + " retrieved succesfully");
                poi.destroy({
                    //success callback, executed if the destroy is successful
                    success: function(poi) {
                        //server log for debugging
                        console.log("Deleted poi " + id);
                        //send an https response with status code 200 to confirm the deletion
                        res.sendStatus(200);
                    },
                    //error callback, executed if an error occurs during deletion (delete not successful)
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
                //send an https response with status code 404
                res.sendStatus(404);
            }
        });

    }
}

//this function called when using the POST function
//creates an POI object on the database via Parse
function createPOI (data, callback) {

    //create a new instance of the POI object prototype
    var poi = new POI();

    //temporarily save the section (ID) string
    var sectionID = data.section;
    //delete the section string from the data
    delete data.section;

    //add the tour property to data, but in the form of a pointer object to the Tour prototype
    poi.set("section",  {"__type":"Pointer","className":"Section","objectId":sectionID});

    //execute the POI save function on the object on the database with Parse, passing success and error callbacks
    poi.save(data, {
        //success callback, executed if the save is successful
        success: function(poi) {
            //server log for debugging
            console.log("Created poi with ID " + poi.id + " at time " + poi.createdAt);
            console.log(poi);

            //return the POI object to the callback function passed by the POST route function
            callback(poi);
        },
        //error callback, executed if the save fails (is unsuccessful)
        error: function(poi, error) {
            //server log for debugging
            console.log("Failed to create poi.");
            console.log("Error: ", error);

            //return the error status and data to the callback function passed by the POST route function
            callback({status: 500, data: error});
        }
    });
}

//export this module
module.exports = poi;
