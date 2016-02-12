touring.run(($rootScope) => {
	$rootScope.$on("$stateChangeError", console.log.bind(console));
});

touring.config(function($stateProvider, $urlRouterProvider, $locationProvider) {

	$locationProvider.html5Mode(true);

	//TODO change this to 404
	// For any unmatched url, redirect to /home
	$urlRouterProvider.otherwise("/");

	//state init
	$stateProvider
	.state('home', {
		url: "/",
		templateUrl: "/app/components/home/home.view.html",
		controller: "HomeCtrl"
	})
    .state('tour', {
		url: "/tour?key",
		templateUrl: "/app/components/tour/tour.view.html",
		controller: "TourCtrl"
	})
    
    
	.state('admin', {
		url: "/admin",
		templateUrl: "/app/components/admin/admin.view.html",
		controller: "AdminCtrl"
	});
});
