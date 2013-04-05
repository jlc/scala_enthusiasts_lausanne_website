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
