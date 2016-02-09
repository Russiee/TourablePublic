var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Organization = Parse.Object.extend("Organization");

var organization = {

	GET: function(req, res) {
        console.log("GET ORGANIZATION");
		var id = req.params.id;
		var query = new Parse.Query(Organization);
		query.get(id, {
			success: function(org) {
				console.log("Organization " + id + " retrieved succesfully");
				res.status(200).send(org);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},
    
    GET_ALL: function(req, res) {
        console.log("GET ALL ORGANIZATIONS");
        var limit = req.query.limit || 5;
        var orderBy = req.query.limit || null;
        
		var query = new Parse.Query(Organization);
        query.limit(parseInt(limit));
        query.find({
            success: function(results) {
                console.log(results.length + " organizations retrieved");
                res.status(200).send(results);
            },
            error: function(error) {
                console.log("Failed to retrieve organizations");
                console.log(error);
                res.send(500);
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
			createOrganization(parseData, function(result) {
				if (result.status !== 500)
					res.status(201).send(result);
				else
					res.status(result.status).send(result.data);
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
                console.log("Organization " + id + " retrieved succesfully");
				for (var prop in parseData) {
                    org.set(prop.toString(), parseData[prop]); 
                }
                org.save(null, {
                    success: function(org) {
                        console.log("Organization " + id + " updated succesfully");
                        res.status(200).send(org);
                    },
                    error:  function(org, error) {
                        console.log("Failed to update organization " + id);
                        console.log(error);
                        res.status(500).send(error);
                    }
                });
                
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
                console.log(error);
                res.sendStatus(404);
			}
		});
    },

	DELETE: function(req, res) {
        console.log("DELETE ORGANIZATION");
        var id = req.params.id;
        var query = new Parse.Query(Organization);
		query.get(id, {
			success: function(org) {
                console.log("Organization " + id + " retrieved succesfully");
				org.destroy({
                    success: function(org) {
                        console.log("Deleted organization " + id);
                        res.sendStatus(200);
                    },
                    error: function(error) {
                        console.log("Failed to delete " + id);
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
			console.log("Created organization with ID " + org.id + " at time " + org.createdAt);
			console.log(org);
			callback(org);
		},
		error: function(org, error) {
			console.log("Failed to create organization.");
			console.log("Error: ", error);
			callback({status: 500, data: error});
		}
	});
}

module.exports = organization;
