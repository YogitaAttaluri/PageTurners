// Handle the movie-find query

import java.sql.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;


public class MovieServlet2 extends HttpServlet {

    public MovieServlet2 ()
    {
	// We aren't doing anything here, but in the future we'll
	// want to open the database here to see if a connection
	// is possible. If not, there's something wrong. Also, 
	// for a high-volume system, this is the place to create
	// a connection-pool.
    }
	 
    public void doPost (HttpServletRequest req, HttpServletResponse resp) 
    {
	// We'll print to terminal to know whether the browser used post or get.
	System.out.println ("MovieServlet2: doPost()");
	handleRequest (req, resp);
    }
    

    public void doGet (HttpServletRequest req, HttpServletResponse resp)
    {
	System.out.println ("MovieServlet2: doGet()");
	handleRequest (req, resp);
    }
    

	public void handleRequest (HttpServletRequest req, HttpServletResponse resp)
	{
		try {
			resp.setContentType ("text/html");
			resp.setCharacterEncoding ("UTF-8");

			PrintWriter out = resp.getWriter();
			// The top part of the HTML page:
			out.println ("<html>");
			out.println ("<head><title> Movie Results </title></head>");
			out.println ("<body bgcolor=\"#DDDDFF\">");
			out.println ("<h2>Movie Finder Results</h2>");
			
			// Extract names that were typed into the actor's name textfields:
			String actorName1 = req.getParameter ("actorname1");
			String actorName2 = req.getParameter ("actorname2");

			// The actual results after pulling from the database
			ArrayList<String> movieTitles = getMovieTitles (actorName1, actorName2);

			// Write out the HTML
			if ((movieTitles == null) || (movieTitles.size() == 0)) {
				out.println ("<h3> None found</h3>");
			}
			else {
				out.println ("<ol>");
				for (String title: movieTitles) {
					out.println ("<li> " + title);
				}
				out.println ("</ol>");
			}

			// The bottom part, along with the all-important flush().
			out.println ("</body>");
			out.println ("</html>");
			out.flush ();	    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	ArrayList<String> getMovieTitles (String actorname1, String actorname2)
	{
		try {
			ArrayList<String> movieTitles = new ArrayList<>();

			// Load the program that knows how to talk to the DB.
			Class.forName ("org.h2.Driver");
			Connection conn = DriverManager.getConnection ("jdbc:h2:~/Desktop/myservers/databases/movies", "sa", "");

			// Modified SQL statement to fetch common movies for both actors:
			String sql = "SELECT DISTINCT M1.TITLE FROM MOVIES M1, ACTORS A1, ACTORS A2"
				+ " WHERE A1.MOVIEID = M1.MOVIEID AND A2.MOVIEID = M1.MOVIEID"
				+ " AND A1.ACTOR = ? AND A2.ACTOR = ?";

			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, actorname1);
			st.setString(2, actorname2);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				String title = rs.getString ("TITLE");
				System.out.println ("In MovieServlet: title=" + title);
				movieTitles.add (title);
			}

			conn.close();
			return movieTitles;
		}
		catch (Exception e) {
			e.printStackTrace ();
			return null;
		}
	}
}