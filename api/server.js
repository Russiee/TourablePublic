var express = require('express');
var app = express();
var bodyParser = require('body-parser');

//If we use Parse
//var Parse = require('parse/node').Parse;
//Parse.initialize();


//Allow cross origin requests
app.use(function(req, res, next) {
    //console.log("Running CORS middlesware");
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
});

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

//Serve static files
app.use(express.static(__dirname + '/public'));

var port = process.env.PORT || 3000;

var router = express.Router();

//Route to test if API is working
router.get('/', function(req, res) {
    res.json({ message: 'this is the api' });
});

//example route
router.route('/example')

    //example POST route
    .post(function(req, res) {

        var data = JSON.parse(JSON.stringify(req.body));

        if (data)
            res.send(200)
        else
            res.send(400)
    })

    .get(function(req, res) {

        var data = {
            example: "data"
        }

        res.send(data);
    });

app.use('/api', router);

app.listen(port);
console.log("We're live on port " + port);
