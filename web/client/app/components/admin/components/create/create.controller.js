angular.module('tourable')
    .controller('CreateCtrl', function ($rootScope, $scope, $location, $state, createFactory, classDataFactory, Upload, $timeout, $http) {

        String.prototype.capitalize = function() {
            return this.charAt(0).toUpperCase() + this.slice(1);
        }

        if (!$scope.$parent.admin) {
            $state.go('admin.dashboard');
        }

        $rootScope.loadingLight = false;

        $scope.class = $state.params.className;
        $scope.tour = $state.params.tour;
        $scope.section = $state.params.section;
        $scope.superSection = $state.params.superSection;
        $scope.depth = $state.params.depth;
        $scope.organization = $state.params.organization;

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
            if (!$scope.section) {
                $state.go('admin.dashboard');
            } else {
                $scope.classData = classDataFactory.poi($scope.section);
            }
        } else if ($scope.class === 'admin') {
            if (!$scope.organization) {
                $state.go('admin.manageAdmins');
            } else {
                $scope.classData = classDataFactory.admin($scope.organization);
            }
        }else {
            $state.go('admin.dashboard');
        }





        $scope.create = function() {
            console.log("hello");
            $scope.validate = true;
            var isValid = true;
            var data = $scope.classData.expectedInput;

            for (var index in data) {
                if (data[index].required && data[index].value === undefined) {
                    isValid = false;
                }
            }

            if (isValid) {
                $scope.creating = true;
                var postData = prepData(data, $scope.classData.defaultModels);
                var createObject = createFactory.create($scope.class, postData);
                createObject.then(function(response) {
                    $state.go($scope.classData.afterCreate.route, $scope.classData.afterCreate.options);
                }, function(error) {
                    $scope.createError = true;
                    //Console log in case we need to debug with a user
                    console.log('An error occured while creating the object: ', error);
                });

            } else {
                $scope.createError = true;
            }
        }

        function prepData (data, defaults) {
            var prepped = defaults;
            for (var index in data) {
                prepped[data[index].model] = data[index].value;
            }
            if ($scope.class === 'admin') {
                prepped.username = prepped.email;
            }
            return prepped;
        }

        $scope.createPostItem = function (type) {
            //initiate POI post array
            if (!$scope.classData.expectedInput[1].value) {
                $scope.classData.expectedInput[1].value = [];
            }
            //initialize options array if type is quiz
            if (type === 'quiz') {
                $scope.classData.expectedInput[1].value.push({
                    type: 'quiz',
                    value: {
                        options: ["Wrong Answer A"]
                    }
                });
            } else {
                $scope.classData.expectedInput[1].value.push({
                    type: type
                });
            }
            console.log($scope.classData.expectedInput[1].value);
        }


        //This code is modfified open-source software
        //credit goes to:
        //https://github.com/nukulb/s3-angular-file-upload
        //license: https://github.com/nukulb/s3-angular-file-upload/blob/master/LICENSE

        $scope.uploadFiles = function(file, errFiles) {
            $scope.f = file;
            $scope.errFile = errFiles && errFiles[0];
            if (file) {
                $http.get('/api/s3Policy?mimeType='+ file.type + '&key=' + $scope.section).success(function(response) {
                    console.log(response);
                        var s3Params = response;
                        file.upload = Upload.upload({
                            url: 'https://tourable-media.s3.amazonaws.com/',
                            method: 'POST',
                            transformRequest: function (data, headersGetter) {
                                //Headers change here
                                var headers = headersGetter();
                                delete headers['Authorization'];
                                return data;
                            },
                            data: {
                                'key' : $scope.section + '/' + file.name,
                                'acl' : 'public-read',
                                'Content-Type' : file.type,
                                'AWSAccessKeyId': s3Params.AWSAccessKeyId,
                                'success_action_status' : '201',
                                'Policy' : s3Params.s3Policy,
                                'Signature' : s3Params.s3Signature
                            },
                            file: file,
                        });



                        file.upload.then(function (response) {
                            console.log(response);
                            $timeout(function () {
                                file.result = response.data;
                            });
                        }, function (response) {
                            if (response.status > 0)
                                $scope.errorMsg = response.status + ': ' + response.data;
                        }, function (evt) {
                            file.progress = Math.min(100, parseInt(100.0 *
                                                     evt.loaded / evt.total));
                        });
                    });
            }
        }

    });
