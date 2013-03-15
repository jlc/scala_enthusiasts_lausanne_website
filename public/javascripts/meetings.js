/*
 * meetings.js
 *
 */

/*
 * Angular Controllers
 */

function AgendaCtrl($scope, $routeParams) {
}
AgendaCtrl.$inject = ['$scope', '$routeParams'];

function SessionsCtrl($scope, $routeParams) {
}
SessionsCtrl.$inject = ['$scope', '$routeParams'];

function SpeakersCtrl($scope, $routeParams) {
}
SpeakersCtrl.$inject = ['$scope', '$routeParams'];

/*
 * Angular module
 */

angular.module('meetings', ['common']).

    // configure view routes
    config(['$routeProvider', function($routeProvider) {
	$routeProvider.
	    when('/agenda', {templateUrl: '/templates/meetings/agenda', controller: AgendaCtrl}).
	    when('/sessions', {templateUrl: '/templates/meetings/sessions', controller: SessionsCtrl}).
	    when('/speakers', {templateUrl: '/templates/meetings/speakers', controller: SpeakersCtrl}).
	    otherwise({redirectTo: '/agenda'});
    }]);

