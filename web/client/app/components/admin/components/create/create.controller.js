angular.module('tourable')
    .controller('TourCtrl', function ($scope, $location, $state) {
        $scope.class = $state.params.class;
    });
