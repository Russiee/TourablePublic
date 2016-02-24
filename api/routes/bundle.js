var async = require('async');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Tour = Parse.Object.extend("Tour");
var Section = Parse.Object.extend("Section");
var POI = Parse.Object.extend("POI");

var bundle = {

	GET: function (req, res) {
		console.log("GET BUNDLE " + req.params.id);
		var id = req.params.id;
		var query = new Parse.Query(Tour);
//		query.include("sections");
//		query.include("sections.pois");
//		query.include("sections.subsections");
//      query.include("subsections.pois");
		query.get(id, {
			success: function(tour) {
				console.log("Tour " + id + " retrieved succesfully");
				var package = tour.toJSON();
				createBundle(package, function(response) {
					res.status(200).send(response);
				});
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	}
}

function createBundle(data, mainCallback) {
	var package = data;
	var allSections = [];
	var sections = [];
	var subsections = [];

	async.series([
		function(done){
			console.log("Get all sections");
			var query = new Parse.Query(Section);
			query.limit(-1);
			query.find({
				success: function(results) {
					console.log(results.length + " sections retrieved");
					allSections = JSON.parse(JSON.stringify(results));
					console.log("done");
					done();
				},
				error: function(error) {
					console.log("Failed to retrieve sections");
					console.log(error);
					console.log("done");
					done();
				}
			});

		},
		function(done){
			console.log("select relevant sections");
			var counter = 0;
			for (var i = 0; i < allSections.length; i++, counter++) {
				console.log(allSections[i].tour.objectId);
				if (allSections[i].tour.objectId === package.objectId) {
					sections.push(allSections[i]);
				} else if (allSections[i].tour.objectId === "null") {
					subsections.push(allSections[i]);
				}
				if (counter === allSections.length - 1) {
					console.log("done");
					done();
				}
			}
		},
		function(done){
			console.log("connect section hierachy");

			expandSubSections(sections);

			var finished = true;

			function expandSubSections (array) {
				finished = false;
				console.log ("expanding ");
				for (var i = 0; i < array.length; i++) {
					for (var j = 0; j < array[i].subsections.length; j++) {
						for (var k = 0; k < subsections.length; k++) {
							console.log("Checking " + i + ", " + j + ", " + k);
							if (array[i].subsections[j].objectId === subsections[k].objectId) {
								array[i].subsections[j] = subsections[k];
								subsections.splice(k, 1);
								expandSubSections(array[i].subsections[j]);
							}
						}
					}
				}
			}

			var intervalId = setInterval(function() {
				console.log("checking if done");
				if (finished) {
					console.log("done")
					clearInterval(intervalId);
					done()
				}
				else {
					console.log("not done")
					finished = true;
				}
			}, 500)
		},
		function(done){
//			console.log(sections);
			package.sections = sections;
			console.log("Remove Metadata");
			delete package.admin, delete package.keys, delete package.isPublic;
			done();
		},
		function(){
			mainCallback(package);
		}
	]);
}




//function createBundle(data, mainCallback) {
//	//Remove metadata for client-side
//
//	var package = data;
//	var done = false;
//
//	var totalSections = package.sections.length;
//	var counter = 0;
//
//	for (var i = 0; i < package.sections.length; i++) {
//		getSectionWithChildren(package.sections[i].objectId);
//	}
//
//	function getSectionWithChildren(id) {
//		var query = new Parse.Query(Section);
//		query.get(id, {
//			success: function(section) {
//				var data = section.toJSON();
//				console.log("Section " + data.objectId + " retrieved.");
//				delete data.tour, delete data.superSection;
//				package.sections[counter] = data;
//
//				totalSections +=
//				counter++;
//				console.log("total: " + totalSections + " counter:" + counter);
//				if (counter === totalSections)
//					done();
//			},
//			error: function(section, error) {
//				console.log("Error retrieving section" + section.objectId);
//				done = true;
//				mainCallback({"error": "failed to retrieve a section"});
//			}
//		});
//	}
//
//	setInterval(function() {
//		console.log("check");
//		if (done)
//			mainCallback(package);
//	}, 200);

//	async.series([
//		function(done){
//			var totalSections = package.sections.length;
//			var counter = 0;
//			for (var i = 0; i < package.sections.length; i++) {
//				getSectionWithChildren(package.sections[i].objectId,  function (add) {
//					totalSections += add;
//					counter++;
//					console.log("total: " + totalSections + " counter:" + counter);
//					if (counter === totalSections)
//						done();
//				});
//			}
//		},
//		function(done){
//			console.log("hello");
//			delete package.admin, delete package.keys, delete package.isPublic;
//			done();
//		},
//		function(){
//			mainCallback(package);
//		}
//	]);
//}

//function getSectionWithChildren(id, callback) {
//	var counter = 0;
//	//save i as counter to avoid async problems
//	var query = new Parse.Query(Section);
//	query.get(id, {
//		success: function(section) {
//			var data = section.toJSON();
//			console.log("Section " + data.objectId + " retrieved.");
//			delete data.tour, delete data.superSection;
//			package.sections[counter] = data;
//			callback();
//		},
//		error: function(section, error) {
//			console.log("Error retrieving section" + section.objectId);
//			callback(0);
//
//		}
//	});
//}



module.exports = bundle;
