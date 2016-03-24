angular.module('tourable')
    .controller('EditCtrl', function ($rootScope, $scope, $state, editFactory) {

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
            if (toState.name === 'admin.edit.tour' || toState.name === 'admin.edit.section') {
                $rootScope.loadingLight = true;
            }
        });

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams) {

            if (toState.name === 'admin.edit.tour') {
                if (!toParams.id) {
                    $state.go('admin.manageTours');
                } else if (fromState.name.indexOf('admin.edit') !== -1) {
                    getTourData();
                    getKeysData();
                }
            } else if (toState.name === 'admin.edit.section') {
                if (!toParams.id && toParams.id !== null) {
                    $state.go('admin.manageTours');
                } else if (fromState.name.indexOf('admin.edit') !== -1) {
                    getSectionData();
                    getSubsectionData();
                    getPOIdata();
                }
            } else if (toState.name === 'admin.edit.key') {
                if (!toParams.id && toParams.id !== null) {
                    $state.go('admin.manageTours');
                } else if (fromState.name.indexOf('admin.edit') !== -1) {
                    $rootScope.loadingLight = true;
                    getKeyData();
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
                getPOIdata();
            } else {
                $state.go('admin.manageTours');
            }
        } else if ($state.current.name === 'admin.edit.key') {
            $rootScope.loadingLight = true;
            getKeyData();
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
                console.log('An error occured while retrieving the admin data: ', error);
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
                console.log('An error occured while retrieving the admin data: ', error);
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
                var pois = $scope.section.pois;
                $scope.section = response.data;
                $scope.section.subsections = subsections;
                $scope.section.pois = pois;
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

            delete data.objectId, delete data.createdAt, delete data.updatedAt;

            console.log(data);

            var editObj = editFactory.save(className, data, id);
            editObj.then(function(response) {
                $scope.saving = false;
                console.log('EDIT response: ', response.data);
                if ($state.current.name === 'admin.edit.tour' || $state.current.name === 'admin.edit.key') {
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
            var deleteObj = editFactory.delete($state.current.name.substring($state.current.name.lastIndexOf('.') + 1), $state.params.id);
            deleteObj.then(function(response) {
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

    });
