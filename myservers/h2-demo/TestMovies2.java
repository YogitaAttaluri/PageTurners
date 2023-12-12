import java.sql.*;

public class TestMovies2 {

    public static void main(String[] a)
    {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/Desktop/myservers/databases/movies", "sa", "");

	    Statement st = conn.createStatement();

	    String actorname = "Will Smith";
	    String sql = "SELECT TITLE FROM MOVIES,ACTORS"
		+ " WHERE ACTORS.MOVIEID=MOVIES.MOVIEID"
		+ " AND ACTORS.ACTOR='" + actorname +"'";
	    ResultSet rs = st.executeQuery (sql);
	    System.out.println ("Results from sql=" + sql);
	    int numMovies = 0;
	    while (rs.next()) {
		String title = rs.getString ("TITLE");
		System.out.println (" title=" + title);
		numMovies ++;
	    }

            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
