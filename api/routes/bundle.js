var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';
var Tour = Parse.Object.extend("Tour");

var bundle = {

	GET: function (req, res) {
		console.log("GET BUNDLE " + req.params.id);
		var id = req.params.id;
		var query = new Parse.Query(Tour);
		query.get(id, {
			success: function(tour) {
				console.log("Tour " + id + " retrieved succesfully");
				res.status(200).send(tour);
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
