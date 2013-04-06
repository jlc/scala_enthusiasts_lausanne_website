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

function SessionsCtrl($scope, ContentSession) {
    $scope.sessions = ContentSession.query(function(sessions) {
	console.debug("SessionCtrl.sessions.callback: ");
	console.debug(sessions);

	// sessions is {title: '', date: '', ...etc.} cf. Content.scala
	$scope.sessions = $.map(sessions, function(elm, idx) {
	    // convert to js date
	    elm.date = new Date(Number(elm.date));
	    return elm;
	});
    });
}
SessionsCtrl.$inject = ['$scope', 'ContentSession'];

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

