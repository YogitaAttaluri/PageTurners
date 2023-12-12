import java.io.*;
import java.math.BigDecimal;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

//@WebServlet("/customer")
public class CustomerServlet extends HttpServlet {

    static Connection conn;
    static Statement statement;

    public void init() throws ServletException {
        try {
            MyDB.main(null);
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:~/Desktop/cs6004/MyProject/Database/MyDataBase", "sa", "");
            statement = conn.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Extract data from request
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        } catch (Exception e) {
        }

        Gson gson = new Gson();
        UserData userData = gson.fromJson(jb.toString(), UserData.class);
        System.out.println("Received username: " + userData.username);
        String result = ""; // Declare the result variable
        if ("placeOrder".equals(userData.action)) {
            result = placeOrder(userData);
        }

        try {
            // Perform action based on the request type;
            if ("login".equals(userData.action)) {
                result = loginUser(userData.username, userData.password);
            } else if ("signup".equals(userData.action)) {
                result = signupUser(userData.username, userData.email, userData.password);
            }
            printTables();
            // Send response back to client
            PrintWriter out = response.getWriter();
            out.println(result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String category = request.getParameter("category");
        String bookId = request.getParameter("bookId");
        String userName = request.getParameter("userName");

        if (category != null) {
            // Fetch books by category
            List<BookData> books = fetchBooksByCategory(category);
            String jsonBooks = new Gson().toJson(books);
            PrintWriter out = response.getWriter();
            out.println(jsonBooks);
        }

        if (bookId != null) {
            // Fetch a single book by bookId
            BookData book = fetchBookById(bookId);
            String jsonBook = new Gson().toJson(book);
            PrintWriter out = response.getWriter();
            out.println(jsonBook);
        }

        if (userName != null) {
            // Fetch orders for a specific user by userName
            List<OrderData> orders = fetchOrdersByUserName(userName);
            String jsonOrders = new Gson().toJson(orders);
            PrintWriter out = response.getWriter();
            out.println(jsonOrders);
        }
    }

    private String loginUser(String username, String password) {
        // Logic to verify user from database
        // Return user details or error message
        try {
            String sql = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // User found
                Gson gson = new Gson();
                UserData userData = new UserData();
                userData.username = rs.getString("USERNAME");
                userData.email = rs.getString("EMAIL");
                return gson.toJson(userData);
            } else {
                // User not found
                return "{\"error\": \"Invalid username or password\"}";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Database error occurred\"}";
        }
    }

    private String signupUser(String username, String email, String password) {
        // Logic to add user to database
        // Return success or error message
        try {
            String sql = "INSERT INTO USER (USERNAME, EMAIL, PASSWORD, ACCESS) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, "USER"); // Default access level
            System.out.println(username);
            System.out.println(email);
            System.out.println(password);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    long userId = generatedKeys.getLong(1);
                    return "{\"userId\": " + userId + "}";
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "{\"error\": \"Database error occurred\"}";
        }
    }

    private List<BookData> fetchBooksByCategory(String category) {
        List<BookData> books = new ArrayList<>();
        try {
            String sql = "SELECT * FROM BOOK WHERE CATEGORY = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                BookData book = new BookData();
                book.bookId = rs.getInt("BOOK_ID");
                book.bookName = rs.getString("BOOK_NAME");
                book.author = rs.getString("AUTHOR");
                book.category = rs.getString("CATEGORY");
                book.price = rs.getBigDecimal("PRICE");
                book.imageUrl = rs.getString("BOOK_URL");
                book.summary = rs.getString("SUMMARY");
                book.rating = rs.getBigDecimal("RATING");
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private BookData fetchBookById(String bookId) {
        BookData book = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT * FROM BOOK WHERE BOOK_ID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(bookId));

            rs = pstmt.executeQuery();

            if (rs.next()) {
                book = new BookData();
                book.bookId = rs.getInt("BOOK_ID");
                book.bookName = rs.getString("BOOK_NAME");
                book.author = rs.getString("AUTHOR");
                book.category = rs.getString("CATEGORY");
                book.price = rs.getBigDecimal("PRICE");
                book.imageUrl = rs.getString("BOOK_URL");
                book.summary = rs.getString("SUMMARY");
                book.rating = rs.getBigDecimal("RATING");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return book;
    }

    private String placeOrder(UserData userData) {
        try {
            // Begin transaction
            conn.setAutoCommit(false);

            // Insert into ORDERS table
            String insertOrderSql = "INSERT INTO ORDERS (USER_ID, ORDER_DATE, STATUS) VALUES (?, CURRENT_DATE, ?)";
            PreparedStatement orderPstmt = conn.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            orderPstmt.setInt(1, getUserId(userData.username));
            System.out.println(userData.username);
            orderPstmt.setString(2, "PENDING");
            orderPstmt.executeUpdate();
            ResultSet orderRs = orderPstmt.getGeneratedKeys();
            int orderId = 0;
            if (orderRs.next()) {
                orderId = orderRs.getInt(1);
            }

            // Insert into ORDER_DETAILS table
            String insertDetailSql = "INSERT INTO ORDER_DETAILS (ORDER_ID, BOOK_ID, BOOK_NAME, QUANTITY, PRICE) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement detailPstmt = conn.prepareStatement(insertDetailSql);
            for (OrderDetail detail : userData.orderDetails.cartItems) {
                detailPstmt.setInt(1, orderId);
                detailPstmt.setInt(2, detail.bookId);
                detailPstmt.setString(3, detail.bookName);
                detailPstmt.setInt(4, detail.quantity);
                detailPstmt.setBigDecimal(5, detail.price);
                detailPstmt.executeUpdate();
            }

            // Commit transaction
            conn.commit();
            return "{\"result\": \"Order placed successfully\"}";
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return "{\"error\": \"Error placing order\"}";
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<OrderData> fetchOrdersByUserName(String userName) {
        List<OrderData> orders = new ArrayList<>();
        try {
            // Adjusted SQL query to reflect the correct column name
            String sql = "SELECT * FROM ORDERS WHERE USER_ID IN (SELECT USER_ID FROM USER WHERE USERNAME = ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderData order = new OrderData();
                order.orderId = rs.getInt("ORDER_ID");
                order.userId = rs.getInt("USER_ID");
                order.orderDate = rs.getDate("ORDER_DATE");
                order.status = rs.getString("STATUS");

                // Fetch order details for each order
                order.orderDetails = fetchOrderDetails(order.orderId);

                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    private List<OrderDetail> fetchOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        try {
            String sql = "SELECT * FROM ORDER_DETAILS WHERE ORDER_ID = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.detailId = rs.getInt("DETAIL_ID");
                detail.bookId = rs.getInt("BOOK_ID");
                detail.bookName = rs.getString("BOOK_NAME");
                detail.quantity = rs.getInt("QUANTITY");
                detail.price = rs.getBigDecimal("PRICE");
                // Calculate total amount for the item
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    private int getUserId(String username) throws SQLException {
        System.out.println(username);
        String sql = "SELECT USER_ID FROM USER WHERE USERNAME = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("USER_ID");
        } else {
            throw new SQLException("User not found");
        }
    }

    class OrderDetails {
        List<OrderDetail> cartItems;

        // Constructor
        public OrderDetails(List<OrderDetail> cartItems) {
            this.cartItems = cartItems;
        }

        public List<OrderDetail> getCartItems() {
            return cartItems;
        }

        public void setCartItems(List<OrderDetail> cartItems) {
            this.cartItems = cartItems;
        }

    }

    public void printTables() throws SQLException {
        System.out.println("Printing USER Table:");
        printTable("SELECT * FROM USER");

        System.out.println("Printing BOOK Table:");
        printTable("SELECT * FROM BOOK");

        System.out.println("Printing ORDERS Table:");
        printTable("SELECT * FROM ORDERS");

        System.out.println("Printing ORDER_DETAILS Table:");
        printTable("SELECT * FROM ORDER_DETAILS");
    }

    private void printTable(String query) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Print column names
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // Print rows
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        }
    }

    // Inner class to handle user data
    class UserData {
        int userId;
        String username;
        String email;
        String password;
        String action;
        OrderDetails orderDetails;
    }

    class BookData {
        int bookId;
        String bookName;
        String author;
        String category;
        BigDecimal price;
        String imageUrl;
        String summary;
        BigDecimal rating;
    }

    class OrderData {
        int orderId;
        int userId;
        int bookId;
        Date orderDate;
        String status;
        List<OrderDetail> orderDetails;
    }

    class OrderDetail {
        int detailId;
        int orderId;
        int bookId;
        String bookName;
        int quantity;
        BigDecimal price;
    }

    public void destroy() {
        // Close database connections if necessary
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
