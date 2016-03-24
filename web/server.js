var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var path = require('path');

app.use("/client", express.static(__dirname + '/client'));

var aws = require('./aws/aws.js');

app.get('/api/s3Policy', aws.getS3Policy);

app.route('*')
    .get(function(req, res) {
        res.sendFile(path.resolve('client/index.html'));
    });

app.set('trust proxy', true);

var port = process.env.PORT || 3000;

app.listen(port, function () {
    console.log("We're live on port " + port);
});
