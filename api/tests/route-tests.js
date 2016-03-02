var should = require('should'); 
var assert = require('assert');
var request = require('supertest');
var organizationTest = require('../tests/organization-test.js');
var tourTest = require('../tests/tour-test.js');
var adminTest = require('../tests/admin-test.js');
var poiTest = require('../tests/poi-test.js');
var keyTest = require('../tests/key-test.js')
var sectionTest = require('../tests/section-test.js')


describe('Route tests', function() {
var organizationObjID;
var sectionObjID;
var poiObjID;
var adminObjID;
var tourObjID;
var keyObjID;


var url = 'http://touring-api.herokuapp.com/';
this.timeout(10000);


    //begin adding organization
    it('Should correctly add an organization', function(done){
        organizationTest.POST(url, function(returnedID){
            organizationObjID = returnedID;
            done();
        });        
    })
    
    it('Should correctly get the added organization', function(done){
        organizationTest.GET1(organizationObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly update the added organization', function(done){
        organizationTest.PUT(organizationObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly get the updated organization', function(done){
        organizationTest.GET2(organizationObjID, url, function(){
            done();
        });
    })
    
    //Begin adding admin
    
    it('Should correctly add the admin pointing to the test organization', function(done){
        adminTest.POST(organizationObjID, url, function(returnID){
            adminObjID = returnID;
            done();
        });        
    })
    
    it('Should correctly get the added admin', function(done){
        adminTest.GET1(adminObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly update the added admin', function(done){
        adminTest.PUT(organizationObjID, adminObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly get the added admin', function(done){
        adminTest.GET2(adminObjID, url, function(){
            done();
        });
    })
    
    //Begin adding tour
    it('Should correctly add a tour pointing to the admin', function(done){
        tourTest.POST(adminObjID, url, function(returnID){
            tourObjID = returnID;
            done();
        });
    })
    
    it('Should correctly get the added tour', function(done){
        tourTest.GET1(tourObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly update the added tour', function(done){
        tourTest.PUT(adminObjID, tourObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly get the updated tour', function(done){
        tourTest.GET2(tourObjID, url, function(){
            done();
        });
    })
    
    //Begin adding section
    
    it('Should correctly add a section pointing to the tour', function(done){
        sectionTest.POST(tourObjID, url, function(returnID){
            sectionObjID = returnID;
            done();
        });
    })
    
    it('Should correctly get the added section', function(done){
        sectionTest.GET1(sectionObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly update the added section', function(done){
        sectionTest.PUT(tourObjID, sectionObjID, url, function(){
            done();
        });
    })
    
    it('Should correctly get the updated section', function(done){
        sectionTest.GET2(sectionObjID, url, function(){
            done();
        });
    })
    
    //Begin adding POI
    
    it('should correctly add a POI', function(done){
        poiTest.POST(sectionObjID, url, function(returnedID){
            poiObjID = returnedID;
            done();            
        });
    });
    
    it('should correctly get added POI', function(done){
        poiTest.GET1(poiObjID, url, function(){
            done();
        });
    })
    
    it('should correctly update the added poi', function(done){
        poiTest.PUT(sectionObjID, poiObjID, url, function(){
            done();
        });
    })
    
    it('should correctly get added POI', function(done){
        poiTest.GET2(poiObjID, url, function(){
            done();
        });
    })
    
    //Begin adding a key 
    
    it('should correctly add a key', function(done){
        keyTest.POST(tourObjID, url, function(returnedID){
            keyObjID = returnedID;
            done();            
        });
    });
    
    it('should correctly get added key', function(done){
        keyTest.GET1(keyObjID, url, function(){
            done();
        });
    })
    
    it('should correctly update the added key', function(done){
        keyTest.PUT(keyObjID, url, function(){
            done();
        });
    })
    
    it('should correctly get added key', function(done){
        keyTest.GET2(keyObjID, url, function(){
            done();
        });
    })
    
    //Begin deleting from bottom up, and checking they are deleted
        
    it('should correctly delete the POI', function(done){
        poiTest.DELETE(poiObjID, url, function(){
            done();
        });
    })
    
    it('should get null for deleted POI', function(done){
        poiTest.GET3(poiObjID, url, function(){
            done();
        })
    })
    
    //Begin deleting section
    
    it('should correctly delete the section', function(done){
        sectionTest.DELETE(sectionObjID, url, function(){
            done();
        });
    })
    
    it('should get null for deleted section', function(done){
        sectionTest.GET3(sectionObjID, url, function(){
            done();
        })
    })
    
    //Begin deleting tour
    
    it('should correctly delete the tour', function(done){
        tourTest.DELETE(tourObjID, url, function(){
            done();
        });
    })
    
    it('should get null for deleted tour', function(done){
        tourTest.GET3(tourObjID, url, function(){
            done();
        })
    })
    
    //Begin deleting admin
    
    it('should correctly delete the admin', function(done){
        adminTest.DELETE(adminObjID, url, function(){
            done();
        });
    })
    
    it('should get null for deleted admin', function(done){
        adminTest.GET3(adminObjID, url, function(){
            done();
        });
    })
    
    //Begin deleting key
    
    it('should correctly delete the key', function(done){
        keyTest.DELETE(keyObjID, url, function(){
            done();
        });
    })
    
    it('should get null for deleted key', function(done){
        keyTest.GET3(keyObjID, url, function(){
            done();
        });
    })
    
    //Begin deleting Organization
    
    it('should correctly delete the Organization', function(done){
        organizationTest.DELETE(organizationObjID, url, function(){
            done();
        });
    })
    
    it('should get null for deleted Organization', function(done){
        organizationTest.GET3(organizationObjID, url, function(){
            done();
        });
    })
});
    
    
    
    
    
    