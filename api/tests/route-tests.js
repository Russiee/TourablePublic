Parse = require('parse/node').Parse;
Parse.initialize("touring-testing", "B9pOdZStXFqj48739yOO0B64MTtbv9Tf");
Parse.serverURL = "https://touring-db-testing.herokuapp.com/parse";

var should = require('should');
var assert = require('assert');
var request = require('supertest');
var organizationTest = require('../tests/organization-test.js');
var tourTest = require('../tests/tour-test.js');
var adminTest = require('../tests/admin-test.js');
var poiTest = require('../tests/poi-test.js');
var keyTest = require('../tests/key-test.js');
var sectionTest = require('../tests/section-test.js');
var bundleTest = require('../tests/bundle-test.js');

//Test suite for testing the organization, admin, key, tour, POI and section routes
//Tests consist of checking all functions of the routes
//Each route is tested by adding it to the API (POST function)
//The object is then queried (GET function)
//The object is then updated (PUT function)
//The object is then queried again to confirm update changes (GET function)
//The object is then deleted (DELETE function)
//The object is then queried a final time to confirm it no longer exists or is connected to other objects (GET function)

//The test suite is started by typing 'gulp tests' in the terminal when in the project directory

describe('Route tests', function() {

    var organizationObjID;
    var sectionObjID;
    var poiObjID;
    var adminObjID;
    var tourObjID;
    var keyObjID;

    //The source for all requests, the database url
    var url = 'http://touring-api-testing.herokuapp.com/';
    //Tests time out after 10s (not the default of 2s), allowing for the database to 'wake up'
    this.timeout(10000);

    var server;

    beforeEach(function () {
        server = require('../server.js');
    });
    afterEach(function () {
        server.close();
    });

    it('responds to /api', function (done) {
        request(server)
            .get('/api')
            .expect(200, done);
    });

    //begin adding organization
    it('Should correctly add an organization', function(done){
        organizationTest.POST(server, function(returnedID){
            organizationObjID = returnedID;
            done();
        });
    })

    it('Should correctly get the added organization', function(done){
        organizationTest.GET1(organizationObjID, server, function(){
            done();
        });
    })

    it('Should correctly get all organizations', function(done){
        organizationTest.GET_ALL(organizationObjID, server, function(){
            done();
        });
    })

    it('Should correctly update the added organization', function(done){
        organizationTest.PUT(organizationObjID, server, function(){
            done();
        });
    })

    it('Should correctly get the updated organization', function(done){
        organizationTest.GET2(organizationObjID, server, function(){
            done();
        });
    })

    //Begin adding admin

    it('Should correctly add the admin pointing to the test organization', function(done){
        adminTest.POST(organizationObjID, server, function(returnID){
            adminObjID = returnID;
            done();
        });
    })

    it('Should correctly get the added admin', function(done){
        adminTest.GET1(adminObjID, server, function(){
            done();
        });
    })

    it('Should correctly get all admins', function(done){
        adminTest.GET_ALL(adminObjID, server, function(){
            done();
        });
    })

    //Begin adding tour
    it('Should correctly add a tour pointing to the admin', function(done){
        tourTest.POST(adminObjID, server, function(returnID){
            tourObjID = returnID;
            done();
        });
    })

    it('Should correctly get the added tour', function(done){
        tourTest.GET1(tourObjID, server, function(){
            done();
        });
    })
    
    it('Should correctly get all tours', function(done){
        tourTest.GET_ALL(tourObjID, server, function(){
            done();
        });
    })

    it('Should correctly update the added tour', function(done){
        tourTest.PUT(adminObjID, tourObjID, server, function(){
            done();
        });
    })

    it('Should correctly get the updated tour', function(done){
        tourTest.GET2(tourObjID, server, function(){
            done();
        });
    })

    //Begin adding section

    it('Should correctly add a section pointing to the tour', function(done){
        sectionTest.POST(tourObjID, server, function(returnID){
            sectionObjID = returnID;
            done();
        });
    })

    it('Should correctly get the added section', function(done){
        sectionTest.GET1(sectionObjID, server, function(){
            done();
        });
    })
    
    it('Should correctly get all sections', function(done){
        sectionTest.GET_ALL(sectionObjID, server, function(){
            done();
        });
    })

    it('Should correctly update the added section', function(done){
        sectionTest.PUT(tourObjID, sectionObjID, server, function(){
            done();
        });
    })

    it('Should correctly get the updated section', function(done){
        sectionTest.GET2(sectionObjID, server, function(){
            done();
        });
    })

    //Begin adding POI

    it('should correctly add a POI', function(done){
        poiTest.POST(sectionObjID, server, function(returnedID){
            poiObjID = returnedID;
            done();
        });
    });

    it('should correctly get added POI', function(done){
        poiTest.GET1(poiObjID, server, function(){
            done();
        });
    })
    
    it('Should correctly get all POIs', function(done){
        poiTest.GET_ALL(poiObjID, server, function(){
            done();
        });
    })

    it('should correctly update the added poi', function(done){
        poiTest.PUT(sectionObjID, poiObjID, server, function(){
            done();
        });
    })

    it('should correctly get added POI', function(done){
        poiTest.GET2(poiObjID, server, function(){
            done();
        });
    })

    //Begin adding a key

    it('should correctly add a key', function(done){
        keyTest.POST(tourObjID, server, function(returnedID){
            keyObjID = returnedID;
            done();
        });
    });

    it('should correctly get added key', function(done){
        keyTest.GET1(keyObjID, server, function(){
            done();
        });
    })
    
    it('Should correctly get all keys', function(done){
        keyTest.GET_ALL(keyObjID, server, function(){
            done();
        });
    })

    it('should correctly update the added key', function(done){
        keyTest.PUT(tourObjID, keyObjID, server, function(){
            done();
        });
    })

    it('should correctly get added key', function(done){
        keyTest.GET2(keyObjID, server, function(){
            done();
        });
    })

    //Bundle test

    it('should correctly get the bundle of the tour', function(done){
        bundleTest.GET(tourObjID, server, function(){
            done();
        });
    })



    //Begin deleting from bottom up, and checking they are deleted

    it('should correctly delete the POI', function(done){
        poiTest.DELETE(poiObjID, server, function(){
            done();
        });
    })

    it('should get null for deleted POI', function(done){
        poiTest.GET3(poiObjID, server, function(){
            done();
        })
    })

    //Begin deleting section

    it('should correctly delete the section', function(done){
        sectionTest.DELETE(sectionObjID, server, function(){
            done();
        });
    })

    it('should get null for deleted section', function(done){
        sectionTest.GET3(sectionObjID, server, function(){
            done();
        })
    })

    //Begin deleting tour

    it('should correctly delete the tour', function(done){
        tourTest.DELETE(tourObjID, server, function(){
            done();
        });
    })

    it('should get null for deleted tour', function(done){
        tourTest.GET3(tourObjID, server, function(){
            done();
        })
    })

    //Begin deleting key

    it('should correctly delete the key', function(done){
        keyTest.DELETE(keyObjID, server, function(){
            done();
        });
    })

    it('should get null for deleted key', function(done){
        keyTest.GET3(keyObjID, server, function(){
            done();
        });
    })

    //Begin deleting Organization

    it('should correctly delete the Organization', function(done){
        organizationTest.DELETE(organizationObjID, server, function(){
            done();
        });
    })

    it('should get null for deleted Organization', function(done){
        organizationTest.GET3(organizationObjID, server, function(){
            done();
        });
    })
});





