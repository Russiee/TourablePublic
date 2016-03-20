angular.module('tourable')
    .controller('TourCtrl', function ($scope, $location, $state, keyFactory, tourFactory) {

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
                if ($state.current.name === "tour.section" && $state.params.id) {
                    if (sessionStorage.getItem('section')) {
                        $scope.section = JSON.parse(sessionStorage.getItem('section'));
                    } else {
                        $location.url('/tour?key=' + $scope.key);
                    }
                }

            }, function (error) {
                console.log("Error fetching tour bundle: ", error);
            });
        }


//        function initSection() {
//            if ($state.params.id === $scope.tour.objectId) {
//                $scope.section = {
//                    title: $scope.tour.title,
//                    description: $scope.tour.description,
//                    subsections: $scope.tour.sections,
//                    pois: [],
//                    previousSection: null
//                };
//            } else {
//                sessionStorage.setItem('tour', response.data);
//            }
//        }


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
            if ($scope.previousSection === null) {
                $location.url('/tour?key=' + $scope.key);
            } else {
                $location.url('/tour/section/' + $scope.section.previousSection.objectId + '?key=' + $scope.key);
            }
        };
    });
