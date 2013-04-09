/*
 * meetings.js
 *
 */

/*
 * Angular Controllers
 */

function AgendaCtrl($scope, $routeParams) {
    $scope.eventSource = {
	url: 'https://www.google.com/calendar/feeds/scala.enthusiasts.lausanne%40gmail.com/public/basic',
	className: 'badge badge-info',
	currentTimezone: 'Europe/Zurich'
    };

    $scope.fullCalendarSettings = {
	editable: false,
	firstDay: 1,
	aspectRatio: 1.8,
	header: {
	    left: 'today prev,next',
	    center: 'month agendaWeek',
	    right: 'title'
	}
    };

    $scope.eventSources = [$scope.eventSource];
}
AgendaCtrl.$inject = ['$scope', '$routeParams'];

function SessionsCtrl($scope, $location, ContentSession) {
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

    $scope.$on('$routeChangeStart', function(next, current) {
	if ($location.path() == '/meetings/sessions')
	    $scope.showSessionsMenu = true;
	else
	    $scope.showSessionsMenu = false;
    });

    $scope.showSessionsMenu = false;
}
SessionsCtrl.$inject = ['$scope', '$location', 'ContentSession'];

function SpeakersCtrl($scope, $routeParams) {
}
SpeakersCtrl.$inject = ['$scope', '$routeParams'];
