var validate = require('./validate.js');
var admin = {

	GET: function(req, res) {
		var id = req.params.id;
		//example date string (ISO 8601)
		//2012-04-23T18:25:43.511Z
		var mockData = {
			id: "8EDFA1BF",
			createdAt: "2016-02-08T11:11:36Z",
			updatedAt: new Date(),
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

		if (id === "8EDFA1BF") {
			res.send(mockData);
		} else {
			res.send(404);
		}
	},

//	POST: function(req, res) {


//		var Test = Parse.Object.extend("Test");
//		//var test = new Test();
//		//test.save();
//		var query = new Parse.Query(Test);
//		query.find({
//		  success: function(results) {
//			console.log("Successfully retrieved " + results.length + " tests.");
//		  },
//		  error: function(error) {
//			console.log("Error: " + error.code + " " + error.message);
//		  }
//		});

//		res.sendStatus(200);
//	},
    
   	POST: function(req, res) {
		console.log("POST ADMIN:",req.body);
		var data = req.body;

		var expectedInput = {
			id: "",
			name: "",
			email: "",
			password: "",
			organization: "",
			tours: [],
            isSuper: ""
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

module.exports = admin;
