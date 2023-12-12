import java.sql.*;

public class TestMovies {

    public static void main(String[] a)
    {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/Desktop/myservers/databases/movies", "sa", "");

	    Statement st = conn.createStatement();

	    // Get all rows from the MOVIES table:
	    String sql = "SELECT * FROM MOVIES ORDER BY MOVIEID";
	    ResultSet rs = st.executeQuery (sql);
	    System.out.println ("Results from sql=" + sql);
	    int numMovies = 0;
	    while (rs.next()) {
		int id = rs.getInt ("MOVIEID");
		String title = rs.getString ("TITLE");
		System.out.println ("id=" + id + " title=" + title);
		numMovies ++;
	    }

	    // Get all rows from the ACTORS table:
	    sql = "SELECT * FROM ACTORS";
	    rs = st.executeQuery (sql);
	    System.out.println ("Results from sql=" + sql);
	    int numActorRows = 0;
	    while (rs.next()) {
		int id = rs.getInt ("MOVIEID");
		String actor = rs.getString ("ACTOR");
		System.out.println ("id=" + id + " actor=" + actor);
		numActorRows ++;
	    }

	    // Get all rows from the GENRES table:
	    sql = "SELECT * FROM GENRES";
	    rs = st.executeQuery (sql);
	    System.out.println ("Results from sql=" + sql);
	    int numGenreRows = 0;
	    while (rs.next()) {
		int id = rs.getInt ("MOVIEID");
		String genre = rs.getString ("GENRE");
		System.out.println ("id=" + id + " genre=" + genre);
		numGenreRows ++;
	    }

	    System.out.println ("SUMMARY");
	    System.out.println ("Movies table: # rows=" + numMovies);
	    System.out.println ("Actors table: # rows=" + numActorRows);
	    System.out.println ("Genres table: # rows=" + numGenreRows);

            conn.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
