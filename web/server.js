var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var path = require('path');

var basicAuth = require('basic-auth');

var auth = function (req, res, next) {
    function unauthorized(res) {
        res.set('WWW-Authenticate', 'Basic realm=Authorization Required');
        return res.send(401);
    };

    var user = basicAuth(req);

    if (!user || !user.name || !user.pass) {
        return unauthorized(res);
    };

    if (user.name === 'whenimakeapullrequest' && user.pass === 'iwillrebaseintomaster') {
        return next();
    } else {
        return unauthorized(res);
    };
};

app.use(express.static(__dirname + '/client'));

app.route('*')
	.get(auth, function(req, res) {
		res.sendFile(path.resolve('client/index.html'));
	});

app.use(express.static(__dirname + '/public'));

var port = process.env.PORT || 3000;

app.listen(port, function () {
	console.log("We're live on port " + port);
});
