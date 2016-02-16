var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Admin = Parse.Object.extend("_User");
var Tour = Parse.Object.extend("Tour");

var tour = {

	GET: function(req, res) {
		console.log("GET TOUR");
		var id = req.params.id;
		var query = new Parse.Query(Tour);
		query.get(id, {
			success: function(tour) {
				console.log("Tour " + id + " retrieved succesfully");
				console.log(tour);
				res.status(200).send(tour);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},

	GET_ALL: function(req, res) {
		console.log("GET ALL TOURS");
		var limit = req.query.limit || 20;
		var orderBy = req.query.limit || null;

		var query = new Parse.Query(Tour);
		query.limit(parseInt(limit));
		query.find({
			success: function(results) {
				console.log(results.length + " tours retrieved");
				res.status(200).send(results);
			},
			error: function(error) {
				console.log("Failed to retrieve tours");
				console.log(error);
				res.send(500);
			}
		});
	},

	POST: function(req, res) {
		console.log("POST TOUR:\n", req.body);
		var data = req.body;

		var expectedInput = {
			"description": "",
			"title": "",
			"admin": "",
			"sections": [],
			"keys": [],
			"isPublic": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);

		console.log("Parsed Data: ", parseData);
		if (!validInput) {
			res.sendStatus(400);
		} else {
			createTour(parseData, function(result) {
				if (result.status !== 500) {
					//TODO check if superadmin
					var query = new Parse.Query(Admin);
					query.equalTo("objectId", result.get("admin").objectId);
					query.find({
						success: function(results) {
							Parse.Cloud.run('addTour', { username: results[0].getUsername(), tour: {"__type":"Pointer","className":"Admin","objectId": result.id} }, {
								success: function(status) {
									console.log(status);
								},
								error: function(error) {
									console.log(error);
								}
							});
						},
						error: function(error) {
							console.log("Failed to retrieve admins");
							console.log(error);
						}
					});
					res.status(201).send(result);
				}
				else
					res.status(result.status).send(result.data);
			});
		}
	},

	PUT: function(req, res) {
		console.log("PUT TOUR:\n", req.body);
		var data = req.body;
		var id = req.params.id;

		var expectedInput = {
			"description": "",
			"title": "",
			"admin": "",
			"sections": [],
			"keys": [],
			"isPublic": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);
		console.log("Parsed Data: ", parseData);

		var query = new Parse.Query(Tour);
		query.get(id, {
			success: function(tour) {
				console.log("Tour " + id + " retrieved succesfully");
				for (var prop in parseData) {
					tour.set(prop.toString(), parseData[prop]);
				}
				tour.save(null, {
					success: function(tour) {
						console.log("Tour " + id + " updated succesfully");
						res.status(200).send(tour);
					},
					error:  function(tour, error) {
						console.log("Failed to update tour " + id);
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
		console.log("DELETE TOUR");
		var id = req.params.id;
		var query = new Parse.Query(Tour);
		query.get(id, {
			success: function(tour) {
				console.log("Tour " + id + " retrieved succesfully");
				tour.destroy({
					success: function(tour) {
						console.log("Deleted tour " + id);
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

function createTour (data, callback) {

	var tour = new Tour();
	var adminID = data.admin;
	delete data.admin;
	tour.set("admin",  {"__type":"Pointer","className":"Admin","objectId":adminID});

	tour.save(data, {
		success: function(tour) {
			console.log("Created tour with ID " + tour.id + " at time " + tour.createdAt);
			console.log(tour);
			callback(tour);
		},
		error: function(tour, error) {
			console.log("Failed to create tour.");
			console.log("Error: ", error);
			callback({status: 500, data: error});
		}
	});
}

module.exports = tour;
