
var myAppModule = angular.module("myApp", []);

myAppModule.controller('myController', [ '$scope', '$http', function($scope, $http) {

    $scope.greeting = { text: "Greetings from javascript!" };

    $scope.changeText = function() {
        $scope.greeting.text = "Greetings again from javascript!";
    };

    $scope.sendData = function() {
        $http.defaults.headers.post["Content-Type"] = "multipart/form-data; charset=utf-8";
        $scope.greeting.text = "Submit clicked";
        $http({
            url : 'http://localhost:40013/form',
            method: 'POST',
            data : {
                'test' : 'teststuff', 
                'username' : $scope.name
            }
        }).then(function(response) {
            // Success Case 
            console.log("Success -> " + response.data);
            $scope.greeting.text = "Success";
            $scope.msgFromServlet = response.data;
        }, function(response) {
            // Failure Case
            console.log("Failure -> " + response.data);
            $scope.greeting.text = "Failure";
            $scope.msgFromServlet = response.data;
        });
     };
}]);
