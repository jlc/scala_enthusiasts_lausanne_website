/*
 * main.js
 */

function LoginCtrl($scope, $http, $timeout) {

    $scope.authenticate = function(user) {
	$http.post('/remote/authenticate', user).
	    success(function(data, status, headers, config) {
		if (!data.return) {
		    $scope.setAuthResult(false);

		    $scope.reset();

		    $timeout(function() { $scope.setAuthResult(null); }, 3000);
		}
		else {
		    $scope.setAuthResult(true);

		    $timeout(function() {
			$('#collapseSubMenu').on('hidden', function() {
			    $(location).attr('href', '/');
			});
			$('#collapseSubMenu').collapse('toggle');
		    }, 3000);
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

