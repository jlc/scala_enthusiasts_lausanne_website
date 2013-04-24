/*
 * admin.js
 *
 */

/*
 * Angular Controllers
 */

function EditIntroductionCtrl($scope, $routeParams, ContentIntroduction, Text, GeneralMessage) {
    $scope.intro = ContentIntroduction.get({}, function(intro) {
	console.debug("EditIntroductionCtrl.intro.callback: ");
	console.debug(intro);
	$scope.intro = intro; // with {title: '', content: ''} cf. Content.scala
    });

    $scope.save = function() {
	$scope.intro.$save(function(intro, headers) {
	    GeneralMessage.update(Text.get('admin.intro.updated'), 'info');
	});
    }
}
EditIntroductionCtrl.$inject = ['$scope', '$routeParams', 'ContentIntroduction', 'Text', 'GeneralMessage'];


function EditAnnouncementCtrl($scope, $routeParams, ContentAnnouncement, Text, GeneralMessage) {
    $scope.announce = ContentAnnouncement.get({}, function(ann) {
	console.debug("EditAnnouncementCtrl.announce.callback: ");
	console.debug(ann);
	$scope.announce = ann; // with {title: '', content: '', ...etc. } cf. Content.scala
    });

    $scope.save = function() {
	$scope.announce.$save(function(ann, headers) {
	    GeneralMessage.update(Text.get('admin.announce.updated'), 'info');
	});
    }
}
EditAnnouncementCtrl.$inject = ['$scope', '$routeParams', 'ContentAnnouncement', 'Text', 'GeneralMessage'];


function EditSessionsCtrl($scope, $route, ContentSession, Text, GeneralMessage) {
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

	    GeneralMessage.update(Text.get('admin.session.created'), 'info');

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

	    GeneralMessage.update(Text.get('admin.session.updated'), 'info');

	    sess.date = new Date(Number(sess.date));
	    session = sess;
	});
    }

    $scope.destroy = function(session) {
	console.debug("EditSessionCtrl.destroy:");

	session.$delete(function(sess, headers) {
	    console.debug("EditSessionsCtrl.update: session deleted");
	    GeneralMessage.update(Text.get('admin.session.deleted'), 'info');

	    $route.reload();
	});
    }
}
EditSessionsCtrl.$inject = ['$scope', '$route', 'ContentSession', 'Text', 'GeneralMessage'];

function EditPasswordCtrl($scope, $resource, Text, GeneralMessage) {
    var UserPassword = $resource('/content/user/password');

    $scope.oldPassword = '';
    $scope.newPassword = '';
    $scope.newPasswordBis = '';

    $scope.save = function() {
	var userPassword = new UserPassword();
	userPassword.oldPassword = $scope.oldPassword;
	userPassword.newPassword = $scope.newPassword;
	userPassword.newPasswordBis = $scope.newPasswordBis;

	userPassword.$save(function (answer, headers) {
	    console.debug('EditPasswordCtrl.save.callbeck: answer: ' + answer.say);
	    if (answer.say == 'dontmatch')
		GeneralMessage.update(Text.get('admin.password.dontmatch'), 'error');
	    else if (answer.say == 'wrongoldpassword')
		GeneralMessage.update(Text.get('admin.password.wrongoldpassword'), 'error');
	    else if (answer.say == 'ok')
		GeneralMessage.update(Text.get('admin.password.saved'), 'info');
	});
    }
}
EditPasswordCtrl.$inject = ['$scope', '$resource', 'Text', 'GeneralMessage'];

