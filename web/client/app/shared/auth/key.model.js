tourable.factory('keyFactory', function($http, $q) {
    return {
        verify: function(key) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/key/verify/' + key
                }).then(function success(response) {
                    resolve (response);
                }, function error(response) {
                    console.log("Error verifying key: ", response);
                    reject (response);
                });
            });
        }
    };
});
