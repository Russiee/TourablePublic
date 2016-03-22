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
            if (!toParams.id) {
                $state.go('admin.manageTours');
            }
            if (toState.name === 'admin.edit.tour') {
                getTourData();
                getKeyData();
            } else if (toState.name === 'admin.edit.section') {
                 getSectionData();
            }
        });


        $scope.tour = {};

        if ($state.current.name === 'admin.edit.tour') {
            getTourData();
            getKeyData();
        } else if ($state.current.name === 'admin.edit.section') {
            getSectionData();
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
                $scope.section = response.data;
                sessionStorage.setItem('section', JSON.stringify($scope.tour.keys));
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
