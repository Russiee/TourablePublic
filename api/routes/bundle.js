var async = require('async');
var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Tour = Parse.Object.extend("Tour");
var Section = Parse.Object.extend("Section");
var POI = Parse.Object.extend("POI");

process.on('uncaughtException', function (err) {
  console.log(err);
})

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
	var POIs;

	async.series([
		function(done) {
			console.log("Get all sections in the tour");
			var query = new Parse.Query(Section);
			query.limit(-1);
			query.equalTo("tour", {"__type":"Pointer","className":"Tour","objectId":package.objectId})
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
		function(done) {
			console.log("Find max depth")

			var maxDepth = 0;
			for (var i = 0; i < allSections.length; i++) {
				if (allSections[i].depth > maxDepth)
					maxDepth = allSections[i].depth
			}
			console.log(maxDepth);

			console.log("Build section hierachy");

			for (var j = maxDepth - 1; j >= 0; j--) {
				for (var k = 0; k < allSections.length; k++) {
					if (allSections[k].depth === j) {
						for (var l = 0; l < allSections.length; l++) {
							if (l != k && allSections[l].superSection && allSections[l].superSection.objectId === allSections[k].objectId) {
								if (allSections[k].subsections) {
									var temp = allSections[l];
									delete temp.tour, delete temp.superSection;
									allSections[k].subsections.push(temp);
								}
								else {
									var temp = allSections[l];
									delete temp.tour, delete temp.superSection;
									allSections[k].subsections = [];
									allSections[k].subsections.push(temp);
								}
							}
						}
					}
				}
			}

			console.log("Finished building section hierarchy")

			done();
		}, function(done) {
			console.log("Get all POIs in the tour");
			var query = new Parse.Query(POI);
			query.limit(-1);
//			query.equalTo("tour", {"__type":"Pointer","className":"Tour","objectId":package.objectId})
			query.find({
				success: function(results) {
					console.log(results.length + " POIs retrieved");
					var pois = JSON.parse(JSON.stringify(results));
					for (var i = 0; i < allSections.length; i++) {
						for (var j = 0; j < pois.length; j++) {
							console.log(i + " " + j);
							if (pois[j].section && pois[j].section.objectId === allSections[i].objectId) {
								if (allSections[i].pois) {
									delete pois[j].section;
									allSections[i].pois.push(pois[j]);
								} else {
									delete pois[j].section;
									allSections[i].pois = [];
									allSections[i].pois.push(pois[j]);
								}
							}
						}
					}
					console.log("done");
					done();
				},
				error: function(error) {
					console.log("Failed to retrieve POIs");

					console.log("done");
					done();
				}
			});
		},
		function(done) {
			console.log("Prep sections for bundle");

			for (var i = 0; i < allSections.length; i++) {
				if (allSections[i].superSection && allSections[i].superSection.objectId === 'null') {
					delete allSections[i].superSection, delete allSections[i].tour;
					sections.push(allSections[i]);
				}
			}

			done();
		},
//		function(done) {
//			console.log("Get layer with biggest depth");
//			var layer = [];
//			for (var i = 0; i < allSections.length; i++) {
//				var id = allSections[i].objectId;
//				var isLowestLevel = true;
//				for (var j = 0; j < allSections.length; j++) {
//					if (i !== j) {
//						if (allSections[j].superSection.objectId === id) {
//							isLowestLevel = false;
//						}
//					}
//				}
//				if (isLowestLevel) {
//					layer.push(allSections[i]);
//				}
//			}
//			console.log("done");
//			layers.push(layer);
//			done();
//		},
//		function(done) {
//			var currentLayer = [];
//			var nextLayer = layers[0];
//
//			var finished = false;
//
//			while (!finished) {
//				currentLayer = nextLayer;
//				nextLayer = [];
//
//				for (var i = 0; i < currentLayer.length; i++) {
//					for (var j = 0; j < allSections.length; j++) {
//						if (currentLayer[i].objectId !== allSections[j].objectId) {
//							if (currentLayer[i].superSection.objectId === allSections[j].objectId) {
//								nextLayer.push(allSections[j]);
//							}
//						}
//					}
//				}
//
//				var allTopLayer = true;
//				for (var k = 0; k < nextLayer.length; k++) {
//					if (nextLaye r[k].superSection.objectId !== null) {
//						allTopLayer = false;
//					}
//				}
//				if (allTopLayer) {
//					finished = true;
//				} else {
//					console.log("nextLayer ", nextLayer);
//					layers.push(nextLayer);
//				}
//			}
//
//			console.log("allLayers", layers)
//			done();
//		},
//		function(done) {
//
//			for (var i = layers.length - 1; i > 0; i--) {
//				for (var j = 0; j < layers[i].length; j++) {
//
//
//
//				}
//			}
//
//			done();
//		},
//		function(done){
//			console.log("select top level sections");
//			for (var i = 0; i < allSections.length; i++) {
//				console.log(allSections[i].tour.objectId);
//				if (allSections[i].tour.objectId === package.objectId) {
//					sections.push(allSections[i]);
//				} else if (allSections[i].tour.objectId === "null") {
//					subsections.push(allSections[i]);
//				}
//				if (counter === allSections.length - 1) {
//					console.log("done");
//					done();
//				}
//			}
//		},
//		function(done){
//			console.log("connect section hierachy");
//
//			var finished = false;
//
//			var currentLayer = sections;
//			var nextLayer = [];
//
//			while (!finished) {
//				var count = 0;
//				for (var i = 0; i < subsections.length; i++) {
//					for (var j    = 0; j < subsections.length; j++) {
//						if (subsections[i].objectId === subsections[j].superSection.objectId) {
//							count++;
//						}
//					}
//				}
//
//				if (count === 0) {
//					console.log("Count is 0");
//					finished = true;
//				} else {
//					console.log("Count is " + count);
//				}
//			}

//			expandSubSections(sections);

//			var finished = true;
//
//			function expandSubSections (array) {
//				finished = false;
//				console.log ("expanding ");
//				for (var i = 0; i < array.length; i++) {
//					for (var j = 0; j < array[i].subsections.length; j++) {
//						for (var k = 0; k < subsections.length; k++) {
//							console.log("Checking " + i + ", " + j + ", " + k);
//							if (array[i].subsections[j].objectId === subsections[k].objectId) {
//								array[i].subsections[j] = subsections[k];
//								subsections.splice(k, 1);
//								expandSubSections(array[i].subsections[j]);
//							}
//						}
//					}
//				}
//			}
//
//			var intervalId = setInterval(function() {
//				console.log("checking if done");
//				if (finished) {
//					console.log("done")
//					clearInterval(intervalId);
//					done()
//				}
//				else {
//					console.log("not done")
//					finished = true;
//				}
//			}, 500)
//		},
		function(done){
			console.log(sections);
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
