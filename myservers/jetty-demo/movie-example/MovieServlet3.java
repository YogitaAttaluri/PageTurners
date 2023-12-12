import java.sql.*;
import java.util.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;

public class MovieServlet3 extends HttpServlet {

    public MovieServlet3() {
        // We'll put initialization logic here in the future
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("MovieServlet: doPost()");
        handleRequest(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        System.out.println("MovieServlet: doGet()");
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.setContentType("text/html");
            resp.setCharacterEncoding("UTF-8");

            PrintWriter out = resp.getWriter();
            out.println("<html>");
            out.println("<head><title> Movie Results </title></head>");
            out.println("<body bgcolor=\"#DDDDFF\">");
            out.println("<h2>Movie Finder Results</h2>");

            String actorName = req.getParameter("actorname");
            ArrayList<String> movieDetails = getMovieDetails(actorName);

            if ((movieDetails == null) || (movieDetails.size() == 0)) {
                out.println("<h3> None found</h3>");
            } else {
                out.println("<ol>");
                for (String detail : movieDetails) {
                    out.println("<li> " + detail);
                }
                out.println("</ol>");
            }

            out.println("</body>");
            out.println("</html>");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getMovieDetails(String actorname) {
        ArrayList<String> results = new ArrayList<>();
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/Desktop/myservers/databases/movies", "sa", "");

            String sql = "SELECT MOVIES.MOVIEID, MOVIES.TITLE, GENRES.GENRE " +
                         "FROM MOVIES, ACTORS, GENRES " +
                         "WHERE ACTORS.MOVIEID=MOVIES.MOVIEID " +
                         "AND GENRES.MOVIEID=MOVIES.MOVIEID " +
                         "AND ACTORS.ACTOR=?";
            PreparedStatement pst = conn.prepareStatement(sql);
            pst.setString(1, actorname);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int movieID = rs.getInt("MOVIEID");
                String title = rs.getString("TITLE");
                String genre = rs.getString("GENRE");
                results.add("ID: " + movieID + ", Title: " + title + " (" + genre + ")");
            }
            conn.close();
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
