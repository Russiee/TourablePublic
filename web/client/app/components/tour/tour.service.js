tourable.factory('tourFactory', function($http, $q) {
    return {
        getTour: function(id) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/tour/' + id
                }).then(function success(response) {
                    console.log(response);
                    resolve (response);
                    // this callback will be called asynchronously
                    // when the response is available
                }, function error(response) {
                    console.log("error", response);
                    reject (response);
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });
            });
        },
        getBundle: function(id) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/bundle/' + id
                }).then(function success(response) {
                    console.log(response);
                    resolve (response);
                    // this callback will be called asynchronously
                    // when the response is available
                }, function error(response) {
                    console.log("error", response);
                    reject (response);
                    // called asynchronously if an error occurs
                    // or server returns response with an error status.
                });
            });
        }
    };
});
