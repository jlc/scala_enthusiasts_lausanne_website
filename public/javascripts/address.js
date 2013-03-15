/*
 * address.js
 *
 */

/*
 * Angular Controllers
 */

function MapCtrl($scope, $routeParams) {
}
MapCtrl.$inject = ['$scope', '$routeParams'];

function ContactCtrl($scope, $routeParams) {
}
ContactCtrl.$inject = ['$scope', '$routeParams'];

/*
 * Angular module
 */

angular.module('address', ['common']).

    // configure view routes
    config(['$routeProvider', function($routeProvider) {
	$routeProvider.
	    when('/map', {templateUrl: '/templates/ngapp/address/map', controller: MapCtrl}).
	    when('/contact', {templateUrl: '/templates/ngapp/address/contact', controller: ContactCtrl}).
	    otherwise({redirectTo: '/map'});
    }]).

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
