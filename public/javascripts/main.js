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

    $scope.userAgentInfo = {family: '', version: '', osName: ''};

    $scope.setupServerSentEvent = function() {
	if (typeof(EventSource) == 'undefined') {
	    console.debug("OtherClientsCtrl.setupServerSentEvent: Server Sent Event not supported.");
	    return;
	}

	$scope.feed = new EventSource('/stream/otheruseragent');

	$scope.feed.addEventListener('open', function(e) {
	    //console.debug('OtherClientsCtrl.setupServerSentEvent: open: ' + e);
	});

	$scope.feed.addEventListener('error', function(e) {
	    console.debug('OtherClientsCtrl.setupServerSentEvent: error: ' + e);
	});

	$scope.feed.addEventListener('otherUserAgentInfo', function(e) {
	    var json = JSON.parse(e.data);
	    $scope.userAgentInfo = json;
	    $scope.$apply();
	});
    }

    $scope.setupServerSentEvent();
}

OtherClientsCtrl.$inject = ['$scope', '$timeout'];



/*
 * OtherClientsCtrl
 */
function IndividualMessageCtrl($scope, $timeout) {

    $scope.message = '';

    $scope.setupServerSentEvent = function() {
	if (typeof(EventSource) == 'undefined') {
	    console.debug("IndividualMessageCtrl.setupServerSentEvent: Server Sent Event not supported.");
	    return;
	}

	$scope.feed = new EventSource('/stream/individualmsg');

	$scope.feed.addEventListener('open', function(e) {
	});

	$scope.feed.addEventListener('error', function(e) {
	    console.debug('IndividualMessageCtrl.setupServerSentEvent: error: ' + e);
	});

	$scope.feed.addEventListener('individualMessage', function(e) {
	    var json = JSON.parse(e.data);
	    $scope.message = json['message'];
	    $scope.$apply();
	});
    }

    $scope.setupServerSentEvent();
}

IndividualMessageCtrl.$inject = ['$scope', '$timeout'];

