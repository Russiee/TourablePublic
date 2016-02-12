var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Tour = Parse.Object.extend("Tour");

var bundle = {

	GET: function (req, res) {
		console.log("GET BUNDLE " + req.params.id);
		var id = req.params.id;
		var query = new Parse.Query(Tour);
        query.include("sections");
        query.include("sections.pois");
        query.include("sections.subsections");
//        query.include("subsections.pois");
		query.get(id, {
			success: function(tour) {
				console.log("Tour " + id + " retrieved succesfully");
                var package = tour.toJSON();
                
                //Remove metadata for client-side
                delete package.admin, delete package.keys, delete package.isPublic;
				res.status(200).send(package);
			},
			error: function(object, error) {
				console.log("Error retrieving " + id);
				res.send(404);
			}
		});
	}
}

function createBundle() {

}

module.exports = bundle;
