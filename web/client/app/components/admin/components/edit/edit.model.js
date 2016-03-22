tourable.factory('editFactory', function($http, $q) {
    return {
        save: function(className, data) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'PUT',
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
        delete: function(className, id) {
            return $q(function(resolve, reject) {
                $http({
                    headers: {
                        'Content-Type': "text/plain"
                    },
                    method: 'DELETE',
                    url: 'https://api.tourable.org/api/v1/' + className + '/' + id
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
        getTour: function(id) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/bundle/' + id
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
        getSection: function(id) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/section/' + id
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
        getSubsections: function(superSectionID) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/sections?superSection=' + superSectionID
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
        getPOIs: function(sectionID) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/pois?section=' + sectionID
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
        getKeys: function(tourID) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/keys?tour=' + tourID
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
