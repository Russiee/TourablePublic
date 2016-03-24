//This code is modfified open-source software (MIT license)
//credit goes to:
//https://github.com/nukulb/s3-angular-file-upload
//license: https://github.com/nukulb/s3-angular-file-upload/blob/master/LICENSE

'use strict';

var AWS = require('aws-sdk'),
    crypto = require('crypto'),
    config = {
        "accessKeyId": "AKIAI5IGIHPQHXO6TOFQ",
        "secretAccessKey": "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",
        "region": "eu-west-1",
        "bucket": "tourable-media"
    }

    createS3Policy,
    getExpiryTime;

function getExpiryTime () {
    var _date = new Date();
    return '' + (_date.getFullYear()) + '-' + (_date.getMonth() + 1) + '-' +
        (_date.getDate() + 1) + 'T' + (_date.getHours() + 3) + ':' + '00:00.000Z';
};

function createS3Policy (contentType, key, callback) {
    var date = new Date();
    var s3Policy = {
        'expiration': getExpiryTime(),
        'conditions': [
            ['starts-with', '$key', key],
            {'bucket': config.bucket},
            {'acl': 'public-read'},
            ['starts-with', '$Content-Type', contentType],
            {'success_action_status' : '201'}
        ]
    };

    // stringify and encode the policy
    var stringPolicy = JSON.stringify(s3Policy);
    var base64Policy = new Buffer(stringPolicy, 'utf-8').toString('base64');

    // sign the base64 encoded policy
    var signature = crypto.createHmac('sha1', config.secretAccessKey)
                        .update(new Buffer(base64Policy, 'utf-8')).digest('base64');

    // build the results object
    var s3Credentials = {
        s3Policy: base64Policy,
        s3Signature: signature,
        AWSAccessKeyId: config.accessKeyId
    };

    // send it back
    callback(s3Credentials);
};

exports.getS3Policy = function(req, res) {
    createS3Policy(req.query.mimeType, req.query.key, function (creds, err) {
        if (!err) {
            return res.status(200).send(creds);
        } else {
            return res.status(500).send(err);
        }
    });
};
