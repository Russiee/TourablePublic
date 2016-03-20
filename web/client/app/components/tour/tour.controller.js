angular.module('tourable')
    .controller('TourCtrl', function ($scope, $location) {
        $scope.location = $location.search().key;
    });
