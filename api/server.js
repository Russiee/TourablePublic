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

//requiring the routes

var router = express.Router();
var poi = require('./routes/poi.js');
var admin = require('./routes/admin.js');
var section = require('./routes/section.js');
var tour = require('./routes/tour.js');
var organization = require('./routes/organization.js');



//Route to test if API is working
router.get('/', function(req, res) {
	res.json({ message: 'This is the Hobbyte API', status: 200 });
});

//Organization routes
router.get('/v1/organization/:id', organization.GET);
router.put('/v1/organization/:id',organization.PUT);
router.delete('/v1/organization/:id',organization.DELETE);

//Admin routes
router.get('/v1/admin/:id', admin.GET);
router.post('/v1/admin/',admin.POST);
router.put('/v1/admin/:id',admin.PUT);
router.delete('/v1/admin/:id',admin.DELETE);

//Tour routes
router.get('/v1/tour/:id',tour.GET);
router.post('/v1/tour/', tour.POST);
router.put('/v1/tour/:id', tour.PUT);
router.delete('/v1/tour/:id', tour.DELETE);

//Section routes
router.get('/v1/section/:id', section.GET);
router.post('v1/section/', section.POST);
router.put('/v1/section/:id', section.PUT);
router.delete('/v1/section/:id', section.DELETE);

//Key routes
router.get('/v1/key/:id', key.GET);
router.post('/v1/key/',key.POST);
router.put('/v1/key/:id', key.PUT);
router.delete('/v1/key/:id', key.DELETE);



//POI routes
router.get('/v1/poi/:id', poi.GET);
router.post('/v1/poi/', poi.POST);
router.put('/v1/poi/:id', poi.PUT);
router.delete('/v1/poi/:id', poi.DELETE);





app.use('/api', router);

app.listen(port);
console.log("We're live on port " + port);
