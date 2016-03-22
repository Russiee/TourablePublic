angular.module('tourable')
    .controller('EditCtrl', function ($scope, $location, $state, classDataFactory, editFactory) {

        String.prototype.capitalize = function() {
            return this.charAt(0).toUpperCase() + this.slice(1);
        }

        for (var index in $scope.$parent.tours.all) {
            if ($scope.$parent.tours.all[index].objectId === $state.params.id) {
                $scope.tour = $scope.$parent.tours.all[index];
            }
        }

        console.log($scope.tour);

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

        var getKeys = editFactory.getKeys($state.params.id);
        getKeys.then(function(response) {
            console.log('Keys: ', response.data);
            $scope.tour.keys = response.data;
            sessionStorage.setItem('tour.keys', JSON.stringify($scope.tour.keys));
        }, function(error) {
            //Console log in case we need to debug with a user
            console.log('An error occured while retrieving the admin data: ', error);
        });

        $scope.createTopSection = function() {
            $state.go('admin.create', {
                className: 'section',
                tour: $state.params.id,
                superSection: "",
                depth: 0
            });
        }

    });
