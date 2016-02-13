var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var path = require('path');
var wwwhisper = require('connect-wwwhisper');

app.use(wwwhisper());

app.use(express.static(__dirname + '/client'));

app.route('*')
	.get(function(req, res) {
		res.sendFile(path.resolve('client/index.html'));
	});

app.use(express.static(__dirname + '/public'));

var port = process.env.PORT || 3000;

var credentials = auth({ name: 'something', pass: 'whatever' })

  if (!credentials || credentials.name !== 'john' || credentials.pass !== 'secret') {
    res.statusCode = 401
    res.setHeader('WWW-Authenticate', 'Basic realm="example"')
    res.end('Access denied')
  } else {
    res.end('Access granted')
  }


app.listen(port, function () {
	console.log("We're live on port " + port);
});
