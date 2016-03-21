var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var path = require('path');

var basicAuth = require('basic-auth');

var auth = function (req, res, next) {
    function unauthorized(res) {
        res.set('WWW-Authenticate', 'Basic realm=Authorization Required');
        return res.sendStatus(401);
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

app.use("/client", express.static(__dirname + '/client'));

app.route('*')
    .get(auth, function(req, res) {
        res.sendFile(path.resolve('client/index.html'));
    });

function wwwRedirect(req, res, next) {
    if (req.headers.host.slice(0, 4) === 'www.') {
        var newHost = req.headers.host.slice(4);
        return res.redirect(301, req.protocol + '://' + newHost + req.originalUrl);
    }
    next();
};

app.set('trust proxy', true);
app.use(wwwRedirect);

var port = process.env.PORT || 3000;

app.listen(port, function () {
    console.log("We're live on port " + port);
});
