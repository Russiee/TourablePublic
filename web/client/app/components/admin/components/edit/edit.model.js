tourable.factory('editFactory', function($http, $q) {
    var editFunctions = {
        save: function(className, data, id, tourID) {
            return $q(function(resolve, reject) {

                var newData = data;

                if (className === 'tour') {
                    newData.version += 1;
                } else {
                    var increment = editFunctions.incrementVersion(tourID);
                    increment.then(function(response) {
                        console.log("incremented tour");
                    }, function(error) {
                        console.log("Error ", error);
                    });
                }

                $http({
                    method: 'PUT',
                    url: 'https://api.tourable.org/api/v1/' + className + '/' + id,
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
        },
        getKey: function(key) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/key/' + key
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
        getPOI: function(poi) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/poi/' + poi
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
        incrementVersion: function(id) {
            return $q(function(resolve, reject) {
                $http({
                    method: 'GET',
                    url: 'https://api.tourable.org/api/v1/tour/' + id
                }).then(function success(response) {

                    var data = response.data;

                    var saveTour = editFunctions.save('tour', data, data.objectId);
                    saveTour.then(function(response) {
                        resolve(response);
                    }, function(error) {
                        console.log("Error ", error);
                        reject (response);
                    });

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

    return editFunctions;
});
