/*
 * admin.js
 *
 */

/*
 * Angular Controllers
 */

function EditIntroductionCtrl($scope, $routeParams) {
}
EditIntroductionCtrl.$inject = ['$scope', '$routeParams'];

function EditAnnouncementCtrl($scope, $routeParams) {
}
EditAnnouncementCtrl.$inject = ['$scope', '$routeParams'];

function EditSessionsCtrl($scop, $routeParams) {
}
EditSessionsCtrl.$inject = ['$scope', '$routeParams'];

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

