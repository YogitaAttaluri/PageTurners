let url = "http://localhost:40112/customer";
var app = angular.module('myApp', []);

app.controller('myController', ['$scope', '$window', 'CartService', '$rootScope', '$http', function ($scope, $window, CartService, $rootScope, $http) {
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
        // Implement logout logic here
        localStorage.removeItem('loggedInUserName'); // Reset logged-in user info
        delete $rootScope.loggedInUserName;
        $window.location.href = 'http://localhost:40112/customer_login.html';
        $scope.dropdownVisible = false;
    };

    //NAVBAR LOGIC
    // Function to fetch book details
    $scope.fetchBookDetails = function (bookId) {
        $http.get(url + '?bookId=' + bookId).then(function (response) {
            $scope.book = response.data;
        }, function (error) {
            console.log('Error: ' + error);
        });
    };

    // Get bookId from URL and fetch book details
    var bookId = getQueryParam("bookId");
    if (bookId) {
        $scope.fetchBookDetails(bookId);
    }

    $scope.quantity = parseInt(localStorage.getItem('currentBookQuantity')) || 0;

    // Update quantity function
    $scope.updateQuantity = function (amount) {
        var newQuantity = $scope.quantity + amount;
        if (newQuantity >= 0) {
            $scope.quantity = newQuantity;
            localStorage.setItem('currentBookQuantity', $scope.quantity);
        }
    };

    // Add to cart function
    $scope.addToCart = function () {
        CartService.addBookToCart($scope.book, $scope.quantity);
        CartService.calculateTotalItemCount();
        $rootScope.$emit('cartQuantityUpdated', CartService.getCartItemCount());
    };

    // Function to parse query parameters
    function getQueryParam(param) {
        var searchParams = new URLSearchParams(window.location.search);
        return searchParams.get(param);
    }
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
