//require the necessary files for this module, and initialize Parse
var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';

//instantiate Section and Tour object prototypes
var Section = Parse.Object.extend("Section");
var Tour = Parse.Object.extend("Tour");

//section module object
//contains functions for all REST api routes for the section object
var section = {

    //GET route function
    //returns a single section object
    //required param(s) in req(uest): id of the section object to be fetched
    //optional param(s) in req: none
	GET: function(req, res) {
        //server log for debugging
		console.log("GET SECTION");
        
        //isolates id from request params
		var id = req.params.id;
        
        //instantiates a new query to Parse (database mount) for the section prototype
		var query = new Parse.Query(Section);
        
        //execute query and pass the id as a parameter, as well as success and error callbacks
		query.get(id, {
            //success callback, executed if the query is successful
			success: function(section) {
                //server log for debugging
				console.log("Section " + id + " retrieved succesfully");
                //send an https response with status code 200 and the section data in JSON format
				res.status(200).send(section);
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
    //returns as many section objects as requested
    //required param(s) in req: none
    //optional param(s) in req: "limit" - number of sections to be fetched, otherwise set to 20,
    //                          "orderBy" - 'descending' or 'ascending' order
	GET_ALL: function(req, res) {
        //server log for debugging
		console.log("GET ALL SECTIONS");
        
        //isolates limit and orderBy from request params

        //default limit to 20 if no limit is passed
		var limit = req.query.limit || 20;
        //TO DO: FIX IT
        //default orderBy to null if no value is passed
		var orderBy = req.query.limit || null;

        //instantiates a new query to Parse (database mount) for the section prototype
		var query = new Parse.Query(Section);
        
        //set the limit to the appropriate value in the query
		query.limit(parseInt(limit));
        
        //execute 'find' query and pass success and error callbacks as parameters
		query.find({
            //success callback, executed if the query is successful
			success: function(results) {
                //server log for debugging
				console.log(results.length + " sections retrieved");
				res.status(200).send(results);
			},
            //error callback, executed if an error occurs
			error: function(error) {
                //server log for debugging
				console.log("Failed to retrieve sections");
				console.log(error);
                //send an https response with status code 500
				res.send(500);
			}
		});
	},

    //POST route function
    //creates a new section object
    //required param(s) in req: none
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
	POST: function(req, res) {
        //server log for debugging
		console.log("POST SECTION:\n", req.body);
        
        //isolates relevant data from request
		var data = req.body;

        //expected input is the expected format and data of the request, in type application/json
        //it does not represent actual data values, however it checks the value types (eg. is the data value a String, Boolean?)
		var expectedInput = {
			"title": "", //expected input is a String
			"description": "",
			"tour": "",
			"superSection": "",
			"depth": 0 //expected input is a Number
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
        //the request is formatted correctly, call the createSection function (separate logic function) and pass it a callback function
        else {
			createSection(parseData, function(result) {
                //checks the status code sent back, if there is no server error (status 500)
                //then send an https response with status code 201 (Created) as well as the data in JSON format.
				if (result.status !== 500 && result.status !== 400) {
					res.status(201).send(result);
				}
                //else send an https response with the error status code and data returned by the createSection function
				else
					res.status(result.status).send(result.data);
			});
		}
	},

    //PUT route function
    //updates an section object
    //required param(s) in req: id of the section object to be updated
    //optional param(s) in req: none
    //required data in req.body: see expectedInput
	PUT: function(req, res) {
        //server log for debugging
		console.log("PUT SECTION:\n", req.body);
        
        //isolates relevant data from request
		var data = req.body;
        //isolates id from request params
		var id = req.params.id;
        
        //check expected input with validate functions, see POST route for more detailed documentation

		var expectedInput = {
			"title": "",
			"description": "",
			"tour": "",
			"superSection": "",
			"depth": 0
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);
        
        //server log for debugging
		console.log("Parsed Data: ", parseData);
        
        //instantiates a new query to Parse (database mount) for the Section prototype
		var query = new Parse.Query(Section);
        
        //first GET the section object to be updated
        //execute query and pass the id as a parameter, as well as success and error callbacks
		query.get(id, {
            //success callback, executed if the query is successful
			success: function(section) {
                //server log for debugging
				console.log("Section " + id + " retrieved succesfully");
                
                //iterate over the properties in the parsed data
				for (var prop in parseData) {
                    //override the current data in the section object with our parsed data
					section.set(prop.toString(), parseData[prop]);
				}
                
                //execute the save function on the object on the database with Parse, passing success and error callbacks
				section.save(null, {
                    //success callback, executed if the save is successful
					success: function(section) {
                        //server log for debugging
						console.log("Section " + id + " updated succesfully");
                        //send an https response with status code 200 and the updated section data in JSON format
						res.status(200).send(section);
					},
                    //error callback, executed if an error occurs during the save
					error:  function(section, error) {
                        //server log for debugging
						console.log("Failed to update section " + id);
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
    //deletes an section object
    //required param(s) in req: id of the section object to be deleted
    //optional param(s) in req: none
	DELETE: function(req, res) {
        //server log for debugging
		console.log("DELETE SECTION");
        
        //isolates id from request params
		var id = req.params.id;
        
        //instantiates a new query to Parse (database mount) for the Section prototype
		var query = new Parse.Query(Section);
        
        //first GET the section object to be deleted
        //execute query and pass the id as a parameter, as well as success and error callbacks
		query.get(id, {
            //success callback, executed if the query is successful
			success: function(section) {
                //server log for debugging
				console.log("Section " + id + " retrieved succesfully");
				section.destroy({
                    //success callback, executed if the destroy is successful
					success: function(section) {
                        //server log for debugging
						console.log("Deleted section " + id);
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
//creates an section object on the database via Parse
function createSection (data, callback) {

    //create a new instance of the section object prototype
	var section = new Section();
    
    //temporarily save the tour and superSection (ID) string
	var tourID = data.tour;
	var superSectionID = data.superSection;
    //delete the tour and superSection string from the data
	delete data.tour;
	delete data.superSection;

    //checks if there is no tour ID
    //if true, tour ID is missing
	if (!tourID || tourID.length < 1) {
        //return the error status code 400 and error message to the callback function passed by the POST route function
		callback({status: 400, data: {"error": "There must be a tourID included in the data."}});
	} 
    //if false, tour ID is correctly included in data
    else {

		section.set("tour",  {"__type":"Pointer","className":"Tour","objectId":tourID});
        
        //if a superSection is included, create a pointer to it
		if (superSectionID.length !== 0) {
            //creates pointer to superSection with the included superSection ID
			section.set("superSection",  {"__type":"Pointer","className":"Section","objectId":superSectionID});
		} 
        //if no superSection is included, create the pointer with a null ID
        else {
            //creates null pointer
			section.set("superSection",  {"__type":"Pointer","className":"Section","objectId":null});
		}
        
        //execute the section save function on the object on the database with Parse, passing success and error callbacks
		section.save(data, {
            //success callback, executed if the save is successful
			success: function(section) {
                //server log for debugging
				console.log("Created section with ID " + section.id + " at time " + section.createdAt);
				console.log(section);
            
                //return the section object to the callback function passed by the POST route function
				callback(section);
			},
            //error callback, executed if the save fails (is unsuccessful)
			error: function(section, error) {
                //server log for debugging
				console.log("Failed to create section.");
				console.log("Error: ", error);
            
            //return the error status and data to the callback function passed by the POST route function
				callback({status: 500, data: error});
			}
		});
	}
}

//export this module
module.exports = section;
