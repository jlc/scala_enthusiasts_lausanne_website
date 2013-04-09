/*
 * admin.js
 *
 */

/*
 * Angular Controllers
 */

function EditIntroductionCtrl($scope, $routeParams, ContentIntroduction, Texts, GeneralMessage) {
    $scope.intro = ContentIntroduction.get({}, function(intro) {
	console.debug("EditIntroductionCtrl.intro.callback: ");
	console.debug(intro);
	$scope.intro = intro; // with {title: '', content: ''} cf. Content.scala
    });

    $scope.save = function() {
	$scope.intro.$save(function(intro, headers) {
	    GeneralMessage.update(Texts.admin.intro.updated, 'info');
	});
    }
}
EditIntroductionCtrl.$inject = ['$scope', '$routeParams', 'ContentIntroduction', 'Texts', 'GeneralMessage'];


function EditAnnouncementCtrl($scope, $routeParams, ContentAnnouncement, Texts, GeneralMessage) {
    $scope.announce = ContentAnnouncement.get({}, function(ann) {
	console.debug("EditAnnouncementCtrl.announce.callback: ");
	console.debug(ann);
	$scope.announce = ann; // with {title: '', content: '', ...etc. } cf. Content.scala
    });

    $scope.save = function() {
	$scope.announce.$save(function(ann, headers) {
	    GeneralMessage.update(Texts.admin.announce.updated, 'info');
	});
    }
}
EditAnnouncementCtrl.$inject = ['$scope', '$routeParams', 'ContentAnnouncement', 'Texts', 'GeneralMessage'];


function EditSessionsCtrl($scope, $route, ContentSession, Texts, GeneralMessage) {
    $scope.newSession = {type: 'new', data: {}};

    //$scope.text will be filled by the view (yes! not the angular way, but usefull to reuse server internationalisation)

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

	    GeneralMessage.update(Texts.admin.session.created, 'info');

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

	    GeneralMessage.update(Texts.admin.session.updated, 'info');

	    sess.date = new Date(Number(sess.date));
	    session = sess;
	});
    }

    $scope.destroy = function(session) {
	console.debug("EditSessionCtrl.destroy:");

	session.$delete(function(sess, headers) {
	    console.debug("EditSessionsCtrl.update: session deleted");
	    GeneralMessage.update(Texts.admin.session.deleted, 'info');

	    $route.reload();
	});
    }
}
EditSessionsCtrl.$inject = ['$scope', '$route', 'ContentSession', 'Texts', 'GeneralMessage'];


