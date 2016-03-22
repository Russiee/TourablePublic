angular.module('tourable')
    .controller('CreateCtrl', function ($scope, $location, $state, createFactory, classDataFactory) {

        String.prototype.capitalize = function() {
            return this.charAt(0).toUpperCase() + this.slice(1);
        }

        if (!$scope.$parent.admin) {
            $state.go('admin.dashboard');
        }



        $scope.class = $state.params.className;
        $scope.tour = $state.params.tour;
        $scope.section = $state.params.section;
        $scope.superSection = $state.params.superSection;
        $scope.depth = $state.params.depth;

        console.log("States",$state.params);

        if ($scope.class === 'tour') {
            $scope.classData = classDataFactory.tour($scope.$parent.admin);
        } else if ($scope.class === 'section') {
            if (!$scope.tour || !$scope.depth) {
                if ($scope.superSection) {
                    $state.go('admin.edit.section',{id:$scope.superSection});
                } else if ($scope.tour) {
                    $state.go('admin.edit.tour',{id:$scope.tour});
                } else {
                    $state.go('admin.dashboard');
                }
            } else {
                if ($scope.superSection) {
                    $scope.classData = classDataFactory.section($scope.tour, $scope.superSection, $scope.depth);
                } else {
                    $scope.classData = classDataFactory.topSection($scope.tour, $scope.depth);
                }
            }
        } else if ($scope.class === 'key') {
            if (!$scope.tour) {
                $state.go('admin.dashboard');
            } else {
                $scope.classData = classDataFactory.key($scope.tour);
            }
        } else if ($scope.class === 'poi') {
            alert($scope.section);
            if (!$scope.section) {
                $state.go('admin.dashboard');
            } else {
                $scope.classData = classDataFactory.poi($scope.section);
            }
        }else {
            $state.go('admin.dashboard');
        }





        $scope.create = function() {
            $scope.validate = true;
            var isValid = true;
            var data = $scope.classData.expectedInput;

            for (var index in data) {
                if (data[index].required && !data[index].value) {
                    isValid = false;
                }
            }

            if (isValid) {
                $scope.creating = true;
                var postData = prepData(data, $scope.classData.defaultModels);
                var createObject = createFactory.create($scope.class, postData);
                createObject.then(function(response) {
                    console.log('Success: ', response.data);
                    $state.go($scope.classData.afterCreate.route, $scope.classData.afterCreate.options);
                }, function(error) {
                    //Console log in case we need to debug with a user
                    console.log('An error occured while retrieving the admin data: ', error);
                });

            }
        }

        function prepData (data, defaults) {
            var prepped = defaults;
            console.log(prepped);
            for (var index in data) {
                console.log(data[index].value);
                prepped[data[index].model] = data[index].value;
            }
            return prepped;
        }


    });
