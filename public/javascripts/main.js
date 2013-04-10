/*
 * main.js
 */


angular.module('segl', ['common', 'ui']).

    // configure view routes
    config(['$routeProvider', '$locationProvider', function($routeProvider, $locationProvider) {

	$routeProvider.
	    // index
	    when('/index', {
		templateUrl: '/templates/index',
		controller: IndexCtrl
	    }).

	    // meetings
	    when('/meetings/agenda', {
		templateUrl: '/templates/meetings/agenda',
		controller: AgendaCtrl
	    }).
	    when('/meetings/sessions', {
		templateUrl: '/templates/meetings/sessions',
		controller: SessionsCtrl
	    }).
	    when('/meetings/speakers', {
		templateUrl: '/templates/meetings/speakers',
		controller: SpeakersCtrl
	    }).

	    // address
	    when('/address/map', {
		templateUrl: '/templates/address/map',
		controller: MapCtrl
	    }).
	    when('/address/contact', {
		templateUrl: '/templates/address/contact',
		controller: ContactCtrl
	    }).

	    // sponsors
	    when('/sponsors/crossing-tech', {
		templateUrl: '/templates/sponsors/crossing-tech',
		controller: XtechCtrl
	    }).
	    when('/sponsors/epfl', {
		templateUrl: '/templates/sponsors/epfl',
		controller: EpflCtrl
	    }).

	    // admin
	    when('/admin/edit-introduction', {
		templateUrl: '/templates/admin/edit-introduction',
		controller: EditIntroductionCtrl
	    }).
	    when('/admin/edit-announcement', {
		templateUrl: '/templates/admin/edit-announcement',
		controller: EditAnnouncementCtrl
	    }).
	    when('/admin/edit-sessions', {
		templateUrl: '/templates/admin/edit-sessions',
		controller: EditSessionsCtrl
	    }).
	    when('/admin/edit-password', {
		templateUrl: '/templates/admin/edit-password',
		controller: EditPasswordCtrl
	    }).

	    otherwise({redirectTo: '/index'});
    }]);


