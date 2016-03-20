angular.module('tourable')
    .controller('HomeCtrl', function ($scope, $location, keyFactory) {

        $scope.startTour = function (key) {
            var promise = keyFactory.verify(key);
            promise.then(function(response) {
                $location.url('/tour?key=' + key);
                console.log('Success: ' + response);
            }, function(error) {
                alert("KEY REJECTED ASSHOLE");
                console.log('Failed: ' + error);
            });
        };
    });
