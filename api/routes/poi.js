var organization = {

	GET: function(req, res) {
		var id = req.params.id;
	},

	POST: function(req, res) {
		var data = JSON.parse(JSON.stringify(req.body));
		if (data)
			res.send(200)
		else
			res.send(400)
	},

	PUT: function(req, res) {
		var id = req.params.id;
		var data = JSON.parse(JSON.stringify(req.body));
		if (data)
			res.send(200)
		else
			res.send(400)
	},

	DELETE: function(req, res) {
		var id = req.params.id;
	}
}

module.exports = organization;
