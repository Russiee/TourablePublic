angular.module('tourable')
    .controller('CreateCtrl', function ($scope, $location, $state) {
        String.prototype.capitalize = function() {
            return this.charAt(0).toUpperCase() + this.slice(1);
        }

        $scope.class = $state.params.class;

        $scope.classData = {
            tour: {
                expectedInput: [
                    {
                        description: "Tour Title",
                        model: "title",
                        type: "text",
                        help: ""
                    },
                    {
                        description: "Tour Description",
                        model: "description",
                        type: "textarea-small",
                        help: ""
                    }
                ],
                afterCreate: "admin.manageTours"
            }
        }

        $scope.create = function() {

        }

        //redirect back to dashboard if class is null or does not exist
        if (!$state.params.class || !$scope.classData[$state.params.class]) {
            $state.go('admin.dashboard');
        }
    });
