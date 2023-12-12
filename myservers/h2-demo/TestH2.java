import java.sql.*;

public class TestH2 {

    public static void main(String[] a)
    {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/Desktop/myservers/databases/test", "sa", "");

            // add application code here
	    Statement st = conn.createStatement();
	    
	    // DROP TABLE IF EXISTS TEST;
	    String sql = "DROP TABLE IF EXISTS TEST";
	    st.executeUpdate (sql);
	    // CREATE TABLE TEST (ID INT PRIMARY KEY, NAME VARCHAR(255));
	    sql = "CREATE TABLE TEST (ID INT PRIMARY KEY, MESSAGE VARCHAR(255))";
	    st.executeUpdate (sql);
	    // INSERT INTO TEST VALUES (1, 'Hello');
	    sql = "INSERT INTO TEST VALUES (1, 'Hello')";
	    st.executeUpdate (sql);
	    // INSERT INTO TEST VALUES (2, 'World');
	    sql = "INSERT INTO TEST VALUES (2, 'World')";
	    st.executeUpdate (sql);

	    // SELECT * FROM TEST ORDER BY ID;
	    sql = "SELECT * FROM TEST ORDER BY ID";
	    ResultSet rs = st.executeQuery (sql);
	    System.out.println ("Results from SELECT:");
	    while (rs.next()) {
		int id = rs.getInt ("ID");
		String name = rs.getString ("MESSAGE");
		System.out.println ("id=" + id + " name=" + name);
	    }
            conn.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
