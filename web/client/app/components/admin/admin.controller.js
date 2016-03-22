angular.module('tourable')
    .controller('AdminCtrl', function ($rootScope, $scope, AuthService, $state, $location, adminFactory) {

        $scope.$state = $state;
        $scope.accountmessage = "";

        $scope.$on('loginStatusChanged', function (event, isLoggedIn) {
            if (isLoggedIn){
                $state.go("admin.dashboard");
                getAdmin();
            } else {
                sessionStorage.removeItem('admin');
                sessionStorage.removeItem('organization');
                sessionStorage.removeItem('admins');
                sessionStorage.removeItem('tours');
                $state.go("home");
            }
        });

        $scope.admin =          JSON.parse(sessionStorage.getItem('admin'));
        $scope.organization =   JSON.parse(sessionStorage.getItem('organization'));
        $scope.admins =         JSON.parse(sessionStorage.getItem('admins'));
        $scope.tours =          JSON.parse(sessionStorage.getItem('tours'));

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
            if (fromState.name === 'admin.create' && fromParams.class === 'tour') {
                getAllTours();
            } else if (fromState.name === 'admin.create' && fromParams.class === 'admin') {
                getAllAdmins();
            }
        });

        if (AuthService.isLoggedIn()) {
            getAdmin();
        }

        function getAdmin () {
            var getAdminData = adminFactory.getAdmin(AuthService.currentUser().id);
            getAdminData.then(function(response) {
                console.log('Success: ', response.data);
                $scope.admin = response.data;
                sessionStorage.setItem('admin', JSON.stringify($scope.admin));
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
                sessionStorage.setItem('organization', JSON.stringify($scope.organization));
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
                sessionStorage.setItem('admins', JSON.stringify($scope.admins));
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
                        sessionStorage.setItem('tours', JSON.stringify($scope.tours));
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
