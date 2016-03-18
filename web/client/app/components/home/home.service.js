touring.factory('keyFactory', function($http, $q){
    return {
        verify: function(key){
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'http://touring-api.herokuapp.com/api/v1/key/verify/' + key
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
