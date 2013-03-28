/*
 * admin.js
 *
 */

/*
 * Angular module
 */

angular.module('adminResources', ['ngResource']).
    factory('AdminIntroduction', function($resource) {
	var AdminIntroduction = $resource('/rest/admin/introduction');
	return AdminIntroduction;
    });

angular.module('admin', ['common', 'adminResources']).

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

function EditIntroductionCtrl($scope, $routeParams, AdminIntroduction) {
    var self = this;

    $scope.intro = AdminIntroduction.get({}, function(intro) {
	console.debug("EditIntroductionCtrl.AdminIntro.callback: ");
	console.debug(intro);
	$scope.intro = intro; // with {title: '', content: ''}
    });

    $scope.save = function() {
	$scope.intro.$save();
    }
}
EditIntroductionCtrl.$inject = ['$scope', '$routeParams', 'AdminIntroduction'];



function EditAnnouncementCtrl($scope, $routeParams) {
}
EditAnnouncementCtrl.$inject = ['$scope', '$routeParams'];

function EditSessionsCtrl($scop, $routeParams) {
}
EditSessionsCtrl.$inject = ['$scope', '$routeParams'];


