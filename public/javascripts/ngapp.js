/*
 * ngapp.js
 *
 * Angular application
 */

angular.module('address', []).

    // configure view routes
    config(['$routeProvider', function($routeProvider) {
	$routeProvider.
	    when('/map', {templateUrl: '/templates/ngapp/address/map', controller: MapCtrl}).
	    when('/contact', {templateUrl: '/templates/ngapp/address/contact', controller: ContactCtrl}).
	    otherwise({redirectTo: '/map'});
    }]).

    // to enable animation, we declare a new directive (fade-it) that create animation
    directive('fadeIt', function() {
	return {
	    compile: function(elm) {
		// set default
		$(elm).css('opacity', 0.0);

		// executed when element is displayed
		return function(scope, elm, attrs) {
		    $(elm).animate({opacity: 1.0}, 1000);
		}
	    }
	};
    });
