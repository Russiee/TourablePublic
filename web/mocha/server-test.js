var should = require('should');
var assert = require('assert');
var request = require('supertest');

describe('Server Test', function() {

    //Tests time out after 10s (not the default of 2s), allowing for the database to 'wake up'
    this.timeout(10000);

    var server;

    beforeEach(function () {
        server = require('../server.js');
    });
    afterEach(function () {
        server.close();
    });

    it('responds to /', function (done) {
        request(server)
            .get('/')
            .expect(200, done);
    });
});





