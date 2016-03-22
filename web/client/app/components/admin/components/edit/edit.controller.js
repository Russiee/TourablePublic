angular.module('tourable')
    .controller('EditCtrl', function ($rootScope, $scope, $state, editFactory) {

        for (var index in $scope.$parent.tours.all) {
            if ($scope.$parent.tours.all[index].objectId === $state.params.id) {
                $scope.tour = $scope.$parent.tours.all[index];
            }
        }

        if (!$state.params.id) {
            $state.go('admin.manageTours');
        }

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {

            if (toState.name === 'admin.edit.tour') {
                if (!toParams.id) {
                    $state.go('admin.manageTours');
                } else {
                    getTourData();
                    getKeyData();
                }
            } else if (toState.name === 'admin.edit.section') {
                if (!toParams.id) {
                    $state.go('admin.manageTours');
                } else {
                    getSectionData();
                    getSubsectionData();
                    getPOIdata();
                }
            }
        });


        $scope.tour = {};
        $scope.section = {};

        if ($state.current.name === 'admin.edit.tour') {

            getTourData();
            getKeyData();
        } else if ($state.current.name === 'admin.edit.section') {
            getSectionData();
            getSubsectionData();
            getPOIdata();
        }

        function getTourData() {
            var getTour = editFactory.getTour($state.params.id);
            getTour.then(function(response) {
                console.log('Success: ', response.data);
                var keys = $scope.tour.keys;
                $scope.tour = response.data;
                $scope.tour.keys = keys;
                sessionStorage.setItem('tour', JSON.stringify($scope.tour));
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        function getKeyData() {
            var getKeys = editFactory.getKeys($state.params.id);
            getKeys.then(function(response) {
                console.log('Keys: ', response.data);
                $scope.tour.keys = response.data;
                sessionStorage.setItem('tour.keys', JSON.stringify($scope.tour.keys));
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        function getSectionData () {
            var getSection = editFactory.getSection($state.params.id);
            getSection.then(function(response) {
                console.log('Section: ', response.data);
                var subsections = $scope.section.subsections;
                var pois = $scope.section.pois;
                $scope.section = response.data;
                $scope.section.subsections = subsections;
                $scope.section.pois = pois;
                sessionStorage.setItem('section', JSON.stringify($scope.tour.keys));
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        function getSubsectionData () {
            var getSubsections = editFactory.getSubsections($state.params.id);
            getSubsections.then(function(response) {
                console.log('Subsection: ', response.data);
                $scope.section.subsections = response.data;
                sessionStorage.setItem('section', JSON.stringify($scope.section));
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        function getPOIdata () {
            var getPOIs = editFactory.getPOIs($state.params.id);
            getPOIs.then(function(response) {
                console.log('POIs: ', response.data);
                $scope.section.pois = response.data;
                sessionStorage.setItem('section', JSON.stringify($scope.section));
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        $scope.createTopSection = function() {
            $state.go('admin.create', {
                className: 'section',
                tour: $state.params.id,
                superSection: "",
                depth: 0
            });
        }

        $scope.createKey = function () {
            $state.go('admin.create', {
                className: 'key',
                tour: $state.params.id
            });
        }

    });
