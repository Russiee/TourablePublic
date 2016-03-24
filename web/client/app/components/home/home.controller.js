angular.module('tourable')
    .controller('HomeCtrl', function ($scope, $location, keyFactory) {

        $scope.startTour = function (key) {
            var promise = keyFactory.verify(key);
            promise.then(function(response) {
                if (moment(response.data.expiry).diff(moment()) < 0) {
                    $scope.keyError = true;
                    console.log('Key expired');
                } else {
                    $location.url('/tour?key=' + key);
                    console.log('Success: ' + response);
                    }
            }, function(error) {
                $scope.keyError = true;
                console.log('Failed: ' + error);
            });
        };
    });
