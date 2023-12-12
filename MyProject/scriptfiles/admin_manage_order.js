let url = "http://localhost:40112/admin";
var app = angular.module('myApp', []);

app.controller('myController', ['$scope', '$window', '$http', '$rootScope', function ($scope, $window, $http, $rootScope) {
    //NAVABR LOGIC//
    // Check if the user is logged in
    if (!$rootScope.loggedInUserName) {
        var storedUserName = localStorage.getItem('loggedInUserName');
        if (!storedUserName) {
            // If no user is logged in, redirect to the login page
            $window.location.href = 'http://localhost:40112/admin_login.html';
            return; // Stop further execution of the controller
        } else {
            $rootScope.loggedInUserName = storedUserName;
        }
    }

    // Retrieve logged-in username from $rootScope or a similar service
    $scope.userName = $rootScope.loggedInUserName;

    // Redirect to login page if no user is logged in
    if (!$scope.userName) {
        $window.location.href = 'http://localhost:40112/admin_login.html';
    }

    // Navigation and logout functions
    $scope.showOrders = function () {
        // Implement navigation to orders page here
        $scope.dropdownVisible = false;
    };

    $scope.logout = function () {
        // Implement logout logic here
        localStorage.removeItem('loggedInUserName'); // Reset logged-in user info
        delete $rootScope.loggedInUserName;
        $window.location.href = 'http://localhost:40112/admin_login.html';
        $scope.dropdownVisible = false;
    };

    //NAVBAR LOGIC
    $scope.dropdownVisible = false;

    $scope.toggleDropdown = function ($event) {
        $event.stopPropagation();
        $scope.dropdownVisible = !$scope.dropdownVisible;
        adjustNavbarHeight();
    };

    function adjustNavbarHeight() {
        var navbar = document.querySelector('.navbar');
        if ($scope.dropdownVisible) {
            navbar.style.minHeight = '120px'; // Height when dropdown is visible
        } else {
            navbar.style.minHeight = 'auto'; // Original navbar height
        }
    }

    // Sample data - replace with your actual data source
    $scope.orders = [];

    $scope.statuses = ['Pending', 'Processing', 'Shipped', 'Delivered', 'Cancelled'];

    $scope.fetchOrders = function () {
        $http.post('/admin', { action: 'fetchAllOrders' })
            .then(function (response) {
                $scope.orders = response.data;
            }, function (error) {
                console.error('Error fetching orders', error);
            });
    };

    $scope.updateOrderStatus = function (order) {
        $http.post('/admin', {
            action: 'updateOrderStatus',
            orderId: order.orderId,
            newStatus: order.newStatus
        }).then(function (response) {
            // Log success and optionally refresh the order list
            console.log('Order status updated', response);
            order.status = order.newStatus; // Update the status in the frontend 
        }, function (error) {
            console.error('Error updating order status', error);
        });
    };


    $scope.calculateTotal = function (orderDetails) {
        return orderDetails.reduce(function (total, detail) {
            return total + (detail.quantity * detail.price);
        }, 0);
    };


    $scope.fetchOrders(); // Initial fetch

    //NAVBAR LOGIC//
    // Close dropdown when clicking outside of it
    angular.element($window).on('click', function (event) {
        if (!event.target.matches('.navbar-user, .navbar-user *')) {
            $scope.$apply(function () {
                $scope.dropdownVisible = false;
                adjustNavbarHeight();
            });
        }
    });
    //NAVBAR LOGIC//
}]);