tourable.config(function($stateProvider, $urlRouterProvider, $locationProvider) {

    Parse.initialize("touring", "2368AB2DAA73D3CB6B55555CEAF6C");
    Parse.serverURL = 'http://touring-db.herokuapp.com/parse';

    $locationProvider.html5Mode(true);

    //TODO change this to 404
    // For any unmatched url, redirect to /home
    $urlRouterProvider.otherwise("/");

    //state init
    $stateProvider

    .state('home', {
        url: "/",
        templateUrl: "/client/app/components/home/home.view.html",
        controller: "HomeCtrl"
    })


    .state('tour', {
        templateUrl: "/client/app/components/tour/tour.view.html",
        controller: "TourCtrl"
    })
    .state('tour.overview', {
        url: "/tour?key",
        templateUrl: "/client/app/components/tour/components/overview.view.html"
    })
    .state('tour.section', {
        url: "/tour/section/*path?key",
        templateUrl: "/client/app/components/tour/components/section.view.html"
    })
    .state('tour.poi', {
        url: "/tour/poi/*path?key",
        templateUrl: "/client/app/components/tour/components/poi.view.html"
    })


    .state('admin', {
        templateUrl: "/client/app/components/admin/admin.view.html",
        controller: "AdminCtrl",
        authenticate: true
    })
    .state('admin.login', {
        url: "/admin/login",
        templateUrl: "/client/app/components/admin/components/login.view.html"
    })
    .state('admin.dashboard', {
        url: "/admin",
        templateUrl: "/client/app/components/admin/components/dashboard.view.html",
        authenticate: true
    })
    .state('admin.manageTours', {
        url: "/admin/manage/tours",
        templateUrl: "/client/app/components/admin/components/dashboard.view.html",
        authenticate: true
    })
    .state('admin.manageAdmins', {
        url: "/admin/manage/admins",
        templateUrl: "/client/app/components/admin/components/dashboard.view.html",
        authenticate: true
    })
    .state('admin.account', {
        url: "/admin/account",
        templateUrl: "/client/app/components/admin/components/dashboard.view.html",
        authenticate: true
    });
});
