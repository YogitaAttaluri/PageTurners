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

    $scope.logout = function () {
        // Implement logout logic here
        localStorage.removeItem('loggedInUserName'); // Reset logged-in user info
        delete $rootScope.loggedInUserName;
        $window.location.href = 'http://localhost:40112/admin_login.html';
        $scope.dropdownVisible = false;
    };

    //NAVBAR LOGIC
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


    // Define the API URL for the AdminServlet
    var apiUrl = 'http://localhost:40112/admin'; // URL of the AdminServlet

    function interactWithBackend(action, userData) {
        var dataToSend = {
            action: action,
        };

        // Add userData to the request only if it's provided
        if (userData) {
            dataToSend.userData = userData;
        }

        return $http.post(apiUrl, dataToSend);
    }


    // user management
    $scope.editFormVisible = false;
    $scope.addFormVisible = false;
    $scope.deleteConfirmVisible = false;
    $scope.editedUser = {};
    $scope.newUser = {};
    $scope.inputUserName = '';
    $scope.allUsersVisible = false;
    $scope.allUsers = [];

    $scope.submit = function (action) {
        $scope.editFormVisible = false;
        $scope.addFormVisible = false;
        $scope.deleteConfirmVisible = false;
        $scope.allUsersVisible = false;

        if (action === 'all-users') {
            $scope.showAllUsers();
            return;
        }
        if (!$scope.inputUserName) {
            alert('Please enter the user name.');
            return;
        }

        var userToHandle = $scope.allUsers.find(user => user.userName === $scope.inputUserName);

        switch (action) {
            case 'edit':
                if (userToHandle) {
                    console.log(userToHandle);
                    $scope.editedUser = angular.copy(userToHandle);
                    $scope.editFormVisible = true;
                } else {
                    alert('No user with that name is present.');
                }
                break;
            case 'delete':
                if (userToHandle) {
                    console.log(userToHandle);
                    $scope.deleteConfirmVisible = true;
                } else {
                    alert('No user with that name is present.');
                }
                break;
            case 'add':
                if (userToHandle) {
                    alert('User already present.');
                } else {
                    $scope.addFormVisible = true;
                    $scope.newUser = {};
                }
                break;
        }
    };

    // Function to add a user
    $scope.submitAddForm = function () {
        interactWithBackend('addUser', $scope.newUser).then(function (response) {
            alert('User added successfully!');
            $scope.addFormVisible = false;
            $scope.showAllUsers();
        }, function (error) {
            alert('Error: ' + (error.data.error || error.statusText));
        });
    };
    // Function to edit a user
    $scope.submitEditForm = function () {
        console.log("I'm in edit user in js file");
        interactWithBackend('editUser', $scope.editedUser).then(function (response) {
            alert('User edited successfully!');
            $scope.editFormVisible = false;
            $scope.showAllUsers();
        }, function (error) {
            alert('Error: ' + (error.data.error || error.statusText));
        });
    };
    // Function to delete a user
    $scope.confirmDelete = function () {
        var userToDelete = { userName: $scope.inputUserName };
        console.log($scope.inputUserName);
        interactWithBackend('deleteUser', userToDelete).then(function (response) {
            // Handle success
            if (response.data && response.data.result) {
                alert('User deleted successfully!');
            } else {
                alert('Error: ' + (response.data.error || 'Unknown error'));
            }
            $scope.deleteConfirmVisible = false;
            $scope.showAllUsers();
        }, function (error) {
            // Handle error
            if (error.data && error.data.error) {
                alert('Error: ' + error.data.error);
            } else {
                console.log(error);
                alert('An unexpected error occurred.');
            }
        });
    };

    $scope.cancelDelete = function () {
        $scope.deleteConfirmVisible = false;
    }
    // Function to show all users
    $scope.showAllUsers = function () {
        interactWithBackend('fetchAllUsers', {}).then(function (response) {
            $scope.allUsers = response.data;
            $scope.allUsersVisible = true;
        }, function (error) {
            alert('Error: ' + (error.data.error || error.statusText));
        });
    };

    // Load all books when the controller is initialized
    $scope.showAllUsers();

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