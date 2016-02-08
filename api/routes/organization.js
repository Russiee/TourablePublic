var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Organization = Parse.Object.extend("Organization");

var organization = {

	GET: function(req, res) {
		var id = req.params.id;
		var query = new Parse.Query(Organization);
		query.get(id, {
			success: function(org) {
				// The object was retrieved successfully.
				console.log("Organization " + id + " retrieved succesfully");
				res.send(org);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},

	POST: function(req, res) {
		console.log("POST ORGANIZATION:\n", req.body);
		var data = req.body;
		var expectedInput = {
			"key": "",
			"name": "",
			"superAdmins": [],
			"admins": [],
			"color": "",
			"logo": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
        var parseData = validate.parseData(data, expectedInput);
        
        console.log("Parsed Data: ", parseData);
		if (!validInput) {
			res.sendStatus(400);
		} else {
			createOrganization(data, function(result) {
				if (result.status !== 500)
					res.status(201).send(result);
				else
					res.sendStatus(500);
			});
		}
	},

	PUT: function(req, res) {
        console.log("PUT ORGANIZATION:\n", req.body);
		var data = req.body;
		var id = req.params.id;
		
		var expectedInput = {
			key: "",
			name: "",
			superAdmins: [],
			admins: [],
			color: "",
			logo: ""
		};

		var validInput = validate.validateInput(data, expectedInput);
        var parseData = validate.parseData(data, expectedInput);
        console.log("Parsed Data: ", parseData);
        
        var query = new Parse.Query(Organization);
		query.get(id, {
			success: function(org) {
				// The object was retrieved successfully.
                console.log("Organization " + id + " retrieved succesfully");
				for (var prop in parseData) {
                    org.set(prop.toString(), parseData[prop]); 
                }
                org.save();
                console.log("Organization " + id + " updated succesfully");
                res.status(200).send(org);
                
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
                console.log(error);
                res.sendStatus(404);
			}
		});
    },

	DELETE: function(req, res) {
        var id = req.params.id;
        var query = new Parse.Query(Organization);
		query.get(id, {
			success: function(org) {
				// The object was retrieved successfully.
                console.log("Organization " + id + " retrieved succesfully");
				org.destroy({
                    success: function(org) {
                        // The object was deleted from the Parse database.
                        console.log("Deleted organization " + org.id);
                        res.sendStatus(200);
                    },
                    error: function(org, error) {
                        // The delete failed.
                        // error is a Parse.Error with an error code and message.
                        console.log("Failed to delete " + org.id);
                        console.log(error);
                        res.sendStatus(500);
                    }
                });
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
                console.log(error);
                res.sendStatus(404);
			}
		});
        
	}
}

function createOrganization (data, callback) {
    
	var org = new Organization();
	org.save(data, {
		success: function(org) {
			// The object was saved successfully.
			console.log("Created Organization with ID " + org.id + " at time " + org.createdAt);
			console.log(org);
			callback(org);
		},
		error: function(org, error) {
			// The save failed.
			console.log("Failed to create Organization.");
			console.log("Error: ", error);
			callback({status: 500});
		}
	});
}

module.exports = organization;
