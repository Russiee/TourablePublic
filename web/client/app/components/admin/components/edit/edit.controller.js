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
            $scope.tour = response.data;
            sessionStorage.setItem('tour', JSON.stringify($scope.tour));
        }, function(error) {
            //Console log in case we need to debug with a user
            console.log('An error occured while retrieving the admin data: ', error);
        });

//        $scope.createSection = function() {
//            $state.go('admin.create' {className})
//        }
    });
