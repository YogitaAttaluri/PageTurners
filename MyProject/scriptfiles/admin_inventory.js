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

    // Define the API URL for the AdminServlet
    var apiUrl = 'http://localhost:40112/admin'; // URL of the AdminServlet

    function interactWithBackend(action, bookData) {
        return $http.post(apiUrl, { action: action, bookData: bookData });
    }

    // Book management
    $scope.editFormVisible = false;
    $scope.addFormVisible = false;
    $scope.deleteConfirmVisible = false;
    $scope.editedBook = {};
    $scope.newBook = {};
    $scope.bookName = '';
    $scope.allBooksVisible = false;
    $scope.allBooks = [];

    $scope.submit = function (action) {
        $scope.editFormVisible = false;
        $scope.addFormVisible = false;
        $scope.deleteConfirmVisible = false;
        $scope.allBooksVisible = false;

        if (action === 'all-books') {
            $scope.showAllBooks();
            return;
        }
        if (!$scope.bookName) {
            alert('Please enter the book name.');
            return;
        }

        var bookToHandle = $scope.allBooks.find(book => book.bookName === $scope.bookName);

        switch (action) {
            case 'edit':
                if (bookToHandle) {
                    $scope.editedBook = angular.copy(bookToHandle);
                    $scope.editFormVisible = true;
                } else {
                    alert('No book with that name is present.');
                }
                break;
            case 'delete':
                if (bookToHandle) {
                    console.log(bookToHandle);
                    $scope.deleteConfirmVisible = true;
                } else {
                    alert('No book with that name is present.');
                }
                break;
            case 'add':
                if (bookToHandle) {
                    alert('Book already present.');
                } else {
                    $scope.addFormVisible = true;
                    $scope.newBook = {};
                }
                break;
        }
    };

    // Function to add a book
    $scope.submitAddForm = function () {
        interactWithBackend('addBook', $scope.newBook).then(function (response) {
            alert('Book added successfully!');
            $scope.addFormVisible = false;
            $scope.showAllBooks();
        }, function (error) {
            alert('Error: ' + (error.data.error || error.statusText));
        });
    };
    // Function to edit a book
    $scope.submitEditForm = function () {
        interactWithBackend('editBook', $scope.editedBook).then(function (response) {
            alert('Book edited successfully!');
            $scope.editFormVisible = false;
            $scope.showAllBooks();
        }, function (error) {
            alert('Error: ' + (error.data.error || error.statusText));
        });
    };
    // Function to delete a book
    $scope.confirmDelete = function () {
        var bookToDelete = { bookName: $scope.bookName };
        console.log($scope.bookName);
        interactWithBackend('deleteBook', bookToDelete).then(function (response) {
            // Handle success
            if (response.data && response.data.result) {
                alert('Book deleted successfully!');
            } else {
                alert('Error: ' + (response.data.error || 'Unknown error'));
            }
            $scope.deleteConfirmVisible = false;
            $scope.showAllBooks();
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
    // Function to show all books
    $scope.showAllBooks = function () {
        interactWithBackend('fetchAllBooks', {}).then(function (response) {
            $scope.allBooks = response.data;
            $scope.allBooksVisible = true;
        }, function (error) {
            alert('Error: ' + (error.data.error || error.statusText));
        });
    };

    // Load all books when the controller is initialized
    $scope.showAllBooks();
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