var express = require('express');
var app = express();
var bodyParser = require('body-parser');

var Parse = require('parse/node').Parse;
Parse.initialize("touring", "yF85llv84OI0NV41ieaHU7PM0oyRCMLT");
Parse.serverURL = 'http://touring-db.herokuapp.com/parse';

//var Test = Parse.Object.extend("Test");
////var test = new Test();
////test.save();
//var query = new Parse.Query(Test);
//query.find({
//  success: function(results) {
//	console.log("Successfully retrieved " + results.length + " tests.");
//  },
//  error: function(error) {
//	console.log("Error: " + error.code + " " + error.message);
//  }
//});

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
router.post('/v1/organization', organization.POST);
router.put('/v1/organization/:id',organization.PUT);
router.delete('/v1/organization/:id',organization.DELETE);

//Admin routes
router.get('/v1/admin/:id');
router.post('/v1/admin/');
router.put('/v1/admin/:id');
router.delete('/v1/admin/:id');

//Tour routes
router.get('/v1/tour/:id');
router.post('/v1/tour/');
router.put('/v1/tour/:id');
router.delete('/v1/tour/:id');

//Section routes
router.get('/v1/section/:id');
router.post('v1/section/');
router.put('/v1/section/:id');
router.delete('/v1/section/:id');

//Key routes
router.get('/v1/key/:id');
router.post('/v1/key/');
router.put('/v1/key/:id');
router.delete('/v1/key/:id');

//POI routes
router.get('/v1/poi/:id', poi.GET);
router.post('/v1/poi/', poi.POST);
router.put('/v1/poi/:id', poi.PUT);
router.delete('/v1/poi/:id', poi.DELETE);





app.use('/api', router);

app.listen(port);
console.log("We're live on port " + port);
