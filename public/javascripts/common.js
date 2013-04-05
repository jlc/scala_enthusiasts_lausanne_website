/*
 * common.js
 *
 * comman angular modules
 */

angular.module('common', ['ngResource']).

    factory('ContentIntroduction', function($resource) {
	var ContentIntroduction = $resource('/content/introduction');
	return ContentIntroduction;
    }).

    factory('ContentAnnouncement', function($resource) {
	var ContentAnnouncement = $resource('/content/announcement');
	return ContentAnnouncement;
    }).

    factory('ContentSession', function($resource) {
	var ContentSession = $resource('/content/session/:uuid', {uuid: '@uuid'});
	return ContentSession;
    }).

    factory('GeneralMessage', function($rootScope) {
	return {
	    content: '',
	    type: '',
	    update: function(content, type) {
		this.content = content;
		this.type = type;
		$rootScope.$broadcast('updateGeneralMessage');
	    }
	}
    }).

    // to enable animation, we declare a new directive (fade-it) that create animation
    directive('fadeIn', function() {
	return {
	    compile: function(elm) {
		// set default
		//$(elm).fadeOut(0);
		$(elm).css('opacity', 0.0);

		// executed when element is displayed
		return function(scope, elm, attrs) {

		    scope.$watch(attrs.fadeIn, function(duration) {
			$(elm).animate({opacity: 1.0});
			//$(elm).fadeIn(duration);
		    });
		}
	    }
	};
    }).

    directive('dynamicResizeRatio', function() {
	return function(scope, elm, attrs) {

	    function update(ratio) {
		var width = $(elm).parent().width() * ratio;
		var height = $(window).height() * ratio;

		$(elm).width(width).height(height);
	    }

	    scope.$watch(attrs.dynamicResizeRatio, function(ratio) {
		update(ratio);
	    });

	    $(window).resize(function() {
		update(attrs.dynamicResizeRatio);
	    });
	};
    });

function ViewLoadingCtrl($scope) {
    $scope.showLoading = true;

    $scope.$on('$viewContentLoaded', function() {
	$scope.showLoading = false;
    });
}
ViewLoadingCtrl.$inject = ['$scope'];

function GeneralMessageCtrl($scope, $timeout, GeneralMessage) {
    $scope.isOpen = false;
    $scope.message = {content: '', type: ''};

    $scope.update = function() {
	$scope.message.content = GeneralMessage.content;
	$scope.message.type = 'alert-' + GeneralMessage.type;
    }

    $scope.$on('updateGeneralMessage', function() {
	console.debug('GeneralMessageCtrl.updateGeneralMessage');
	$scope.update();
	$scope.isOpen = true;
	$timeout(function() {
	    $scope.isOpen = false;
	}, 5000);
    });

    $scope.update();
}
GeneralMessageCtrl.$inject = ['$scope', '$timeout', 'GeneralMessage'];
