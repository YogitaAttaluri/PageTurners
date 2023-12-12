/*
 * Based on this example: https://www.webcodegeeks.com/javascript/angular-js/angularjs-backend-example/
 */

var myAppModule = angular.module("myApp", []);

myAppModule.controller("UserController", [ '$scope', '$http', function($scope, $http) {

    $http.defaults.headers.post["Content-Type"] = "application/x-www-form-urlencoded; charset=utf-8";

    $scope.sendData = function() {
        $http({
            url : 'http://localhost:40013/form',
	    method : 'POST',
	    data : {
	        'test' : 'teststuff',    /* To pass something from js to servlet */
		'username' : $scope.name /* This is from the HTML page */
	    }
	}).then(function(response) {
	    /* Success */
	    console.log("Success -> " + response.data);
	    $scope.msgFromServlet = response.data;
	}, function(response) {
	    /* Failure */
	    console.log("Failure -> " + response.data);
	    $scope.msgFromServlet = 'ERROR: failure to connect';
	});
    };
} ]);
