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
                    var hierachy = $state.params.path.split('');
                    console.log(hierachy);
                    var script = "$scope.section = $scope.tour.sections[" + hierachy[1] + "]";
                    for (var s = 2; s < hierachy.length; s++) {
                        console.log(hierachy[s]);
                        script += ".subsections[" + hierachy[s] + "]";
                    }
                    console.log(script);
                    eval(script);
                }
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

                console.log($state.params);

                if ($state.current.name === "tour.section" && $state.params.path.length > 0) {
                    if (sessionStorage.getItem('section')) {
                        $scope.section = JSON.parse(sessionStorage.getItem('section'));
                    }
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
            sessionStorage.setItem('section', JSON.stringify($scope.section));
        };

        $scope.navBack = function() {
            if ($state.current.name === 'tour.overview') {
                $state.go('home');
            } else if ($state.current.name === 'tour.section') {
                var newPath = $state.params.path.substring(0, $state.params.path.length - 1);
                $state.go('tour.section', {path: newPath});
            }
        };

        $scope.goToSection = function (index) {
            var newPath = $state.params.path + index;
            $state.go('tour.section', {path: newPath});
            $scope.section = $scope.section.subsections[index];
            sessionStorage.setItem('section', JSON.stringify($scope.section));
        }

        $scope.goToPOI = function (index) {
            var newPath = $state.params.path + index;
            $state.go('tour.poi', {path: newPath})
        }
    });
