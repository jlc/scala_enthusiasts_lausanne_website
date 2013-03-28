/*
 * admin.js
 *
 */

/*
 * Angular module
 */

angular.module('admin', ['common']).

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
	console.debug("ContentIntroductionCtrl.intro.callback: ");
	console.debug(intro);
	$scope.intro = intro; // with {title: '', content: ''}
    });

    $scope.save = function() {
	$scope.intro.$save();
    }
}
EditIntroductionCtrl.$inject = ['$scope', '$routeParams', 'ContentIntroduction'];


function EditAnnouncementCtrl($scope, $routeParams, ContentAnnouncement) {
    $scope.announce = ContentAnnouncement.get({}, function(ann) {
	console.debug("ContentAnnouncementCtrl.announce.callback: ");
	console.debug(ann);
	$scope.announce = ann; // with {title: '', content: ''}
    });

    $scope.save = function() {
	$scope.announce.$save();
    }
}
EditAnnouncementCtrl.$inject = ['$scope', '$routeParams', 'ContentAnnouncement'];


function EditSessionsCtrl($scop, $routeParams) {
}
EditSessionsCtrl.$inject = ['$scope', '$routeParams'];


