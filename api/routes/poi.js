var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var POI = Parse.Object.extend("POI");
var Section = Parse.Object.extend("Section");

var poi = {

	GET: function(req, res) {
		console.log("GET POINT OF INTEREST");
		var id = req.params.id;
		var query = new Parse.Query(POI);
		query.get(id, {
			success: function(poi) {
				console.log("POI " + id + " retrieved succesfully");
				res.status(200).send(poi);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},

	GET_ALL: function(req, res) {
		console.log("GET ALL POINTS OF INTEREST");
		var limit = req.query.limit || 20;
		var orderBy = req.query.limit || null;

		var query = new Parse.Query(POI);
		query.limit(parseInt(limit));
		query.find({
			success: function(results) {
				console.log(results.length + " pois retrieved");
				res.status(200).send(results);
			},
			error: function(error) {
				console.log("Failed to retrieve pois");
				console.log(error);
				res.send(500);
			}
		});
	},

	POST: function(req, res) {
		console.log("POST POINT OF INTEREST:\n", req.body);
		var data = req.body;

		var expectedInput = {
			"title": "",
			"description": "",
			"post": [],
			"section": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);

		console.log("Parsed Data: ", parseData);
		if (!validInput) {
			res.sendStatus(400);
		} else {
			createPOI(parseData, function(result) {
				if (result.status !== 500) {
					var query = new Parse.Query(Section);
					query.equalTo("objectId", result.get("section").objectId);
					query.find({
						success: function(results) {
							results[0].add("pois", result);
							results[0].save();
						},
						error: function(error) {
							console.log("Failed to retrieve section");
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
		console.log("PUT POINT OF INTEREST:\n", req.body);
		var data = req.body;
		var id = req.params.id;

		var expectedInput = {
			"title": "",
			"description": "",
			"post": [],
			"section": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);
		console.log("Parsed Data: ", parseData);

		var query = new Parse.Query(POI);
		query.get(id, {
			success: function(poi) {
				console.log("POI " + id + " retrieved succesfully");
				for (var prop in parseData) {
					poi.set(prop.toString(), parseData[prop]);
				}
				poi.save(null, {
					success: function(poi) {
						console.log("POI " + id + " updated succesfully");
						res.status(200).send(poi);
					},
					error:  function(poi, error) {
						console.log("Failed to update poi " + id);
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
		console.log("DELETE POINT OF INTEREST");
		var id = req.params.id;
		var query = new Parse.Query(POI);
		query.get(id, {
			success: function(poi) {
				console.log("POI " + id + " retrieved succesfully");
				poi.destroy({
					success: function(poi) {
						console.log("Deleted poi " + id);
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

function createPOI (data, callback) {

	var poi = new POI();
	var sectionID = data.section;
	delete data.section;

	poi.set("section",  {"__type":"Pointer","className":"Section","objectId":sectionID});

	poi.save(data, {
		success: function(poi) {
			console.log("Created poi with ID " + poi.id + " at time " + poi.createdAt);
			console.log(poi);
			callback(poi);
		},
		error: function(poi, error) {
			console.log("Failed to create poi.");
			console.log("Error: ", error);
			callback({status: 500, data: error});
		}
	});
}

module.exports = poi;
