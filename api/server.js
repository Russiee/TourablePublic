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
var key = require('./routes/key.js');
var admin = require('./routes/admin.js');
var section = require('./routes/section.js');
var tour = require('./routes/tour.js');
var organization = require('./routes/organization.js');
var key = require('./routes/key.js');



//Route to test if API is working
router.get('/', function(req, res) {
	res.json({ message: 'This is the Hobbyte API', status: 200 });
});

//Organization routes
router.get('/v1/organization/:id', organization.GET);
router.get('/v1/organizations', organization.GET_ALL);
router.post('/v1/organization', organization.POST);
router.put('/v1/organization/:id', organization.PUT);
router.delete('/v1/organization/:id', organization.DELETE);

//Admin routes
router.get('/v1/admin/:id', admin.GET);
router.get('/v1/admins', admin.GET_ALL);
router.post('/v1/admin/', admin.POST);
router.put('/v1/admin/:id', admin.PUT);
router.delete('/v1/admin/:id', admin.DELETE);

//Tour routes
router.get('/v1/tour/:id',tour.GET);
router.get('/v1/tours', tour.GET_ALL);
router.post('/v1/tour/', tour.POST);
router.put('/v1/tour/:id', tour.PUT);
router.delete('/v1/tour/:id', tour.DELETE);

//Section routes
router.get('/v1/section/:id', section.GET);
router.get('/v1/sections', section.GET_ALL);
router.post('v1/section/', section.POST);
router.put('/v1/section/:id', section.PUT);
router.delete('/v1/section/:id', section.DELETE);

//Key routes
router.get('/v1/key/:id', key.GET);
router.get('/v1/keys', key.GET_ALL);
router.post('/v1/key/', key.POST);
router.put('/v1/key/:id', key.PUT);
router.delete('/v1/key/:id', key.DELETE);
//router.post('/v1/validate/:key', key.validateKey);

//POI routes
router.get('/v1/poi/:id', poi.GET);
router.post('/v1/poi/', poi.POST);
router.put('/v1/poi/:id', poi.PUT);
router.delete('/v1/poi/:id', poi.DELETE);





app.use('/api', router);

app.listen(port);
console.log("We're live on port " + port);
