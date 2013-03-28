/*
 * index.js
 *
 */

/*
 * Angular module
 */

angular.module('index', ['common']);


/*
 * Angular Controllers
 */

function IntroductionCtrl($scope, ContentIntroduction) {
    $scope.intro = ContentIntroduction.get({}, function(intro) {
	console.debug("IntroductionCtrl.Intro.callback: ");
	console.debug(intro);
	$scope.intro = intro; // with {title: '', content: ''}
    });
}
IntroductionCtrl.$inject = ['$scope', 'ContentIntroduction'];



