angular.module('tourable')
.factory( 'AuthService', function($rootScope, $q) {

    Parse.initialize("touring", "2368AB2DAA73D3CB6B55555CEAF6C");
    Parse.serverURL = 'https://touring-db.herokuapp.com/parse';

//    var currentUser;

//        Parse.User.current().fetch().then(function (user) {
//             currentUser = user;
//        });

    return {
//        signup: function(firstname, lastname, email, phone, pass) {
//            var user = new Parse.User();
//            user.set("username", email);
//            user.set("password", pass);
//            user.set("email", email);
//            user.set("firstname", firstname);
//            user.set("lastname", lastname);
//
//            user.signUp(null, {
//                success: function(user) {
//                    $rootScope.$broadcast('loginStatusChanged', true);
//                    return true;
//                },
//                error: function(user, error) {
//                    // Show the error message somewhere and let the user try again.
//                    console.log("Error: " + error.code + " " + error.message);
//                    return false;
//                }
//            });
//        },
        login: function(email, pass) {
            Parse.User.logIn(email, pass, {
                success: function(user) {
                    // Do stuff after successful login.
                    $rootScope.$broadcast('loginStatusChanged', true);
                },
                error: function(user, error) {
                    console.log(error);
                    // The login failed. Check error to see why.
                }
            });
        },
        logout: function() {
            Parse.User.logOut();
            $rootScope.$broadcast('loginStatusChanged', false);
        },
        isLoggedIn: function() {
            if (Parse.User.current()) {
                return true;
            } else {
                return false;
            }
        },
        currentUser: function() {
            if (Parse.User.current()){
                return Parse.User.current();
            } else {
                return undefined;
            }
        }
    };
})

//Check if route has to be authenticated, and redirect to login if it is.
.run(function ($rootScope, $state, AuthService) {
    $rootScope.$on("$stateChangeStart", function(event, toState, toParams, fromState, fromParams) {
        if (toState.authenticate && !AuthService.isLoggedIn()){
            $state.transitionTo("admin.login");
            event.preventDefault();
        } else if (toState.name === "admin.login" && AuthService.isLoggedIn()) {
            $state.transitionTo("admin.dashboard");
            event.preventDefault();
        }
    });
});
