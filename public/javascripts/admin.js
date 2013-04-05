/*
 * admin.js
 *
 */

/*
 * Angular module
 */

angular.module('admin', ['common', 'ui']).

    // configure view routes
    config(['$routeProvider', function($routeProvider) {
	$routeProvider.
	    when('/edit-introduction', {templateUrl: '/templates/admin/edit-introduction', controller: EditIntroductionCtrl}).
	    when('/edit-announcement', {templateUrl: '/templates/admin/edit-announcement', controller: EditAnnouncementCtrl}).
	    when('/edit-sessions', {templateUrl: '/templates/admin/edit-sessions', controller: EditSessionsCtrl}).
	    otherwise({redirectTo: '/edit-introduction'});
    }]);


/*
 * Angular Controllers
 */

function EditIntroductionCtrl($scope, $routeParams, ContentIntroduction) {
    $scope.intro = ContentIntroduction.get({}, function(intro) {
	console.debug("EditIntroductionCtrl.intro.callback: ");
	console.debug(intro);
	$scope.intro = intro; // with {title: '', content: ''} cf. Content.scala
    });

    $scope.save = function() {
	$scope.intro.$save();
    }
}
EditIntroductionCtrl.$inject = ['$scope', '$routeParams', 'ContentIntroduction'];


function EditAnnouncementCtrl($scope, $routeParams, ContentAnnouncement) {
    $scope.announce = ContentAnnouncement.get({}, function(ann) {
	console.debug("EditAnnouncementCtrl.announce.callback: ");
	console.debug(ann);
	$scope.announce = ann; // with {title: '', content: '', ...etc. } cf. Content.scala
    });

    $scope.save = function() {
	$scope.announce.$save();
    }
}
EditAnnouncementCtrl.$inject = ['$scope', '$routeParams', 'ContentAnnouncement'];


function EditSessionsCtrl($scope, $route, ContentSession) {
    $scope.newSession = {type: 'new', data: {}};

    $scope.sessions = ContentSession.query(function(sessions) {
	console.debug("EditSessionsCtrl.sessions.callback: ");
	console.debug(sessions);

	// sessions is {title: '', date: '', ...etc.} cf. Content.scala
	$scope.sessions = $.map(sessions, function(elm, idx) {
	    // convert to js date
	    elm.date = new Date(Number(elm.date));
	    return {type: 'existing', data: elm};
	});
	$scope.sessions.unshift($scope.newSession);
    });

    $scope.create = function() {
	console.debug("EditSessionsCtrl.create: ");

	// NOTE: by copying the newSession.data, we cut the binding/watching link with the view.
	// When receiving the answer (the object itself), the view try to update the date
	// before we have the oportunity to convert it, which result in error.
	var sess = angular.copy($scope.newSession.data);

	// convert the date for the server
	sess.date = sess.date.toISOString();
	sess.uuid = "new";
	var createdSession = new ContentSession(sess);

	createdSession.$save(function(sess, headers) {
	    console.debug("EditSessionsCtrl.create: session created");
	    // update the view
	    sess.date = new Date(Number(sess.date));
	    $scope.newSession.data = sess;

	    $route.reload();
	});
    }

    $scope.update = function(session) {
	console.debug("EditSessionsCtrl.update: ");

	// NOTE: by copying the session, we cut the binding/watching link with the view.
	// (cf. note above)
	var sess = angular.copy(session);

	// convert the date for the server
	sess.date = sess.date.toISOString();

	sess.$save(function(sess, headers) {
	    console.debug("EditSessionsCtrl.update: session updated");
	    sess.date = new Date(Number(sess.date));
	    session = sess;
	});
    }

    $scope.destroy = function(session) {
	console.debug("EditSessionCtrl.destroy:");

	session.$delete(function(sess, headers) {
	    console.debug("EditSessionsCtrl.update: session deleted");
	});
    }
}
EditSessionsCtrl.$inject = ['$scope', '$route', 'ContentSession'];


