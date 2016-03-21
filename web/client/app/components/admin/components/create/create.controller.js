angular.module('tourable')
    .controller('CreateCtrl', function ($scope, $location, $state, createFactory) {

        String.prototype.capitalize = function() {
            return this.charAt(0).toUpperCase() + this.slice(1);
        }

        $scope.class = $state.params.class;

        if (!$scope.$parent.admin) {
            $state.go('admin.dashboard');
        }

        $scope.classData = {
            tour: {
                expectedInput: [
                    {
                        description: "Tour Title",
                        model: "title",
                        type: "text",
                        help: "Example: 'Cardiac Imaging Tour'",
                        required: true
                    },
                    {
                        description: "Tour Description",
                        model: "description",
                        type: "textarea-small",
                        help: "",
                        required: false
                    }
                ],
                defaultModels: {
                    admin: $scope.$parent.admin.objectId,
                    isPublic : false, //expected input is a Boolean
                    estimatedTime: 30,
                    version: 1
                },
                afterCreate: "admin.manageTours"
            }
        }

        $scope.create = function() {
            $scope.validate = true;
            var isValid = true;
            var data = $scope.classData[$state.params.class].expectedInput;

            for (var index in data) {
                if (data[index].required && !data[index].value) {
                    isValid = false;
                }
            }

            if (isValid) {
                $scope.creating = true;
                var postData = prepData(data, $scope.classData[$state.params.class].defaultModels);
                var createObject = createFactory.create($scope.class, postData);
                createObject.then(function(response) {
                    console.log('Success: ', response.data);
                    $state.go($scope.classData[$state.params.class].afterCreate);
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

        //redirect back to dashboard if class is null or does not exist
        if (!$state.params.class || !$scope.classData[$state.params.class]) {
            $state.go('admin.dashboard');
        }
    });
