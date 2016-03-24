var express = require('express');
var app = express();
var bodyParser = require('body-parser');
var path = require('path');

app.use("/client", express.static(__dirname + '/client'));

var aws = require('./aws/aws.js');

app.get('/api/s3Policy', aws.getS3Policy);

//app.get('/s3', function(req, res) {

//
//var CryptoJS = require("crypto-js");
//
//var accessKeyID = "AKIAI5IGIHPQHXO6TOFQ";
//var secretAccessKey = "IKx+X4KeHfMvYAZux2KXzXxuPiSH2SJ06m3VGPyi";
//
//var bucket = "tourable-media";
//var region = "eu-west-1"; // overwrite with your region
//var folder = "/"; // overwrite with your folder
//var expiration = "2017-09-28T12:00:00.000Z"; // overwrite date
//var date = Date.now();
//var serviceName = "s3";
//
//function getSignatureKey(key, dateStamp, regionName, serviceName) {
//   var kDate = CryptoJS.HmacSHA256(dateStamp, "AWS4" + key);
//   var kRegion = CryptoJS.HmacSHA256(regionName, kDate);
//   var kService = CryptoJS.HmacSHA256(serviceName, kRegion);
//   var kSigning = CryptoJS.HmacSHA256("aws4_request", kService);
//
//   return kSigning;
//}
//
//var s3Policy = {"expiration": expiration,
//  "conditions": [
//   {"bucket": bucket},
//   ["starts-with", "$key", folder],
//   {"acl": "public-read"},
//   ["starts-with", "$Content-Type", "image/"],
//   {"x-amz-meta-uuid": "14365123651274"},
//   ["starts-with", "$x-amz-meta-tag", ""],
//   {"x-amz-credential": accessKeyID + "/" + date + "/" + region + "/" + serviceName +"/aws4_request"},
//   {"x-amz-algorithm": "AWS4-HMAC-SHA256"},
//   {"x-amz-date": date + "T000000Z" }
//  ]
//};
//
//var base64Policy = new Buffer(JSON.stringify(s3Policy), "utf-8").toString("base64");
//console.log('base64Policy:', base64Policy);
//
//var signatureKey = getSignatureKey(secretAccessKey, date, region, serviceName);
//var s3Signature = CryptoJS.HmacSHA256(base64Policy, signatureKey).toString(CryptoJS.enc.Hex);
//console.log('s3Signature:', s3Signature);
//
//    res.send(
//        [
//            s3Signature,
//            base64Policy
//        ]
//    );
//
//});

app.route('*')
    .get(function(req, res) {
        res.sendFile(path.resolve('client/index.html'));
    });

app.set('trust proxy', true);

var port = process.env.PORT || 3000;

app.listen(port, function () {
    console.log("We're live on port " + port);
});
