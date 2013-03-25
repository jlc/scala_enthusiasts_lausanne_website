/*
 * main.js
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
 * OtherClientsCtrl
 */
function OtherClientsCtrl($scope, $timeout) {

    $scope.lastInfo = "";

    $scope.setupServerSentEvent = function() {
	if (typeof(EventSource) == 'undefined') {
	    console.debug("OtherClientsCtrl.setupServerSentEvent: Server Sent Event not supported.");
	    return;
	}

	$scope.feed = new EventSource('/stream/otherclients');

	console.debug("Debug: source created: ");
	console.debug($scope.feed);

	$scope.feed.addEventListener('open', function(e) {
	    console.debug('OtherClientsCtrl.setupServerSentEvent: open: ' + e);
	});

	$scope.feed.addEventListener('error', function(e) {
	    console.debug('OtherClientsCtrl.setupServerSentEvent: error: ' + e);
	});

	$scope.feed.addEventListener('newclient', function(e) {
	    var json = JSON.parse(e.data);
	    console.debug('OtherClientsCtrl.setupServerSentEvent: newclient: ' + json);
	    console.debug(json);

	    $scope.lastInfo = json['info'];
	    $scope.$apply();
	});
    }

    $scope.setupServerSentEvent();
}

OtherClientsCtrl.$inject = ['$scope', '$timeout'];

