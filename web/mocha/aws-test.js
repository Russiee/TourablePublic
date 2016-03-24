var should = require('should');
var assert = require('assert');
var request = require('supertest');

describe('AWS Test', function() {

    //Tests time out after 10s (not the default of 2s), allowing for the database to 'wake up'
    this.timeout(10000);

    var server;

    beforeEach(function () {
        server = require('../server.js');
    });
    afterEach(function () {
        server.close();
    });

    it('AWS responds', function (done) {
        request(server)
            .get('/api/s3Policy?mimeType=image/png&key=/')
            .expect(200, done);
    });


});






