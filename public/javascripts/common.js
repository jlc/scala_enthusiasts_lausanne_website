/*
 * common.js
 *
 * comman angular modules
 */

Object.toType = function(obj) {
  return ({}).toString.call(obj).match(/\s([a-z|A-Z]+)/)[1].toLowerCase();
}

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

    factory('Text', function($resource) {

	function Reference() {
	    this.txt = null;
	    this.valueOf = this.toString = this.toSource = this.toLocaleString =
		function() { return this.txt; }
	}

	Reference.prototype.isInitialised = function() {
	    return (this.txt == null) ? false : true;
	}

	Reference.prototype.setText = function (text) {
	    this.txt = text;
	}

	return {
	    texts: {},
	    resource: $resource('/content/text/:id'),

	    getReference: function(textId, from) {
		return this.getReferenceFromList(textId.split('.'), from);
	    },

	    getReferenceFromList: function(ls, from) {
		if (ls.length == 0) return from;
		else {
		    var elm = ls.shift();
		    if (!(elm in from)) {
			if (ls.length == 0) from[elm] = new Reference();
			else from[elm] = {};
		    }
		    return this.getReferenceFromList(ls, from[elm]);
		}
	    },

	    get: function(textId) {
		var instance = this.getReference(textId, this.texts);

		if (!instance.isInitialised()) {
		    this.resource.get({id: textId}, function(value) {
			instance.setText(value.text);
		    });
		}

		return instance;
	    },

	    // TODO: get(textId, callback):
	    // call the callback with the returned value as soon as the text is available
	    // Handle some cases where texts (txt) may be used (displayed) but may still not be available.
	    
	    search: function(r) {
		var self = this;
		this.resource.get({regex: r}, function(results) {
		    $.each(results, function(key, value) {
			self.getReference(key, self.texts).setText(value);
		    });
		});
	    },

	    prefetch: function() {
		this.search('.*');
	    }
	};
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

    // to enable animation, we declare a new directive (fade-in) that create animation
    directive('fadeIn', function() {
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
	// NOTE: if content is a Text Reference object, we do not go within 'txt' property since
	// it may not yet initialised with its value.
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

