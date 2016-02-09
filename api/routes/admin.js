var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Admin = Parse.Object.extend("User");

var admin = {

	GET: function(req, res) {
        console.log("GET ADMIN");
		var id = req.params.id;
		var query = new Parse.Query(Admin);
		query.get(id, {
			success: function(admin) {
				console.log("Admin " + id + " retrieved succesfully");
				res.status(200).send(admin);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},
    
    GET_ALL: function(req, res) {
        console.log("GET ALL ADMINS");
        var limit = req.query.limit || 5;
        var orderBy = req.query.limit || null;
        
		var query = new Parse.Query(Admin);
        query.limit(parseInt(limit));
        query.find({
            success: function(results) {
                console.log(results.length + " admins retrieved");
                res.status(200).send(results);
            },
            error: function(error) {
                console.log("Failed to retrieve admins");
                console.log(error);
                res.send(500);
            }
        });
	},
    
   	POST: function(req, res) {
		console.log("POST ADMIN:",req.body);
		var data = req.body;

		var expectedInput = {
			"username": "",
			"email": "",
			"password": "",
			"organization": "",
			"tours": [],
            "isSuper": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
        var parseData = validate.parseData(data, expectedInput);
        
        console.log("Parsed Data: ", parseData);
		if (!validInput) {
			res.sendStatus(400);
		} else {
			createAdmin(parseData, function(result) {
				if (result.status !== 500)
					res.status(201).send(result);
				else
					res.status(result.status).send(result.data);
			});
		}
	},

    //TODO: AUTHENTICATE FOR THIS ROUTE TO WORK
    PUT: function(req, res) {
        console.log("PUT ADMIN:\n", req.body);
		var data = req.body;
		var id = req.params.id;
		
		var expectedInput = {
			"username": "",
			"email": "",
			"organization": "",
			"tours": [],
            "isSuper": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
        var parseData = validate.parseData(data, expectedInput);
        console.log("Parsed Data: ", parseData);
        
        var query = new Parse.Query(Admin);
		query.get(id, {
			success: function(admin) {
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
			error: function(object, error) {
				console.log("Error retrieving " + id);
                console.log(error);
                res.sendStatus(404);
			}
		});
    },

    //TODO: AUTHENTICATE FOR THIS ROUTE TO WORK
	DELETE: function(req, res) {
        var id = req.params.id;
        var query = new Parse.Query(Admin);
		query.get(id, {
			success: function(admin) {
                console.log("Admin " + id + " retrieved succesfully");
				admin.destroy({
                    success: function(admin) {
                        console.log("Deleted admin " + id);
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

function createAdmin (data, callback) {
    
	var admin = new Admin();
	admin.signUp(data, {
		success: function(admin) {
			console.log("Created admin with ID " + admin.id + " at time " + admin.createdAt);
			console.log(admin);
			callback(admin);
		},
		error: function(admin, error) {
			console.log("Failed to create admin.");
			console.log("Error: ", error);
			callback({status: 500, data: error});
		}
	});
}

module.exports = admin;
