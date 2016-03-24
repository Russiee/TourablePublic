tourable.factory('createFactory', function($http, $q) {
    return {
        create: function(className, data) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'POST',
                    url: 'https://api.tourable.org/api/v1/' + className,
                    data: data
                }).then(function success(response) {
                    console.log(response);
                    resolve (response);
                    //this callback will be called asynchronously
                    //when the response is available
                }, function error(response) {
                    console.log("error", response);
                    reject (response);
                    //called asynchronously if an error occurs
                    //or server returns response with an error status.
                });
            });
        },
        upload: function(file) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'http://' + location.host + '/s3'
                }).then(function success(response) {
                    console.log(response);

                    file.upload = Upload.upload({
                    url: 'https://s3-eu-west-1.amazonaws.com/tourable-media/', //S3 upload url including bucket name
                    method: 'POST',
                    data: {
                            key: file.name, // the key to store the file on S3, could be file name or customized
                            AWSAccessKeyId: 'AKIAIPMBEGVV5SYPVQCQ',
                            acl: 'public', // sets the access to the uploaded file in the bucket: private, public-read, ...
                            policy: response.body[1], // base64-encoded json policy (see article below)
                            signature:  response.body[0], // base64-encoded signature based on policy string (see article below)
                            "Content-Type": file.type != '' ? file.type : 'application/octet-stream', // content type of the file (NotEmpty)
                            filename: file.name, // this is needed for Flash polyfill IE8-9
                            file: file
                        }
                    });

                    resolve (file.upload);
                    //this callback will be called asynchronously
                    //when the response is available
                }, function error(response) {
                    console.log("error", response);
                    reject (response);
                    //called asynchronously if an error occurs
                    //or server returns response with an error status.
                });
            });
        }
    };
});
