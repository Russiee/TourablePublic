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
                getAllTours();
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the all-admin data: ', error);
            });
        }

        function getAllTours () {
            var getAllTourData = adminFactory.getAllTours();
            getAllTourData.then(function(response) {
                console.log('Success: ', response.data);
                $scope.tours = {
                    thisAdmin: [],
                    otherAdmins: []
                };
                for (var index in response.data) {
                    var tour = response.data[index];

                    if (tour.admin && tour.admin.objectId === $scope.admin.objectId) {
                        console.log("Compare " + tour.admin.objectId + " to " + $scope.admin.objectId);
                        $scope.tours.thisAdmin.push(tour);
                    } else if (tour.admin) {
                        for (var _index in $scope.admins) {
                            console.log("Compare " + tour.admin.objectId + " to " + $scope.admins[_index].objectId);
                            if (tour.admin.objectId === $scope.admins[_index].objectId) {
                                $scope.tours.otherAdmins.push(tour);
                            }
                        }
                    }
                }
                console.log("tours", $scope.tours);
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
