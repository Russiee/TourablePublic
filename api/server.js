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
var examples = require('./routes/examples.js');
var poi = require('./routes/poi.js');
//Route to test if API is working
router.get('/', function(req, res) {
	res.json({ message: 'This is the Hobbyte API', status: 200 });
});

//POI routes
router.get('/v1/poi/:id', poi.GET);
router.post('/v1/poi/', poi.POST);
router.put('/v1/poi/:id', poi.PUT);
router.delete('/v1/poi/:id', poi.DELETE);



app.use('/api', router);

app.listen(port);
console.log("We're live on port " + port);
