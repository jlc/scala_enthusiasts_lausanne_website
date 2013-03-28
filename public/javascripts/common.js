/*
 * common.js
 *
 * comman angular modules
 */

angular.module('common', ['ngResource']).

    factory('ContentIntroduction', function($resource) {
	var ContentIntroduction = $resource('/rest/content/introduction');
	return ContentIntroduction;
    }).

    factory('ContentAnnouncement', function($resource) {
	var ContentAnnouncement = $resource('/rest/content/announcement');
	return ContentAnnouncement;
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
	    console.debug("dynamicResizeRatio");

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
