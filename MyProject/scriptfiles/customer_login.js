let url = "http://localhost:40112/customer";
// Define the module and controller
var app = angular.module('myApp', []);
app.controller('myController', ['$scope', '$http', '$rootScope', function ($scope, $http, $rootScope) {

    // Function to handle login
    $scope.login = function () {
        console.log("I'm inside login");
        var data = {
            username: $scope.login.username,
            password: $scope.login.password,
            action: 'login'
        };

        $http.post('/customer', data).then(function (response) {
            if (response.data.error) {
                $scope.errorMessage = response.data.error;
            } else {
                console.log("I'm in! I should be able to go to main page");
                $rootScope.loggedInUserName = response.data.username; // Set in $rootScope
                localStorage.setItem('loggedInUserName', response.data.username); // Storing the username
                console.log('Username set in localStorage:', localStorage.getItem('loggedInUserName'));
                window.location.href = 'http://localhost:40112/customer_main.html';
            }
        }, function (error) {
            $scope.errorMessage = 'Login details are incorrect or not present';
        });
    };

    // Function to handle signup
    $scope.signup = function () {
        console.log("I'm inside signup");
        console.log($scope.signup.email);
        // Validate inputs
        if ($scope.signup.password.length < 6 || $scope.signup.password.length > 20) {
            $scope.errorMessage1 = 'Password should be between 6 and 20 characters';
            return;
        }
        if (!$scope.signup.email.includes('@')) {
            $scope.errorMessage1 = 'Email ID must contain @';
            return;
        }

        var data = {
            username: $scope.signup.username,
            email: $scope.signup.email,
            password: $scope.signup.password,
            action: 'signup'
        };

        $http.post('/customer', data).then(function (response) {
            if (response.data.error) {
                $scope.errorMessage1 = response.data.error;
            } else {
                $scope.successMessage = 'Signup successful! Please Login.';
            }
        }, function (error) {
            $scope.errorMessage1 = 'Username already exists';
        });
    };

    // Function to submit forms
    $scope.submit = function (type) {
        if (type === 'login') {
            $scope.login();
        } else if (type === 'signup') {
            $scope.signup();
        }
    };
}]);



function openTab(evt, tabName) {
    // Hide all tab contents and remove active class from all tab links
    var tabcontents = document.getElementsByClassName("tabcontent");
    for (var i = 0; i < tabcontents.length; i++) {
        tabcontents[i].style.display = "none";
    }

    var tablinks = document.getElementsByClassName("tablinks");
    for (var i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }

    // Show the current tab content and add active class to the clicked tab link
    document.getElementById(tabName).style.display = "block";
    evt.currentTarget.className += " active";

    // Move the slider
    moveSlider(evt.currentTarget);
}

function moveSlider(element) {
    var slider = document.querySelector('.slider');
    slider.style.width = element.offsetWidth + "px";
    slider.style.left = element.offsetLeft + "px";
}

// Adjust slider position on window load
window.onload = function () {
    document.getElementById("defaultOpen").click();
};

