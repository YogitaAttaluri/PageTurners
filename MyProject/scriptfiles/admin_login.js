let url = "http://localhost:40112/admin";
var app = angular.module('myApp', []);
app.controller('myController', ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope) {

    // Function to handle login
    $scope.login = function () {
        console.log("I'm inside login");
        var data = {
            username: $scope.user.username,
            password: $scope.user.password,
            action: 'login'
        };

        $http.post('/admin', data).then(function (response) {
            if (response.data.error) {
                $scope.errorMessage = response.data.error;
            } else {
                console.log("I'm in! I should be able to go to main page");
                $rootScope.loggedInUserName = response.data.username; // Set in $rootScope
                console.log("$rootScope.loggedInUserName");
                localStorage.setItem('loggedInUserName', response.data.username); // Storing the username
                console.log('Username set in localStorage:', localStorage.getItem('loggedInUserName'));
                window.location.href = 'http://localhost:40112/admin_main.html';
            }
        }, function (error) {
            $scope.errorMessage = 'Login details are incorrect or not present';
        });
    };

    // Function to submit forms
    $scope.submit = function (type) {
        $scope.login();
    };
}]);
