/*
 * directives.js
 */

angular.module('common', ['ngResource']).

    directive('copyToModel', function($parse) {
	return function(scope, elem, attrs) {
	    $parse(attrs.ngModel).assign(scope, attrs.copyToModel);
	}
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
    }).

    // to enable animation, we declare a new directive (fade-it) that create animation
    directive('fadeIt', function() {
	return {
	    compile: function(elm) {
		// set default
		$(elm).css('opacity', 0.0);

		// executed when element is displayed
		return function(scope, elm, attrs) {
		    $(elm).animate({opacity: 1.0}, 1000);
		}
	    }
	};
    }).

    directive('locationWithGoogleMap', function($timeout) {
	return {
	    link: function(scope, elm, attrs) {
		function renderMap() {
		    $(elm).gmap3({
			map: {
			    options: {
				center: [46.517454, 6.562097],
				zoom: 12,
				mapTypeId: google.maps.MapTypeId.ROADMAP,
				//disableDefaultUI: true,
				// controls
				panControl: false,
				zoomControl: false,
				mapTypeControl: true,
				scaleControl: true,
				streetViewControl: false,
				overviewMapControl: false,
				mapTypeControlOptions: {
				    style: google.maps.MapTypeControlStyle.DROPDOWN_MENU,
				},
				scrollWheel: true
			    }
			},
			marker: {
			    latLng: [46.517454, 6.562097],
			    options: {
				icon: new google.maps.MarkerImage('/assets/images/magicshow.png', new google.maps.Size(32, 37, 'px', 'px'))
			    }
			}
		    });
		}

		$timeout(function(){
		    renderMap();
		    //$(elm).gmap3({trigger: 'resize'});
		}, 500);
	    }
	};
    });
