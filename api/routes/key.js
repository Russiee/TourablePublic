var config = require('../config.js');
var validate = require('./validate.js');
var http = require('http');

var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Key = Parse.Object.extend("Key");
var Tour = Parse.Object.extend("Tour");

var key = {

	GET: function(req, res) {
		console.log("GET KEY");
		var id = req.params.id;
		var query = new Parse.Query(Key);
		query.get(id, {
			success: function(key) {
				console.log("Key " + id + " retrieved succesfully");
				res.status(200).send(key);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},

	GET_ALL: function(req, res) {
		console.log("GET ALL KEYS");
		var limit = req.query.limit || 20;
		var orderBy = req.query.limit || null;

		var query = new Parse.Query(Key);
		query.limit(parseInt(limit));
		query.find({
			success: function(results) {
				console.log(results.length + " keys retrieved");
				res.status(200).send(results);
			},
			error: function(error) {
				console.log("Failed to retrieve keys");
				console.log(error);
				res.send(500);
			}
		});
	},

	POST: function(req, res) {
		console.log("POST KEY:\n", req.body);
		var data = req.body;
		var expectedInput = {
			"code": "",
			"tour": "",
			"expiresAt": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);

		console.log("Parsed Data: ", parseData);

		var query = new Parse.Query(Key);
		query.equalTo("code", req.body.code);

		query.find({
			success: function(results) {
				if (results.length !== 0)
					res.status(400).send({error: "Keys must be unique"});
				else {
					if (!validInput) {
						res.sendStatus(400);
					} else {
						createKey(parseData, function(result) {
							if (result.status !== 500) {
								res.status(201).send(result);
							}
							else
								res.status(result.status).send(result.data);
						});
					}
				}
			}
		});
	},

	PUT: function(req, res) {
		console.log("PUT KEY:\n", req.body);
		var data = req.body;
		var id = req.params.id;

		var expectedInput = {
			"code": "",
			"tour": "",
			"expiresAt": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);
		console.log("Parsed Data: ", parseData);

		var query = new Parse.Query(Key);
		query.get(id, {
			success: function(key) {
				console.log("Key " + id + " retrieved succesfully");
				for (var prop in parseData) {
					key.set(prop.toString(), parseData[prop]);
				}
				key.save(null, {
					success: function(key) {
						console.log("Key " + id + " updated succesfully");
						res.status(200).send(key);
					},
					error:  function(key, error) {
						console.log("Failed to update key " + id);
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
		console.log("DELETE KEY");
		var id = req.params.id;
		var query = new Parse.Query(Key);
		query.get(id, {
			success: function(key) {
				console.log("Key " + id + " retrieved succesfully");
				key.destroy({
					success: function(key) {
						console.log("Deleted key " + id);
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

	},

	verify: function(req, res) {
		console.log("VERIFYING KEY");
		var code = req.params.code;

		var query = new Parse.Query(Key);
		query.equalTo("code", code);
		query.find({
			success: function(results) {
				console.log(results.length + " keys retrieved");
				if (results.length === 1)
					res.status(200).send(results);
				else if (results.length === 0)
					res.sendStatus(404);
				else
					res.sendStatus(500);
			},
			error: function(error) {
				console.log("Failed to retrieve key");
				console.log(error);
				res.send(500);
			}
		});
	}
}


function createKey (data, callback) {

	var key = new Key();
	var tourID = data.tour;
	delete data.tour;

	key.set("tour",  {"__type":"Pointer","className":"Tour","objectId":tourID});

	key.save(data, {
		success: function(key) {
			console.log("Created key with ID " + key.id + " at time " + key.createdAt);
			console.log(key);
			callback(key);
		},
		error: function(key, error) {
			console.log("Failed to create key.");
			console.log("Error: ", error);
			callback({status: 500, data: error});
		}
	});
}

module.exports = key;
