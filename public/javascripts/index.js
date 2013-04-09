/*
 * index.js
 *
 */

/*
 * Angular Controllers
 */

function IndexCtrl($scope) {
}
IndexCtrl.$inject = ['$scope'];

function IntroductionCtrl($scope, ContentIntroduction) {
    $scope.intro = ContentIntroduction.get({}, function(intro) {
	console.debug("IntroductionCtrl.Intro.callback: ");
	console.debug(intro);
	$scope.intro = intro; // with {title: '', content: ''}
    });
}
IntroductionCtrl.$inject = ['$scope', 'ContentIntroduction'];


function AnnouncementCtrl($scope, ContentAnnouncement) {
    $scope.announce = ContentAnnouncement.get({}, function(ann) {
	console.debug("AnnouncementCtrl.announce.callback: ");
	console.debug(ann);
	$scope.announce = ann; // with {title: '', content: ''}
    });
}
AnnouncementCtrl.$inject = ['$scope', 'ContentAnnouncement'];



