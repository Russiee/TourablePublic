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
		templateUrl: "/app/components/admin/admin.view.html",
		controller: "AdminCtrl",
        authenticate: true
	})
    .state('admin.login', {
		url: "/admin/login",
        parent: 'admin',
		templateUrl: "/app/components/admin/login.view.html",
		controller: "AdminCtrl"
	})
    .state('admin.dashboard', {
		url: "/admin",
        parent: 'admin',
		templateUrl: "/app/components/admin/dashboard.view.html",
		controller: "AdminCtrl",
        authenticate: true
	});
});
