app.service('CartService', ['$window', '$rootScope', function ($window, $rootScope) {
    var cartItems = JSON.parse($window.localStorage.getItem('cartItems')) || [];

    this.addBookToCart = function (book, quantity) {
        console.log('addBookToCart called with book:', book.bookName, 'Quantity:', quantity);
        var existingItem = cartItems.find(item => item.bookId === book.bookId);
        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            cartItems.push({
                bookId: book.bookId,
                bookName: book.bookName,
                quantity: quantity,
                price: book.price
            });
        }
        this.updateLocalStorage();
    };


    this.getCartItems = function () {
        return cartItems;
    };

    this.getCartItemCount = function () {
        return cartItems.reduce((total, item) => total + item.quantity, 0);
    };

    this.totalItemCount = 0;

    this.setCartItemCount = function (count) {
        this.totalItemCount = count;
    };

    this.calculateTotalItemCount = function () {
        this.totalItemCount = cartItems.reduce((total, item) => total + item.quantity, 0);
    };

    this.clearCart = function () {
        cartItems = [];
        this.updateLocalStorage();
    };

    this.broadcastCartUpdate = function () {
        $rootScope.$broadcast('cartUpdated');
    };

    this.updateLocalStorage = function () {
        $window.localStorage.setItem('cartItems', JSON.stringify(cartItems));
    };

    this.resetCart = function () {
        cartItems = [];
        this.totalItemCount = 0;
        this.updateLocalStorage();
        this.broadcastCartUpdate();
    };



    // Initialize cartItems from localStorage
    if ($window.localStorage.getItem('cartItems')) {
        cartItems = JSON.parse($window.localStorage.getItem('cartItems'));
    }
}]);
