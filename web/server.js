var express = require('express');
var app = express();
var bodyParser = require('body-parser');

app.get('*', function(req, res) {
	res.sendFile(__dirname + '/client/index.html');
});

var port = process.env.PORT || 3000;

app.listen(port, function () {
	console.log("We're live on port " + port);
});
