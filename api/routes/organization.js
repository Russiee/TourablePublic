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
				console.log("Object " + id + " retrieved succesfully");
				res.send(org);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},

	POST: function(req, res) {
		console.log("POST ORGANIZATION:\n",req.body);
		var data = req.body;

		var expectedInput = {
			key: "",
			name: "",
			superAdmins: [],
			admins: [],
			color: "",
			logo: ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		if (!validInput) {
			res.sendStatus(400);
		} else {
			createOrganization(data, function(success) {
				if (success === true)
					res.sendStatus(201);
				else
					res.sendStatus(500);
			});
		}
	},

	PUT: function(req, res) {
		var id = req.params.id;
		var data = req.body;

		var expectedInput = {
			key: "",
			name: "",
			superAdmins: [],
			admins: [],
			color: "",
			logo: ""
		};

		var validInput = validate.validateInput(data, expectedInput);

		console.log(validInput);

		if (validInput)
			res.sendStatus(200);
		else
			res.sendStatus(400);
	},

	DELETE: function(req, res) {
		var id = req.params.id;
	}
}

function createOrganization (data, callback) {

	var org = new Organization();
	org.save(data, {
		success: function(org) {
			// The object was saved successfully.
			console.log("Created Organization with ID " + org.id + " at time " + org.createdAt);
			console.log(org);
			callback(true);
		},
		error: function(org, error) {
			// The save failed.
			console.log("Failed to create Organization.");
			console.log("Error: ", error);
			callback(true);
		}
	});
}

module.exports = organization;
