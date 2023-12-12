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

    // Retrieve logged-in username from $rootScope or a similar service
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
    $scope.orders = [];

    $scope.fetchOrders = function () {
        var userName = localStorage.getItem('loggedInUserName');
        console.log(userName);
        if (!userName) {
            // Handle no username scenario
            console.error('No username found');
            return;
        }

        $http.get('http://localhost:40112/customer?userName=' + userName)
            .then(function (response) {
                console.log(response);
                $scope.orders = response.data;
                console.log($scope.orders);
            }, function (error) {
                console.error('Error fetching orders:', error);
            });
        console.log($scope.orders);
    };
    $scope.fetchOrders();
    $scope.calculateOrderTotal = function (order) {
        let total = 0;
        order.orderDetails.forEach(function (item) {
            total += item.quantity * item.price;
        });
        return total;
    };

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
