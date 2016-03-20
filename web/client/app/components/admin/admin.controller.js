angular.module('tourable')
    .controller('AdminCtrl', function ($scope, AuthService, $state, $location) {

        $scope.$on('loginStatusChanged', function (event, isLoggedIn) {
            if (isLoggedIn){
                console.log("hello");
                $state.transitionTo("admin.dashboard");
            }
        });

        $scope.login = function () {
            AuthService.login($scope.email, $scope.password);
        };

        $scope.logout = function () {
            AuthService.logout();
        };

    });
