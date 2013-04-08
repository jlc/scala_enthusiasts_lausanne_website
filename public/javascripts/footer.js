/*
 * main.js
 */

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
 * IndividualMessageCtrl
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

