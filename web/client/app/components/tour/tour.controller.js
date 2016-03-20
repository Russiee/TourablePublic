angular.module('tourable')
    .controller('TourCtrl', function ($scope, $location, keyFactory, tourFactory) {

        //verify that the key is valid again, in case the user tries to manipulate the url
        var verifyKey = keyFactory.verify($location.search().key);
        verifyKey.then(function(response) {
            $scope.location = $location.search().key;
            console.log('Success: ', response.data);

            getTourBundle(response.data.tour.objectId);

        }, function(error) {
            //If it fails, redirect back to homepage
            location.url('/tour?key=' + key);
            //Console log in case we need to debug with a user
            console.log('Invalid Key: ', key);
        });

        function getTourMetaData(id) {
            var getMetaData = tourFactory.getTour(id);
            getMetaData.then(function(response) {
                console.log(response.data);
            }, function (error) {
                console.log("Error fetching tour metadata: ", error);
            });
        }

        function getTourBundle(id) {
            var getBundle = tourFactory.getBundle(id);
            getBundle.then(function(response) {
                console.log(response.data);
            }, function (error) {
                console.log("Error fetching tour bundle: ", error);
            });
        }
    });
