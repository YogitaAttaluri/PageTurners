// This program creates the example FLIGHT database (db) from scratch.
// If the FLIGHT db already exists, it is first deleted, and then created
// afresh. The assumption before running the program is that the
// H2 database server is already running. To do so, execute
//       java -cp h2*.jar org.h2.tools.Server -tcp -pg
// in the directory where the h2.jar is located.

// java.sql has many objects like Statement, Connection, ResultSet
// that will be useful for accessing the database from java.
import java.sql.*;

public class MyDB {
    static Statement statement;

    public static void main(String[] args) {
        try {
            // The first step is to load the driver and use it to open
            // a connection to the H2 server (that should be running).
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:h2:~/Desktop/cs6004/MyProject/Database/MyDataBase",
                    "sa",
                    "");

            // If the connection worked, we'll reach here (otherwise an
            // exception is thrown.

            // Now make a statement, which is the object used to issue
            // queries.
            statement = conn.createStatement();

            // The user table:
            if (!tableExists(conn, "USER")) {
                makeUserTable();
                printTable("USER", 5);
            }
            // The book table:
            if (!tableExists(conn, "BOOK")) {
                makeBookTable();
                printTable("BOOK", 8);
            }
            // The order table:
            if (!tableExists(conn, "ORDERS")) {
                makeOrderTable();
                printTable("ORDERS", 4);
            }
            // The order details table:
            if (!tableExists(conn, "ORDER_DETAILS")) {
                makeOrderDetailsTable();
                printTable("ORDER_DETAILS", 6);
            }
            // Close the connection, and we're done.
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData dbm = conn.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        return tables.next();
    }

    static void printTable(String tableName, int numColumns)
            throws SQLException {
        // Build the SELECT query string:
        String sql = "SELECT * FROM " + tableName;

        // Execute at the database, which returns rows that are
        // placed into the ResultSet object.
        ResultSet rs = statement.executeQuery(sql);

        // Now extract the results from ResultSet
        System.out.println("\nRows from " + tableName + ":");
        while (rs.next()) {
            String row = "Row: ";
            for (int i = 1; i <= numColumns; i++) {
                String s = rs.getString(i);
                row += " " + s;
            }
            System.out.println(row);
        }
    }

    static void makeUserTable()
            throws SQLException {
        // Get rid of any existing table by this name.
        String sql = "DROP TABLE IF EXISTS USER";
        statement.executeUpdate(sql);

        // Now make a fresh (but empty) table.
        sql = "CREATE TABLE USER (USER_ID INT PRIMARY KEY AUTO_INCREMENT, USERNAME VARCHAR(25), EMAIL VARCHAR(50), PASSWORD VARCHAR(12), ACCESS VARCHAR(5))";
        statement.executeUpdate(sql);

        // Insert rows one by one.
        sql = "INSERT INTO USER VALUES (1, 'yogita', 'yogitalakshmi0805@gmail.com', 'abc123', 'USER')";
        statement.executeUpdate(sql);
        sql = "INSERT INTO USER VALUES (2, 'Admin', 'yogitalakshmi080599@gmail.com', 'abc123', 'ADMIN')";
        statement.executeUpdate(sql);
    }

    static void makeBookTable()
            throws SQLException {
        String sql = "DROP TABLE IF EXISTS BOOK";
        statement.executeUpdate(sql);
        sql = "CREATE TABLE BOOK (BOOK_ID INT PRIMARY KEY AUTO_INCREMENT, BOOK_NAME VARCHAR(100), AUTHOR VARCHAR(100), CATEGORY VARCHAR(50), PRICE DECIMAL(10, 2), BOOK_URL VARCHAR(1000), SUMMARY VARCHAR(10000), RATING DECIMAL(3, 1))";
        statement.executeUpdate(sql);

        sql = "INSERT INTO BOOK VALUES (1, 'Onyx', 'Jennifer L. Armentrout', 'Fiction', 32.00, 'https://cdn.kobo.com/book-images/d9fe6dd6-abc5-4eab-bbd8-15af1853848a/353/569/90/False/onyx-19.jpg', 'BEING CONNECTED TO DAEMON BLACK SUCKS…\n"
                + //
                "\n" + //
                "Thanks to his alien mojo, Daemon’s determined to prove what he feels for me is more than a product of our bizarro connection. So I’ve sworn him off, even though he’s running more hot than cold these days. But we’ve got bigger problems.', 4.5)";
        statement.executeUpdate(sql);
    }

    static void makeOrderTable()
            throws SQLException {
        String sql = "DROP TABLE IF EXISTS ORDERS";
        statement.executeUpdate(sql);
        sql = "CREATE TABLE ORDERS (ORDER_ID INT PRIMARY KEY AUTO_INCREMENT, USER_ID INT REFERENCES USER(USER_ID), ORDER_DATE DATE, STATUS VARCHAR(255))";
        statement.executeUpdate(sql);

        sql = "INSERT INTO ORDERS VALUES (1, 1, '2023-12-04', 'PROCESSED')";
        statement.executeUpdate(sql);
    }

    static void makeOrderDetailsTable()
            throws SQLException {
        String sql = "DROP TABLE IF EXISTS ORDER_DETAILS";
        statement.executeUpdate(sql);
        sql = "CREATE TABLE ORDER_DETAILS (DETAIL_ID INT PRIMARY KEY AUTO_INCREMENT, ORDER_ID INT REFERENCES ORDERS(ORDER_ID), BOOK_ID INT REFERENCES BOOK(BOOK_ID), BOOK_NAME VARCHAR(100) REFERENCES BOOK(BOOK_NAME), QUANTITY INT, PRICE DECIMAL(10, 2))";
        statement.executeUpdate(sql);

        sql = "INSERT INTO ORDER_DETAILS VALUES (1, 1, 1, 'Onyx', 2, 32.00)";
        statement.executeUpdate(sql);
    }

}
