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

    $scope.order = {
        shippingAddress: '',
        cardNumber: '',
        cvv: '',
        expiry: '',
        zip: ''
    };

    $scope.orderPlaced = false;

    function isValidCardDetails() {
        var cardNumberValid = $scope.order.cardNumber && $scope.order.cardNumber.length >= 15 && $scope.order.cardNumber.length <= 19;
        var cvvValid = $scope.order.cvv && ($scope.order.cvv.length === 3 || $scope.order.cvv.length === 4);

        if (!cardNumberValid) {
            alert("Invalid card number. Please enter a valid number.");
            return false;
        }
        if (!cvvValid) {
            alert("Invalid CVV. Please enter a valid CVV.");
            return false;
        }

        return cardNumberValid && cvvValid;
    }

    $scope.submitOrder = function () {
        console.log("I'm in submit order function!");
        console.log("Form validity: ", $scope.orderForm.$valid);
        if ($scope.orderForm.$valid) {
            console.log("Username before sending request:", $rootScope.loggedInUserName);
            var orderData = {
                action: "placeOrder",
                username: $rootScope.loggedInUserName,
                orderDetails: {
                    cartItems: CartService.getCartItems(),
                    totalAmount: $scope.totalAmount
                }
            };
            console.log("Sending request with data:", orderData);
            $http.post('/customer', orderData, { headers: { 'Content-Type': 'application/json' } })
                .then(function (response) {
                    alert("Order successfully submitted!");
                    CartService.resetCart();
                    $window.location.href = 'http://localhost:40112/customer_orders.html';
                }, function (error) {
                    console.error('Order submission failed:', error);
                });
        } else {
            alert("Please check your input details!");
        }
    };




    $scope.previousExpiryValue = "";


    $scope.isExpiryValid = function () {
        if (!$scope.order.expiry) {
            console.log('Expiry date is empty or undefined');
            return false;
        }

        var parts = $scope.order.expiry.split('/');
        if (parts.length !== 2) {
            console.log('Expiry date format is incorrect');
            return false;
        }

        var expMonth = parseInt(parts[0], 10);
        var expYear = parseInt(parts[1], 10);
        var today = new Date();
        var currentYear = today.getFullYear() % 100;
        var currentMonth = today.getMonth() + 1;
        var isValid = expYear > currentYear || (expYear === currentYear && expMonth >= currentMonth);
        console.log('Expiry Date:', $scope.order.expiry, 'Is Valid:', isValid);
        return isValid;
    };

    $scope.totalAmount = localStorage.getItem('totalAmount');
    console.log($scope.totalAmount);

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
