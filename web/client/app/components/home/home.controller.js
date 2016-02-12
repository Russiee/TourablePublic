'use strict';

angular.module('touring')
	.controller('HomeCtrl', function ($scope, $location) {
        $scope.startTour = function (key) {
            $location.url('/tour?key=' + key);
        }

	});
