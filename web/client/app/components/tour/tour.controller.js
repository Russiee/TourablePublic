angular.module('tourable')
    .controller('TourCtrl', function ($rootScope, $scope, $location, $state, keyFactory, tourFactory) {

        if ($state.current.name === "tour.section" && $state.params.path.length === 0) {
            $location.url('/tour?key=' + $scope.key);
        }

        $rootScope.$on('$stateChangeSuccess', function(event, toState, toParams, fromState, fromParams){
            if ($state.current.name === "tour.section" && $state.params.path.length === 0) {
                $location.url('/tour?key=' + $scope.key);
            } else if ($state.current.name === "tour.section") {
                if ($state.params.path === 't') {
                    $scope.startTour();
                } else {
                    getSectionFromPath($state.params.path);
                }
            } else if ($state.current.name === "tour.poi") {
                getPOIFromPath($state.params.path);
            }
        });

        //verify that the key is valid again, in case the user tries to manipulate the url
        var verifyKey = keyFactory.verify($location.search().key);
        verifyKey.then(function(response) {
            $scope.key = $location.search().key;
            console.log('Success: ', response.data);
            getTourMetaData(response.data.tour.objectId);
            getTourBundle(response.data.tour.objectId);

        }, function(error) {
            //Console log in case we need to debug with a user
            console.log('Invalid Key: ', $location.search().key);
            //Redirect back to homepage
            $location.url('/');
        });

        function getSectionFromPath(path) {
            var hierachy = path.split('');
            console.log(hierachy);
            var script = "$scope.section = $scope.tour.sections[" + hierachy[1] + "]";
            for (var s = 2; s < hierachy.length; s++) {
                console.log(hierachy[s]);
                script += ".subsections[" + hierachy[s] + "]";
            }
            console.log(script);
            eval(script);
        }

        function getPOIFromPath(path) {
            var hash = path.indexOf('#');
            console.log(path.substring(0,hash));
            getSectionFromPath(path.substring(0,hash));
            $scope.poi = $scope.section.pois[parseInt(path.substring(hash + 1))]
        }

        function getTourMetaData(id) {
            var getMetaData = tourFactory.getTour(id);
            getMetaData.then(function(response) {
                console.log(response.data);
                //check if bundle has already populated $scope.tour, if not fill it with the metadata
                if (!$scope.tour) {
                    $scope.tour = response.data;
                }
            }, function (error) {
                console.log("Error fetching tour metadata: ", error);
            });
        }

        function getTourBundle(id) {
            var getBundle = tourFactory.getBundle(id);
            getBundle.then(function(response) {
                console.log(response.data);
                $scope.tour = response.data;
                sessionStorage.setItem('tour', response.data);



                if ($state.current.name === "tour.section" && $state.params.path.length > 0) {
                    getSectionFromPath($state.params.path);
                } else if ($state.current.name === "tour.poi" && $state.params.path.length > 0) {
                    getPOIFromPath($state.params.path);
                }

            }, function (error) {
                console.log("Error fetching tour bundle: ", error);
            });
        }

        $scope.startTour = function() {
            console.log($scope.tour.objectId);
            $location.url('/tour/section/t?key=' + $scope.key);
            $scope.section = {
                title: $scope.tour.title,
                description: $scope.tour.description,
                subsections: $scope.tour.sections,
                pois: [],
                previousSection: null
            };
        };

        $scope.navBack = function() {
            if ($state.current.name === 'tour.overview') {
                $state.go('home');
            } else if ($state.current.name === 'tour.section') {
                var newPath = $state.params.path.substring(0, $state.params.path.length - 1);
                $state.go('tour.section', {path: newPath, key: $scope.key});
            } else if ($state.current.name === 'tour.poi') {
                var hash = $state.params.path.indexOf('#');
                var newPath = $state.params.path.substring(0, hash);
                $state.go('tour.section', {path: newPath, key: $scope.key});
            }
        };

        $scope.goToSection = function (index) {
            var newPath = $state.params.path + index;
            $state.go('tour.section', {path: newPath, key: $scope.key});
        }

        $scope.goToPOI = function (index) {
            var newPath = $state.params.path + '#' + index;
            $state.go('tour.poi', {path: newPath, key: $scope.key})
        }

        $scope.solve;
    });
