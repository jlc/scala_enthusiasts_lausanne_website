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

    // Be aware, using Texts will be filled up _after_ controllers initialisation
    // due to asText copy
    factory('Texts', function() {
	return {};
    }).

    directive('asText', function($parse, Texts) {
	// This directive is very anti-angular since it copy from the view to the model.
	// angular is supposed to work the other way arround. However, if well controled, this is ok.
	return {
	    compile: function(elm, attrs, transclude) {
		return {
		    pre: function preLink(scope, elem, attrs, ctrl) {
			// NOTE: copy as earlier as possible
			$parse(attrs.ngModel).assign(Texts, attrs.asText);
		    },
		    post: function postLink(scope, elem, attrs, ctrl) {}
		}
	    },
	    link: function postLink(scope, elem, attrs) {}
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

