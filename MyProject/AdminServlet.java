import java.io.*;
import java.math.BigDecimal;

import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

//@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    static Connection conn;
    static PreparedStatement preparedStatement;

    public void init() throws ServletException {
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection("jdbc:h2:~/Desktop/cs6004/MyProject/Database/MyDataBase", "sa", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        System.out.println("I'm in the servlet");
        StringBuilder jb = new StringBuilder();
        String line;
        try {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
        } catch (Exception e) {
        }
        System.out.println("Received JSON: " + jb.toString()); // Log the received JSON

        Gson gson = new Gson();
        AdminData adminData = gson.fromJson(jb.toString(), AdminData.class);
        System.out.println("Admin Data is: " + adminData);
        String result = "";
        try {
            printTables();
            switch (adminData.action) {
                case "login":
                    // This action comes from admin_login.html
                    result = adminLogin(adminData.username, adminData.password);
                    break;
                case "addBook":
                case "editBook":
                case "deleteBook":
                case "fetchAllBooks":
                    // These actions come from admin_inventory.html
                    result = handleInventoryActions(adminData);
                    break;
                case "addUser":
                case "editUser":
                case "deleteUser":
                case "fetchAllUsers":
                    // These actions come from admin_inventory.html
                    result = handleUserActions(adminData);
                    break;
                case "fetchAllOrders":
                    result = fetchAllOrders();
                    break;
                case "updateOrderStatus":
                    result = updateOrderStatus(adminData.orderId, adminData.newStatus);
                    break;
            }
        } catch (SQLException e) {
            result = "{\"error\": \"" + e.getMessage() + "\"}";
        }

        PrintWriter out = response.getWriter();
        out.println(result);
    }

    private String adminLogin(String username, String password) throws SQLException {
        String sql = "SELECT * FROM USER WHERE USERNAME = ? AND PASSWORD = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        ResultSet rs = preparedStatement.executeQuery();

        if (rs.next()) {
            if ("ADMIN".equals(rs.getString("ACCESS"))) {
                return "{\"username\": \"" + username + "\"}";
            } else {
                return "{\"error\": \"Not an admin user\"}";
            }
        } else {
            return "{\"error\": \"Invalid username or password\"}";
        }
    }

    private String handleInventoryActions(AdminData adminData) throws SQLException {
        switch (adminData.action) {
            case "addBook":
                return addBook(adminData.bookData);
            case "editBook":
                System.out.println("I'm in case editBook");
                return editBook(adminData.bookData);
            case "deleteBook":
                System.out.println("I'm in case deleteBook");
                return deleteBook(adminData.bookData.bookName);
            case "fetchAllBooks":
                return new Gson().toJson(listAllBooks());
            default:
                return "{\"error\": \"Invalid action\"}";
        }
    }

    private String handleUserActions(AdminData adminData) throws SQLException {
        switch (adminData.action) {
            case "addUser":
                System.out.println(adminData.userData);
                return addUser(adminData.userData);
            case "editUser":
                System.out.println("I'm in case editUser");
                System.out.println(adminData);
                return editUser(adminData.userData);
            case "deleteUser":
                System.out.println("I'm in case deleteUser");
                return deleteUser(adminData.userData.userName);
            case "fetchAllUsers":
                return new Gson().toJson(listAllUsers());
            default:
                return "{\"error\": \"Invalid action\"}";
        }
    }

    private String fetchAllOrders() throws SQLException {
        List<OrderData> orders = new ArrayList<>();
        String sql = "SELECT * FROM ORDERS";
        preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            OrderData order = new OrderData();
            order.orderId = rs.getInt("ORDER_ID");
            order.userId = rs.getInt("USER_ID");
            order.orderDate = rs.getDate("ORDER_DATE");
            order.status = rs.getString("STATUS");

            order.orderDetails = getOrderDetailsForOrder(order.orderId);

            orders.add(order);
        }
        return new Gson().toJson(orders);
    }

    private List<OrderData.OrderDetail> getOrderDetailsForOrder(int orderId) throws SQLException {
        List<OrderData.OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT * FROM ORDER_DETAILS WHERE ORDER_ID = ?";

        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setInt(1, orderId);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            OrderData.OrderDetail detail = new OrderData().new OrderDetail();
            detail.detailId = rs.getInt("DETAIL_ID");
            detail.bookId = rs.getInt("BOOK_ID");
            detail.bookName = rs.getString("BOOK_NAME");
            detail.quantity = rs.getInt("QUANTITY");
            detail.price = rs.getBigDecimal("PRICE");
            orderDetails.add(detail);
        }

        return orderDetails;
    }

    private String updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE ORDERS SET STATUS = ? WHERE ORDER_ID = ?";
        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, orderId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                return "{\"result\": \"Order status updated successfully\"}";
            } else {
                return "{\"error\": \"No order found with the given ID\"}";
            }
        }
    }

    /* BOOKS */
    private String addBook(BookData bookData) throws SQLException {
        String sql = "INSERT INTO BOOK (BOOK_NAME, AUTHOR, CATEGORY, PRICE, BOOK_URL, SUMMARY, RATING) VALUES (?, ?, ?, ?, ?, ?, ?)";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, bookData.bookName);
        preparedStatement.setString(2, bookData.author);
        preparedStatement.setString(3, bookData.category);
        preparedStatement.setBigDecimal(4, bookData.price);
        preparedStatement.setString(5, bookData.imageUrl);
        preparedStatement.setString(6, bookData.summary);
        preparedStatement.setBigDecimal(7, bookData.rating);
        preparedStatement.executeUpdate();
        return "{\"result\": \"Book added successfully\"}";
    }

    private String editBook(BookData bookData) throws SQLException {
        String sql = "UPDATE BOOK SET BOOK_NAME = ?, AUTHOR = ?, CATEGORY = ?, PRICE = ?, BOOK_URL = ?, SUMMARY = ?, RATING = ? WHERE BOOK_ID = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, bookData.bookName);
        preparedStatement.setString(2, bookData.author);
        preparedStatement.setString(3, bookData.category);
        preparedStatement.setBigDecimal(4, bookData.price);
        preparedStatement.setString(5, bookData.imageUrl);
        preparedStatement.setString(6, bookData.summary);
        preparedStatement.setBigDecimal(7, bookData.rating);
        preparedStatement.setInt(8, bookData.bookId);
        preparedStatement.executeUpdate();
        return "{\"result\": \"Book updated successfully\"}";
    }

    private String deleteBook(String bookName) throws SQLException {
        System.out.println("I'm inside deleteBook logic and the bookName is :" + bookName);
        String sql = "DELETE FROM BOOK WHERE BOOK_NAME = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, bookName);
        preparedStatement.executeUpdate();
        return "{\"result\": \"Book deleted successfully\"}";
    }

    private List<BookData> listAllBooks() throws SQLException {
        List<BookData> books = new ArrayList<>();
        String sql = "SELECT * FROM BOOK";
        preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();

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
        return books;
    }

    /* BOOKS */
    /* USERS */
    private String addUser(UserData userData) throws SQLException {
        String sql = "INSERT INTO USER (USERNAME, EMAIL, PASSWORD, ACCESS) VALUES (?, ?, ?, ?)";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, userData.userName);
        preparedStatement.setString(2, userData.email);
        preparedStatement.setString(3, userData.password);
        preparedStatement.setString(4, userData.access);
        preparedStatement.executeUpdate();
        return "{\"result\": \"User added successfully\"}";
    }

    private String editUser(UserData userData) throws SQLException {
        System.out.println("I'm inside editUser in servlet");
        System.out.println("Received userData: " + userData);
        if (userData == null) {
            System.out.println("userData is null");
            return "{\"error\": \"userData is null\"}";
        }
        String sql = "UPDATE USER SET EMAIL = ?, PASSWORD = ?, ACCESS = ? WHERE USERNAME = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, userData.userName);
        preparedStatement.setString(2, userData.email);
        preparedStatement.setString(3, userData.password);
        preparedStatement.setString(4, userData.access);
        preparedStatement.executeUpdate();
        return "{\"result\": \"User updated successfully\"}";
    }

    private String deleteUser(String userName) throws SQLException {
        System.out.println("I'm inside deleteUser logic and the userName is :" + userName);
        String sql = "DELETE FROM USER WHERE USERNAME = ?";
        preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, userName);
        preparedStatement.executeUpdate();
        return "{\"result\": \"User deleted successfully\"}";
    }

    private List<UserData> listAllUsers() throws SQLException {
        List<UserData> users = new ArrayList<>();
        String sql = "SELECT * FROM USER";
        preparedStatement = conn.prepareStatement(sql);
        ResultSet rs = preparedStatement.executeQuery();

        while (rs.next()) {
            UserData user = new UserData();
            user.userId = rs.getInt("USER_ID");
            user.userName = rs.getString("USERNAME");
            user.email = rs.getString("EMAIL");
            user.password = rs.getString("PASSWORD");
            user.access = rs.getString("ACCESS");
            users.add(user);
        }
        return users;
    }

    public void printTables() throws SQLException {
        System.out.println("Printing USER Table:");
        printTable("SELECT * FROM USER");

        System.out.println("Printing BOOK Table:");
        printTable("SELECT * FROM BOOK");

        System.out.println("Printing ORDERS Table:");
        printTable("SELECT * FROM ORDERS");
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

    /* USERS */
    public void destroy() {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    class AdminData {
        String username;
        String password;
        String action;
        BookData bookData;
        UserData userData;
        int orderId;
        String newStatus;
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

    class UserData {
        int userId;
        String userName;
        String email;
        String password;
        String access;
    }

    // Inner class to represent order data
    class OrderData {
        private int orderId;
        private int userId;
        private Date orderDate;
        private String status;
        private List<OrderDetail> orderDetails;

        public OrderData() {
            orderDetails = new ArrayList<>();
        }

        // Inner class for OrderDetail
        class OrderDetail {
            private int detailId;
            private int bookId;
            private String bookName;
            private int quantity;
            private BigDecimal price;
        }
    }
}
