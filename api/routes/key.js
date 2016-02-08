var validate = require('./validate.js');
var key = {

    GET: function(req, res) {
		var id = req.params.id;
		//example date string (ISO 8601)
		//2012-04-23T18:25:43.511Z
		var mockData = {
			id: "7DACX13",
			createdAt: "2016-02-08T11:11:36Z",
			updatedAt: new Date(),
            code: "RBH-1251",
			title: "Sample Title",
			post: {
				content: [
					"Text text text text",
					"imageurl",
					"text tesxasdasd;flkj a;sldkfja;sldf",
					"videourl",
					"Text text text text"
				]
			}
		}

		if (id === "7DACX13") {
			res.send(mockData);
		} else {
			res.send(404);
		}
	},

	POST: function(req, res) {
		console.log("POST KEY:",req.body);
		var data = req.body;

		var expectedInput = {
			code: "",
			tour: "",
			expiresAt: ""
        };

		var validInput = validate.validateInput(data, expectedInput);

		console.log(validInput);

		if (validInput)
			res.sendStatus(200);
		else
			res.sendStatus(400);
	},

	PUT: function(req, res) {
		var id = req.params.id;
		var data = req.body;

		var expectedInput = {
			code: "",
			tour: "",
			expiresAt: ""
        };

		var validInput = validate.validateInput(data, expectedInput);

		console.log(validInput);

		if (validInput)
			res.sendStatus(200);
		else
			res.sendStatus(400);
	},

	DELETE: function(req, res) {
		var id = req.params.id;
	}
}

module.exports = key;
