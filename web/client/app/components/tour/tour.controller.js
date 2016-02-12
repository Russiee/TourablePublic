'use strict';

angular.module('touring')
	.controller('TourCtrl', function ($scope, $location) {
        $scope.location = $location.search().key;
	});
