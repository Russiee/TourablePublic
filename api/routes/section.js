var validate = require('./validate.js');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Section = Parse.Object.extend("Section");
var Tour = Parse.Object.extend("Tour");

var section = {

	GET: function(req, res) {
		console.log("GET SECTION");
		var id = req.params.id;
		var query = new Parse.Query(Section);
		query.get(id, {
			success: function(section) {
				console.log("Section " + id + " retrieved succesfully");
				res.status(200).send(section);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	},

	GET_ALL: function(req, res) {
		console.log("GET ALL SECTIONS");
		var limit = req.query.limit || 20;

		var query = new Parse.Query(Section);
		query.limit(parseInt(limit));
		query.find({
			success: function(results) {
				console.log(results.length + " sections retrieved");
				res.status(200).send(results);
			},
			error: function(error) {
				console.log("Failed to retrieve sections");
				console.log(error);
				res.send(500);
			}
		});
	},

	POST: function(req, res) {
		console.log("POST SECTION:\n", req.body);
		var data = req.body;

		var expectedInput = {
			"title": "",
			"description": "",
			"tour": "",
			"superSection": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);

		console.log("Parsed Data: ", parseData);
		if (!validInput) {
			res.sendStatus(400);
		} else {
			createSection(parseData, function(result) {
				if (result.status !== 500 && result.status !== 400) {
					if (result.toJSON().superSection.objectId === null) {
						var query = new Parse.Query(Tour);
						query.equalTo("objectId", result.toJSON().tour.objectId);
						query.find({
							success: function(results) {
								results[0].add("sections", result);
								results[0].save();
							},
							error: function(error) {
								console.log("Failed to retrieve tour");
								console.log(error);
							}
						});
					} else {
						var query = new Parse.Query(Section);
						query.equalTo("objectId", result.toJSON().superSection.objectId);
						query.find({
							success: function(results) {
								results[0].add("subsections", result);
								results[0].save();
							},
							error: function(error) {
								console.log("Failed to retrieve tour");
								console.log(error);
							}
						});

					}

					res.status(201).send(result);
				}
				else
					res.status(result.status).send(result.data);
			});
		}
	},

	PUT: function(req, res) {
		console.log("PUT SECTION:\n", req.body);
		var data = req.body;
		var id = req.params.id;

		var expectedInput = {
			"title": "",
			"description": "",
			"tour": "",
			"superSection": ""
		};

		var validInput = validate.validateInput(data, expectedInput);
		var parseData = validate.parseData(data, expectedInput);
		console.log("Parsed Data: ", parseData);

		var query = new Parse.Query(Section);
		query.get(id, {
			success: function(section) {
				console.log("Section " + id + " retrieved succesfully");
				for (var prop in parseData) {
					section.set(prop.toString(), parseData[prop]);
				}
				section.save(null, {
					success: function(section) {
						console.log("Section " + id + " updated succesfully");
						res.status(200).send(section);
					},
					error:  function(section, error) {
						console.log("Failed to update section " + id);
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
		console.log("DELETE SECTION");
		var id = req.params.id;
		var query = new Parse.Query(Section);
		query.get(id, {
			success: function(section) {
				console.log("Section " + id + " retrieved succesfully");
				section.destroy({
					success: function(section) {
						console.log("Deleted section " + id);
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

function createSection (data, callback) {

	var section = new Section();
	var tourID = data.tour;
	var superSectionID = data.superSection;

	if (tourID.length !== 0 && data.superSection.length !== 0) {
		callback({status: 400, data: {"error": "Cannot attach to a tour AND supersection, pick one."}});
	} else {
		delete data.tour;
		delete data.superSection;

		if (tourID.length !== 0) {
			section.set("tour",  {"__type":"Pointer","className":"Tour","objectId":tourID});
			section.set("superSection",  {"__type":"Pointer","className":"Section","objectId":null});
		} else if (superSectionID.length !== 0) {
			section.set("tour",  {"__type":"Pointer","className":"Tour","objectId":null});
			section.set("superSection",  {"__type":"Pointer","className":"Section","objectId":superSectionID});
		}

		section.save(data, {
			success: function(section) {
				console.log("Created section with ID " + section.id + " at time " + section.createdAt);
				console.log(section);
				callback(section);
			},
			error: function(section, error) {
				console.log("Failed to create section.");
				console.log("Error: ", error);
				callback({status: 500, data: error});
			}
		});
	}
}

module.exports = section;
