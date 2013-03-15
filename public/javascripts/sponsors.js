/*
 * sponsors.js
 *
 */

/*
 * Angular Controllers
 */

function XtechCtrl($scope, $routeParams) {
}
XtechCtrl.$inject = ['$scope', '$routeParams'];

function EpflCtrl($scope, $routeParams) {
}
EpflCtrl.$inject = ['$scope', '$routeParams'];

/*
 * Angular module
 */

angular.module('sponsors', ['common']).

    // configure view routes
    config(['$routeProvider', function($routeProvider) {
	$routeProvider.
	    when('/crossing-tech', {templateUrl: '/templates/ngapp/sponsors/crossing-tech', controller: XtechCtrl}).
	    when('/epfl', {templateUrl: '/templates/ngapp/sponsors/epfl', controller: EpflCtrl}).
	    otherwise({redirectTo: '/crossing-tech'});
    }]);

