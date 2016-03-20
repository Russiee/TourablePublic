var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Admin = Parse.Object.extend("_User");
var Organization = Parse.Object.extend("Organization");

//Admin route module
//Contains all functions for admin route (POST, PUT, GET, DELETE)
var admin = {
    //one of two GET functions, this one getting only a single object for each request
	GET: function(req, res) {
		console.log("GET ADMIN");
        //Prepares to send query to database
		var id = req.params.id;
        //Type of request to be made
		var query = new Parse.Query(Admin);
		console.log(query);
		//Sends query to API database
        query.get(id, {
            //If the query is successful 
			success: function(admin) {
				console.log("Admin " + id + " retrieved succesfully");
				//Send back status code 200 (request worked), and admin JSON object
                res.status(200).send(admin); 
			},
            //If the query is not successful
			error: function(object, error) {
				console.log("Error retrieving " + id);
				//Sends back status code 404, object not found
                res.sendStatus(404);  
			}
		});
	},
    
    //second of the GET functions, this one getting multiple objects with each request
	GET_ALL: function(req, res) {
		console.log("GET ALL ADMINS");
        //Limit to number of objects to get
        //Limit can be set, or left to resort to default value of 20
		var limit = req.query.limit || 20;
        //Changes the order in which they are returned
        //TO DO: FIX IT
		var orderBy = req.query.limit || null;
        //Sends the query for the admin object(s)
		var query = new Parse.Query(Admin);
        //Informs the query to use the limit number to return correct number of objects
		query.limit(parseInt(limit));
        //query.find to check for all admins (without using object IDs)
		query.find({
            //On query success, function(results) is run
			success: function(results) {
				console.log(results.length + " admins retrieved");
                //Sends status code 200 (request worked), and admin JSON objects
				res.status(200).send(results);
			},
            //If this query is unsuccessful, print the error and return eroor 500 (server error)
			error: function(error) {
				console.log("Failed to retrieve admins");
				console.log(error);
				res.sendStatus(500);
			}
		});
	},
    
    //POST function, used to add objects to the database
	POST: function(req, res) {
		console.log("POST ADMIN:",req.body);
		var data = req.body;
        //Expected input is the expected format of the request
        //It does not represent actual data values, however it checks the value types (eg. is the data value a String, Boolean?)
		var expectedInput = {
			"username": "", //expected input is a String
			"email": "", 
			"password": "",
			"organization": "",
			"isSuper": false //expected input is a Boolean
		};
        //This runs the validate module function to check the request input format against the expected input format
        //Checking that each data value is of the correct type for the database
        //returns false if the request input format is not correct
		var validInput = validate.validateInput(data, expectedInput);
        //Runs through the request data and removes any unexpected properties
        //This cleans the data and ensures it adheres to the correct format
		var parseData = validate.parseData(data, expectedInput);
        
		console.log("Parsed Data: ", parseData);
		//If the input is not valid, send back a bad request status code (status 400)
        if (!validInput) {
			res.sendStatus(400);
		} 
        //If the input is valid call the createAdmin function (separate logic function) and function(result), a callback function
        else {
			createAdmin(parseData, function(result) {
				console.log("callback");
                //Checks the status code sent back, if there is no server error (status 500)
                //return the data and the status code from the successful POST
				if (result.status !== 500) {
					res.status(201).send(result);
				}
				else {
					res.status(result.status).send(result.data);
				}
			});
		}
	},
    
    //PUT function, used to update admin objects already in the database
	//TODO: AUTHENTICATE FOR THIS ROUTE TO WORK
	PUT: function(req, res) {
		console.log("PUT ADMIN:\n", req.body);
		var data = req.body;
        //Prepares to send request to database
		var id = req.params.id;
        //Checking expected input with validate functions
		var expectedInput = {
			"username": "",
			"email": "",
			"organization": "",
			"isSuper": false
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);
		console.log("Parsed Data: ", parseData);
        
        //Parsing through the new update query
		var query = new Parse.Query(Admin);
        //Sending the query to the database
		query.get(id, {
            //On success invoke this function
			success: function(admin) {
                //First it finds the admin object to be updated
				console.log("Admin " + id + " retrieved succesfully");
				for (var prop in parseData) {
					admin.set(prop.toString(), parseData[prop]);
				}
				admin.save(null, {
					success: function(admin) {
						console.log("Admin " + id + " updated succesfully");
						res.status(200).send(admin);
					},
					error:  function(admin, error) {
						console.log("Failed to update admin " + id);
						console.log(error);
						res.status(500).send(error);
					}
				});
			},
            //If unsuccessful, invoke this function returning the error and status code (404)
			error: function(object, error) {
				console.log("Error retrieving " + id);
				console.log(error);
				res.sendStatus(404);
			}
		});
	},

	//TODO: AUTHENTICATE FOR THIS ROUTE TO WORK
    //DELETE function, used to remove admin objects from the database
	DELETE: function(req, res) {
		console.log("DELETE ADMIN");
        //Prepares to send request to database
		var id = req.params.id;
        //Type of request to be made
		var query = new Parse.Query(Admin);
        //Sends query to database
		query.get(id, {
            //Able to access user objects with admin rights
			useMasterKey: true,
            //On successful query, delete the specified admin object using the object's ID
			success: function(admin) {
				console.log("Admin " + id + " retrieved succesfully");
				admin.destroy({
                    //If successfully deleted, return deleted object's ID, and status code 200
					success: function(admin) {
						console.log("Deleted admin " + id);
						res.sendStatus(200);
					},
                    //If unsuccessful deletion, return error and status code 500 (server error)
					error: function(error) {
						console.log("Failed to delete " + id);
						console.log(error);
						res.sendStatus(500);
					}
				});
			},
            //On unsuccessful query, return the error and status code 404 (object not found)
			error: function(object, error) {
				console.log("Error retrieving " + id);
				console.log(error);
				res.sendStatus(404);
			}
		});

	}
}

//The function called when using the POST function
//This creates the user object
function createAdmin (data, callback) {
    //Preparing the new admin object and organization it will be pointing to
	var admin = new Admin();
	var orgID = data.organization;
    //Creation of the pointer object (a reference to the object)
	delete data.organization;
	admin.set("organization",  {"__type":"Pointer","className":"Organization","objectId":orgID});
    //Saving the user object in the database
	admin.signUp(data, {
        //If successful, callback completes with the newly created admin object being returned
		success: function(admin) {
			console.log("Created admin with ID " + admin.id + " at time " + admin.createdAt);
			console.log(admin);
			callback(admin);
		},
        //If unsuccessful, return the error and status code 500 (server error)
		error: function(admin, error) {
			console.log("Failed to create admin.");
			console.log("Error: ", error);
			callback({status: 500, data: error});
		}
	});
}

module.exports = admin;
