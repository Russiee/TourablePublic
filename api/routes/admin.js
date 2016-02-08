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

	POST: function(req, res) {


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

		res.sendStatus(200);
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
