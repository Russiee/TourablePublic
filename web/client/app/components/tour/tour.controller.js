angular.module('tourable')
    .controller('TourCtrl', function ($scope, $location, $state, keyFactory, tourFactory) {

        if ($state.current.name === "tour.section" && $state.params.path.length === 0) {
            $location.url('/tour?key=' + $scope.key);
        }

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
            $location.url('/tour/section/' + $scope.tour.objectId + '?key=' + $scope.key);
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
            if ($scope.section.previousSection === null) {
                $location.url('/tour?key=' + $scope.key);
            } else {
                $location.url('/tour/section/' + $scope.section.previousSection.objectId + '?key=' + $scope.key);
            }
        };
    });
