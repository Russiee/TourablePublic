angular.module('tourable')
    .controller('EditCtrl', function ($rootScope, $scope, $state, editFactory, $http, Upload, $timeout) {

        $rootScope.loadingLight = true;

        for (var index in $scope.$parent.tours.all) {
            if ($scope.$parent.tours.all[index].objectId === $state.params.id) {
                $scope.tour = $scope.$parent.tours.all[index];
            }
        }

        if (!$state.params.id) {
            $state.go('admin.manageTours');
        }

        $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams) {
            $scope.pois = [];
            if (toState.name === 'admin.edit.tour' || toState.name === 'admin.edit.section') {
                $rootScope.loadingLight = true;
            }
        });

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {
            if (toState.name.indexOf('admin.edit') !== -1) {
                if (!toParams.id && toParams.id !== null) {
                    $state.go('admin.manageTours');
                } else if (fromState.name.indexOf('admin.edit') !== -1) {
                    $rootScope.loadingLight = true;
                    if (toState.name === 'admin.edit.tour') {
                        getTourData();
                        getKeysData();
                    } else if (toState.name === 'admin.edit.section') {
                        getSectionData();
                        getSubsectionData();
                        getAllPOIdata();
                    } else if (toState.name === 'admin.edit.key') {
                        getKeyData();
                    } else if (toState.name === 'admin.edit.poi') {
                        getPOIdata();
                    }
                }
            }
        });


        $scope.tour = sessionStorage.getItem('tour') || {};
        $scope.keys = {};
        $scope.keymessage = "";

        $scope.section = sessionStorage.getItem('section') || {};
        $scope.deleteSwitch = 0;

        if ($state.current.name === 'admin.edit.tour') {
            getTourData();
            getKeysData();
        } else if ($state.current.name === 'admin.edit.section') {
            if ($state.params.id && $state.params.id !== "null") {
                getSectionData();
                getSubsectionData();
                getAllPOIdata();
            } else {
                $state.go('admin.manageTours');
            }
        } else if ($state.current.name === 'admin.edit.key') {
            $rootScope.loadingLight = true;
            getKeyData();
        } else if ($state.current.name === 'admin.edit.poi') {
            $rootScope.loadingLight = true;
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
                if ($state.current.name === 'admin.edit.tour' && $scope.tour.objectId) {
                    $rootScope.loadingLight = false;
                }
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the tour data: ', error);
            });
        }

        function getKeysData() {
            var getKeys = editFactory.getKeys($state.params.id);
            getKeys.then(function(response) {
                console.log('Keys: ', response.data);
//                $scope.tour.keys = response.data;
                $scope.keys = response.data;
                sessionStorage.setItem('tour.keys', JSON.stringify($scope.tour.keys));
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the key data: ', error);
            });
        }

        function getKeyData() {
            var getKey = editFactory.getKey($state.params.id);
            getKey.then(function(response) {
                console.log('Key: ', response.data);
//                $scope.tour.keys = response.data;
                $scope.key = response.data;
                $scope.key.expiry = new Date ($scope.key.expiry);
                sessionStorage.setItem('key', JSON.stringify($scope.key));
                $rootScope.loadingLight = false;
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
                $scope.section = response.data;
                $scope.section.subsections = subsections;
                sessionStorage.setItem('section', JSON.stringify($scope.tour.keys));
                if ($state.current.name === 'admin.edit.section') {
                    $rootScope.loadingLight = false;
                }
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
                $state.go('admin.edit.tour')
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
            var getPOI = editFactory.getPOI($state.params.id);
            getPOI.then(function(response) {
                console.log('POI: ', response.data);
                $scope.poi = response.data;
                sessionStorage.setItem('poi', JSON.stringify($scope.poi));
                $rootScope.loadingLight = false;
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        function getAllPOIdata () {
            var getPOIs = editFactory.getPOIs($state.params.id);
            getPOIs.then(function(response) {
                console.log('POIs: ', response.data);
                $scope.pois = response.data;
                sessionStorage.setItem('section', JSON.stringify($scope.section));
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
            });
        }

        $scope.save = function () {

            $scope.saving = true;

            var data, className;

            if ($state.current.name === 'admin.edit.tour') {
                data = $scope.tour;
                className = 'tour';
            } else if ($state.current.name === 'admin.edit.section') {
                data = $scope.section;
                className = 'section';
            } else if ($state.current.name === 'admin.edit.key') {
                data = $scope.key;
                className = 'key';
            } else if ($state.current.name === 'admin.edit.poi') {
                data = $scope.poi;
                className = 'poi'
            }

            var id = data.objectId;

            console.log(data);

            var editObj = editFactory.save(className, data, id, $scope.tour.objectId || $scope.key.tour.objectId || $scope.section.tour.objectId || $scope.objectId);
            editObj.then(function(response) {
                $scope.saving = false;
                console.log('EDIT response: ', response.data);
                if ($state.current.name === 'admin.edit.tour') {
                    $rootScope.loadingLight = true;
                    getTourData();
                } if ($state.current.name === 'admin.edit.key') {
                    $state.go('admin.edit.tour', {id: $scope.key.tour.objectId});
                } else if ($state.current.name === 'admin.edit.section') {
                    if ($scope.section.superSection.objectId !== "null") {
                        $state.go('admin.edit.section', {
                            className: 'section',
                            id: $scope.section.superSection.objectId
                        });
                    } else if ($scope.tour.objectId) {
                         $state.go('admin.edit.tour', {
                            className: 'tour',
                            id: $scope.tour.objectId
                        });
                    }
                }
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
                $scope.saving = false;
                $scope.errormessage = true;
            });
        }

        $scope.delete = function() {
            deleteObject();
        }


        function deleteObject () {
            $scope.saving = true;
            var deleteObj = editFactory.delete($state.current.name.substring($state.current.name.lastIndexOf('.') + 1), $state.params.id);
            deleteObj.then(function(response) {
                $scope.saving = false;
                console.log('DELETE response: ', response.data);
                if ($state.current.name === 'admin.edit.tour') {
                    $state.go('admin.manageTours');
                } else if ($state.current.name === 'admin.edit.section') {
                    if ($scope.section.superSection.objectId !== "null") {
                        $state.go('admin.edit.section', {
                            className: 'section',
                            id: $scope.section.superSection.objectId
                        });
                    } else if ($scope.tour.objectId) {
                         $state.go('admin.edit.tour', {
                            className: 'tour',
                            id: $scope.tour.objectId
                        });
                    }
                }
            }, function(error) {
                //Console log in case we need to debug with a user
                console.log('An error occured while retrieving the admin data: ', error);
                $scope.saving = false;
                if ($scope.section.superSection.objectId !== "null") {
                    $state.go('admin.edit.section', {
                        className: 'section',
                        id: $scope.section.superSection.objectId
                    });
                } else if ($scope.tour.objectId) {
                     $state.go('admin.edit.tour', {
                        className: 'tour',
                        id: $scope.tour.objectId
                    });
                }
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

        $scope.createSubsection = function() {
            $state.go('admin.create', {
                className: 'section',
                tour: $state.params.id,
                superSection: $scope.section.objectId,
                depth: $scope.section.depth + 1
            });
        }

        $scope.createPOI = function() {
            $state.go('admin.create', {
                className: 'poi',
                section: $scope.section.objectId,
            });
        }

        $scope.createKey = function () {
            $state.go('admin.create', {
                className: 'key',
                tour: $state.params.id
            });
        }

         $scope.createPostItem = function (type) {
            //initiate POI post array
            if (!$scope.poi.post) {
                $scope.poi.post = [];
            }
            //initialize options array if type is quiz
            if (type === 'quiz') {
                $scope.poi.post.push({
                    type: 'quiz',
                    answer: '',
                    options: ["Wrong Answer A"]
                });
            } else {
                $scope.poi.post.push({
                    type: type
                });
            }
            console.log($scope.poi.post);
        }

        String.prototype.capitalize = function() {
            return this.charAt(0).toUpperCase() + this.slice(1);
        }


        //This code is modfified open-source software
        //credit goes to:
        //https://github.com/nukulb/s3-angular-file-upload
        //license: https://github.com/nukulb/s3-angular-file-upload/blob/master/LICENSE

        $scope.uploadFiles = function(file, errFiles, item) {
            $scope.uploading = true;
            $scope.f = file;
            $scope.errFile = errFiles && errFiles[0];
            if (file) {
                $http.get('/api/s3Policy?mimeType='+ file.type + '&key=' + $state.params.section).success(function(response) {
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
                                'key' : $state.params.section + '/' + file.name,
                                'acl' : 'public-read',
                                'Content-Type' : file.type,
                                'AWSAccessKeyId': s3Params.AWSAccessKeyId,
                                'success_action_status' : '201',
                                'Policy' : s3Params.s3Policy,
                                'Signature' : s3Params.s3Signature
                            },
                            file: file,
                        });

                    console.log($state.params);

                        file.upload.then(function (response) {

                            $scope.uploading = false;

                            var fileuri = file.name;

                            if (file.type === 'video/avi' || file.type === 'video/dcm') {
                                fileuri = fileuri.substring(0, fileuri.lastIndexOf('.')) + '.mp4';
                            }

                            var url = "https://tourable-media.s3.amazonaws.com/" + $state.params.section + '/' + fileuri;

                            $scope.poi.post.push({
                                type: item,
                                url: url,
                                description: " "
                            });

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
