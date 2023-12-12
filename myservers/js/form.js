/* 
 * Description - AngularJS Script for Form Submission
 * Created By - Yatin Batra  
 * */

var helloAjaxApp = angular.module("myApp", []);

helloAjaxApp.controller("UserController", [ '$scope', '$http', function($scope, $http) {

	$http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded; charset=utf-8";
	$scope.sendData = function() {
		$http({
			url : 'http://localhost:8080/form',
			method : "POST",
			data : {
				'username' : $scope.name
			}
		}).then(function(response) {
			/**** Success Case ****/
			console.log("Success -> " + response.data);
			$scope.msgFromServlet = response.data;
		}, function(response) {
			/**** Failure Case ****/
			console.log("Failure -> " + response.data);
			$scope.msgFromServlet = response.data;
		});
	};
} ]);
