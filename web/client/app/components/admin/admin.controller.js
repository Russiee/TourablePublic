'use strict';

angular.module('touring')
	.controller('AdminCtrl', function ($scope, AuthService, $state) {
    
        $scope.$on('loginStatusChanged', function (event, isLoggedIn) {
            if (isLoggedIn){
                $state.transitionTo("admin.dashboard");
            }
        });
    
        $scope.login = function () {
            AuthService.login($scope.email, $scope.password);
        }

	});
