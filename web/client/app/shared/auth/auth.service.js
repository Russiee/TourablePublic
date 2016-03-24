angular.module('tourable')
.factory( 'AuthService', function($rootScope, $q) {

    Parse.initialize("touring", "2368AB2DAA73D3CB6B55555CEAF6C");
    Parse.serverURL = 'https://touring-db.herokuapp.com/parse';

//    var currentUser;

//        Parse.User.current().fetch().then(function (user) {
//             currentUser = user;
//        });

    return {
        login: function(email, pass) {
            Parse.User.logIn(email, pass, {
                success: function(user) {
                    // Do stuff after successful login.
                    $rootScope.$broadcast('loginStatusChanged', true);
                },
                error: function(user, error) {
                    console.log(error);
                    $rootScope.loading = false;
                    $rootScope.loginError = true;
                    $rootScope.$apply();
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
        },
        updateUser: function(data) {
            var user = Parse.User.current();

            console.log(data);

            user.set("firstname", data.firstname);
            user.set("lastname", data.lastname);
            user.setEmail(data.email);  // attempt to change username
            user.setUsername(data.email);  // attempt to change username
            user.setPassword(data.password);  // attempt to change username

            user.save(null, {
                success: function(user) {
                    $rootScope.$broadcast('accountmessage', "success");
                },
                error: function(error) {
                    $rootScope.$broadcast('accountmessage', "error");
                    console.log("Error updating user",error);
                }
            });
        },
        deleteUser: function(data) {
            var user = Parse.User.current();



            //parse doesn't allow automatic deletes, so we set the account unusable
            console.log(data);

            user.set("organization", null);
            user.set("firstname", data.firstname);
            user.set("lastname", data.lastname);
            user.setEmail(data.email);  // attempt to change username
            user.setUsername(data.email);  // attempt to change username
            user.setPassword("deleted");  // attempt to change username

            user.save(null, {
                success: function(user) {
                    $rootScope.$broadcast('accountmessage', "success");
                },
                error: function(error) {
                    $rootScope.$broadcast('accountmessage', "error");
                    console.log("Error updating user",error);
                }
            });
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
