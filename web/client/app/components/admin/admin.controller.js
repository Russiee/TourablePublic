angular.module('tourable')
    .controller('AdminCtrl', function ($scope, AuthService, $state, $location, adminFactory) {

        $scope.$state = $state;
        $scope.accountmessage = "";

        $scope.$on('loginStatusChanged', function (event, isLoggedIn) {
            console.log("hello");
            if (isLoggedIn){
                console.log("hello");
                $state.go("admin.dashboard");
            } else {
                $state.go("home");
            }
        });

        if (AuthService.isLoggedIn()) {
            var getAdminData = adminFactory.getAdmin(AuthService.currentUser().id);
            getAdminData.then(function(response) {
                console.log('Success: ', response.data);
                $scope.admin = response.data;
                if ($scope.admin.isSuper) {
                    getOrganization(response.data.organization.objectId);
                    getAllAdmins(response.data.organization.objectId);
                    getAllTours(response.data.organization.objectId);
                }
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
                //Redirect back to homepage
                $location.url('/');
            });

        }

        function getOrganization (id) {
            var getOrganizationData = adminFactory.getOrganization(id);
            getOrganizationData.then(function(response) {
                console.log('Success: ', response.data);
                $scope.organization = response.data;
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        function getAllAdmins (orgID) {
            var getAllAdminData = adminFactory.getAllAdmins(orgID);
            getAllAdminData.then(function(response) {
                console.log('Success: ', response.data);
                $scope.admins = response.data;
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the all-admin data: ', error);
            });
        }

        function getAllTours (orgID) {
            var getAllTourData = adminFactory.getAllTours(orgID);
            getAllTourData.then(function(response) {
                console.log('Success: ', response.data);
                $scope.tours = response.data;
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving all tours: ', error);
            });
        }

        $scope.login = function () {
            //if already logged in
            if (AuthService.isLoggedIn()) {
                $state.go("admin.dashboard");
            } else {
                AuthService.login($scope.login.email, $scope.login.password);
            }
        };

        $scope.logout = function () {
            AuthService.logout();
        };

    });
