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
function DynamicMenuCtrl($scope, $location, $timeout, Texts) {
    var self = this;

    $scope.links = [];
    $scope.showMenu = false;

    var ensureLinks = function() {
	console.debug("ensureLinks initialise");
	self.links = {
	    index: [
		{url: '#/index', text: Texts.index.menu.home}
	    ],
	    meetings: [
		{url: '#/meetings/agenda', text: Texts.meetings.menu.agenda},
		{url: '#/meetings/sessions', text: Texts.meetings.menu.sessions},
		{url: '#/meetings/speakers', text: Texts.meetings.menu.speakers}
	    ],
	    address: [
		{url: '#/address/map', text: Texts.address.menu.map},
		{url: '#/address/contact', text: Texts.address.menu.contact}
	    ],
	    sponsors: [
		{url: '#/sponsors/crossing-tech', text: Texts.sponsors.menu.crossingtech},
		{url: '#/sponsors/epfl', text: Texts.sponsors.menu.epfl}
	    ],
	    admin: [
		{url: '#/admin/edit-introduction', text: Texts.admin.menu.edit.intro},
		{url: '#/admin/edit-announcement', text: Texts.admin.menu.edit.announce},
		{url: '#/admin/edit-sessions', text: Texts.admin.menu.edit.sessions}
	    ]
	};
	// reset ensureLinks to empty function to improve efficiency of future calls
	ensureLinks = function() {};
    }

    $scope.$on('$routeChangeStart', function(next, current) {
	ensureLinks();

	var parts = $location.url().split('/');

	if (parts.length < 1)
	    $scope.links = [];
	else
	    $scope.links = self.links[parts[1]];

	//console.debug($scope.links);
	if ($scope.links.length > 0) $scope.showMenu = true;
	else $scope.showMenu = false;
    });
}
DynamicMenuCtrl.$inject = ['$scope', '$location', '$timeout', 'Texts'];

