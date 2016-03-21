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
        }
    };
});
