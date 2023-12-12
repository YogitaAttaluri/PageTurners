let url = "http://localhost:40112/customer";

var app = angular.module('myApp', []);

app.controller('myController', ['$scope', '$http', '$window', 'CartService', '$rootScope', function ($scope, $http, $window, CartService, $rootScope) {
    //NAVBAR LOGIC
    // Dropdown visibility
    $scope.dropdownVisible = false;

    // Toggle dropdown menu
    $scope.toggleDropdown = function ($event) {
        $event.stopPropagation();
        $scope.dropdownVisible = !$scope.dropdownVisible;
        adjustNavbarHeight();
    };

    // Adjust navbar height based on dropdown visibility
    function adjustNavbarHeight() {
        var navbar = document.querySelector('.navbar');
        if ($scope.dropdownVisible) {
            navbar.style.minHeight = '120px';
        } else {
            navbar.style.minHeight = 'auto';
        }
    }

    // Cart item count - initially fetched from shared service
    $scope.cartItemCount = CartService.getCartItemCount();

    $rootScope.$on('cartQuantityUpdated', function (event, newCartCount) {
        console.log("I'm in navbar cartQuantityUpdate");
        $scope.cartItemCount = newCartCount;
    });

    // Watch for changes in the shared cart item count
    $scope.$watch(function () {
        return CartService.getCartItemCount();
    }, function (newCount) {
        $scope.cartItemCount = newCount;
    });

    // Check if username is in $rootScope, if not, try getting it from localStorage
    if (!$rootScope.loggedInUserName) {
        var storedUserName = localStorage.getItem('loggedInUserName');
        if (storedUserName) {
            $rootScope.loggedInUserName = storedUserName;
        }
    }

    // Retrieve logged-in username from $rootScope
    $scope.userName = $rootScope.loggedInUserName;

    // Redirect to login page if no user is logged in
    if (!$scope.userName) {
        $window.location.href = 'http://localhost:40112/customer_login.html';
    }

    // Navigation and logout functions
    $scope.showOrders = function () {
        $scope.dropdownVisible = false;
    };

    $scope.logout = function () {
        localStorage.removeItem('loggedInUserName'); // Reset logged-in user info
        delete $rootScope.loggedInUserName;
        $window.location.href = 'http://localhost:40112/customer_login.html';
        $scope.dropdownVisible = false;
    };

    //NAVBAR LOGIC
    $scope.books = {};

    // Set the default category
    var defaultCategory = 'Fiction';

    $scope.fetchBooks = function (category) {
        console.log("fetch books is working!");
        $http.get(url + '?category=' + category).then(function (response) {
            $scope.books[category] = response.data;
        }, function (error) {
            console.log('Error: ' + error);
        });
        console.log(category);
        console.log($scope.books);
    };

    $scope.openTab = function (evt, tabName) {
        var i, tabcontents, tablinks;
        console.log("I'm inside openTab");
        // Hide all tab contents and remove active class from all tab links
        tabcontents = document.getElementsByClassName("tabcontent");
        for (i = 0; i < tabcontents.length; i++) {
            tabcontents[i].style.display = "none";
        }

        tablinks = document.getElementsByClassName("tablinks");
        for (i = 0; i < tablinks.length; i++) {
            tablinks[i].className = tablinks[i].className.replace(" active", "");
        }

        // Show the current tab and add active class to the clicked tab link
        document.getElementById(tabName).style.display = "block";
        evt.currentTarget.className += " active";

        // Extract the category from the button text
        var category = evt.currentTarget.textContent.trim();
        console.log(category);
        // Fetch books for the opened tab
        $scope.fetchBooks(category);
    };

    $scope.activateDefaultTab = function () {
        // Hide all tab contents initially
        var tabcontents = document.getElementsByClassName("tabcontent");
        for (var i = 0; i < tabcontents.length; i++) {
            tabcontents[i].style.display = "none";
        }

        // Display the default tab content (Tab1)
        document.getElementById("Tab1").style.display = "block";

        // Set the default tab link (Tab1) as active
        var defaultTabLink = document.getElementById('defaultOpen');
        if (defaultTabLink) {
            defaultTabLink.className += " active";
        }
        $scope.fetchBooks(defaultCategory);
    };

    // Call activateDefaultTab on controller initialization
    $scope.activateDefaultTab();
    //NAVBAR LOGIC
    // Close dropdown when clicking outside of it
    angular.element($window).on('click', function (event) {
        if (!event.target.matches('.navbar-user, .navbar-user *')) {
            $scope.$apply(function () {
                $scope.dropdownVisible = false;
                adjustNavbarHeight();
            });
        }
    });
    //NAVBAR LOGIC
}]);