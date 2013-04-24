/*
 * menu.js
 */

/*
 * LoginCtrl
 */
function LoginCtrl($scope, $http, $timeout) {

    $scope.authenticate = function(user) {
	$http.post('/auth/authenticate', user).
	    success(function(data, status, headers, config) {
		// invalid authentication
		if (!data.return) {
		    $scope.setAuthResult(false);

		    $scope.reset();

		    $timeout(function() { $scope.setAuthResult(null); }, 3000);
		}
		// valid auth
		else {
		    $scope.setAuthResult(true);

		    $timeout(function() {
			$('#collapseSubMenu').on('hidden', function() {
			    $(location).attr('href', '/');
			});
			$('#collapseSubMenu').collapse('toggle');
		    }, 1500);
		}
	    }).
	    error(function(data, status, headers, config) {
		$scope.setAuthResult(false);
	    });
    }

    $scope.reset = function() {
	$scope.user = angular.copy($scope.default);
    }

    $scope.setAuthResult = function(b) {
	if (b == null) {
	    $scope.authenticated = false;
	    $scope.errorAuthenticate = false;
	}
	else if (b) $scope.authenticated = true;
	else $scope.errorAuthenticate = true;
    }

    $scope.default = {};
    $scope.user = angular.copy($scope.default);

    $scope.authenticated = false;
    $scope.errorAuthenticate = false;
}

LoginCtrl.$inject = ['$scope', '$http', '$timeout'];


/*
 * DynamicMenuCtrl
 */
function DynamicMenuCtrl($scope, $location, $timeout, Text) {
    var self = this;

    $scope.links = [];
    $scope.showMenu = false;

    var ensureLinks = function() {
	console.debug("ensureLinks initialise");
	self.links = {
	    index: [
		{url: '#/index', text: Text.get('index.menu.home')}
	    ],
	    meetings: [
		{url: '#/meetings/agenda', text: Text.get('meetings.menu.agenda')},
		{url: '#/meetings/sessions', text: Text.get('meetings.menu.sessions')} /*,
		{url: '#/meetings/speakers', text: Text.get('meetings.menu.speakers')} */
	    ],
	    address: [
		{url: '#/address/map', text: Text.get('address.menu.map')},
		{url: '#/address/contact', text: Text.get('address.menu.contact')}
	    ],
	    sponsors: [
		{url: '#/sponsors/crossing-tech', text: Text.get('sponsors.menu.crossingtech')},
		{url: '#/sponsors/epfl', text: Text.get('sponsors.menu.epfl')}
	    ],
	    admin: [
		{url: '#/admin/edit-introduction', text: Text.get('admin.menu.edit.intro')},
		{url: '#/admin/edit-announcement', text: Text.get('admin.menu.edit.announce')},
		{url: '#/admin/edit-sessions', text: Text.get('admin.menu.edit.sessions')},
		{url: '#/admin/edit-password', text: Text.get('admin.menu.edit.password')}
	    ]
	};
	// reset ensureLinks to empty function to improve efficiency of future calls
	ensureLinks = function() {};
    }

    $scope.$on('$routeChangeStart', function(next, current) {
	ensureLinks();

	var parts = $location.url().split('/');

	if (parts.length < 2)
	    $scope.links = [];
	else
	    $scope.links = self.links[parts[1]];

	if ($scope.links.length > 0) $scope.showMenu = true;
	else $scope.showMenu = false;
    });
}
DynamicMenuCtrl.$inject = ['$scope', '$location', '$timeout', 'Text'];

