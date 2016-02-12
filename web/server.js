var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var path = require('path');

app.use(express.static(__dirname + '/client'));

app.route('*')
	.get(function(req, res) {
		res.sendFile(path.resolve('client/index.html'));
	});

app.use(express.static(__dirname + '/public'));

var port = process.env.PORT || 3000;

app.listen(port, function () {
	console.log("We're live on port " + port);
});
