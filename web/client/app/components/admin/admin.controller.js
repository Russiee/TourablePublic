angular.module('tourable')
    .controller('AdminCtrl', function ($rootScope, $scope, AuthService, $state, $location, adminFactory) {

        if ($state.current.name !== 'admin.login') {
            $rootScope.loadingLight = true;
        }

        if (($state.current.name === 'admin.dashboard' || $state.current.name === 'admin.manageAdmins') && $scope.admin && !$scope.admin.isSuper) {
            $state.go('admin.manageTours');
        }

        $scope.$state = $state;
        $scope.accountmessage = "";

        $scope.$on('loginStatusChanged', function (event, isLoggedIn) {
            if (isLoggedIn){
                if ($scope.admin && $scope.admin.isSuper) {
                    $state.go("admin.dashboard");
                } else {
                    $state.go("admin.manageTours");
                }
                getAdmin();
            } else {
                sessionStorage.removeItem('admin');
                sessionStorage.removeItem('organization');
                sessionStorage.removeItem('admins');
                sessionStorage.removeItem('tours');
                sessionStorage.removeItem('tour');
                sessionStorage.removeItem('keys');
                sessionStorage.removeItem('key');
                $state.go("home");
            }
        });

        $scope.admin =          JSON.parse(sessionStorage.getItem('admin'));
        $scope.organization =   JSON.parse(sessionStorage.getItem('organization'));
        $scope.admins =         JSON.parse(sessionStorage.getItem('admins'));
        $scope.tours =          JSON.parse(sessionStorage.getItem('tours'));

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
            if (fromState.name === 'admin.create' && fromParams.className === 'tour') {
                getAllTours();
            } else if (fromState.name === 'admin.create' && fromParams.class === 'admin') {
                getAdmin();
            } else if (toState.name === 'admin.manageAdmins') {
                getAdmin();
            } else if (toState.name === 'admin.manageTours') {
                getAllTours();
            }
        });

        if (AuthService.isLoggedIn()) {
            getAdmin();
        }

        function getAdmin () {
            var getAdminData = adminFactory.getAdmin(AuthService.currentUser().id);
            getAdminData.then(function(response) {
                $scope.admin = response.data;
                sessionStorage.setItem('admin', JSON.stringify($scope.admin));
                if ($state.current.name === 'admin.account') {
                    $rootScope.loadingLight = false;
                }
                if ($scope.admin.isSuper) {
                    getOrganization(response.data.organization.objectId);
                    getAllAdmins(response.data.organization.objectId);
                } else {
                    $rootScope.loading = false;
                    $rootScope.loadingLight = false;
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
                $scope.organization = response.data;
                sessionStorage.setItem('organization', JSON.stringify($scope.organization));
                $rootScope.loading = false;
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        function getAllAdmins (orgID) {
            var getAllAdminData = adminFactory.getAllAdmins(orgID);
            getAllAdminData.then(function(response) {
                $scope.admins = response.data;
                sessionStorage.setItem('admins', JSON.stringify($scope.admins));
                getAllTours();
                if ($state.current.name === 'admin.manageAdmins') {
                    $rootScope.loadingLight = false;
                }
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the all-admin data: ', error);
            });
        }

        function getAllTours () {
            var getAllTourData = adminFactory.getAllTours();
            getAllTourData.then(function(response) {
                $scope.tours = {
                    thisAdmin: [],
                    otherAdmins: [],
                    all: []
                };
                for (var index in response.data) {
                    var tour = response.data[index];
                    $scope.tours.all.push(tour);
                    if (tour.admin && tour.admin.objectId === $scope.admin.objectId) {
                        $scope.tours.thisAdmin.push(tour);
                        sessionStorage.setItem('tours', JSON.stringify($scope.tours));
                    } else if (tour.admin) {
                        for (var _index in $scope.admins) {
                            if (tour.admin.objectId === $scope.admins[_index].objectId) {
                                $scope.tours.otherAdmins.push(tour);
                            }
                        }
                    }
                }
                if ($state.current.name === 'admin.manageTours') {
                    $rootScope.loadingLight = false;
                }
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving all tours: ', error);
            });
        }

        $scope.login = function () {
            $rootScope.loading = true;
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

        $scope.saveAccountDetails = function() {
            $scope.saving = true;
            $scope.accountmessage = "";

            AuthService.updateUser($scope.admin);
        };

        $scope.$on('accountmessage', function (event, message) {
            $scope.accountmessage = message;
            $scope.saving = false;
            console.log($scope.accountmessage);
            $scope.$apply();
        });

        $scope.accountDeleteSwitch = 0;

        $scope.deleteAccount = function () {
            AuthService.deleteUser($scope.admin);
            AuthService.logout();
        }

    });
