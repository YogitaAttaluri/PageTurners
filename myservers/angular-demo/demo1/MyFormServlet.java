import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import com.google.gson.*;

public class MyFormServlet extends HttpServlet {

    public void doPost(HttpServletRequest req, HttpServletResponse resp) 
    {
	System.out.println ("MyFormServlet: doPost");
	handleRequest(req, resp);
    }
    

    public void doGet(HttpServletRequest req, HttpServletResponse resp) 
    {
	System.out.println ("MyFormServlet: doGet");
	handleRequest(req, resp);
    }
    

    public void handleRequest(HttpServletRequest req, HttpServletResponse resp)    {
	try {
	    StringBuffer sbuf = null;
	    BufferedReader bufReader = null;
	    String inputStr = null;
	
	    bufReader = req.getReader ();
	    sbuf = new StringBuffer ();
	    while ((inputStr = bufReader.readLine()) != null) {
		sbuf.append (inputStr);
	    }

	    // What's in the buffer is the entire JSON string.
	    String jStr = sbuf.toString();
	    System.out.println ("Received: " + jStr);

	    // Parse out the username from JSON:
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    JsonParser parser = new JsonParser ();
	    JsonElement jsonTree = parser.parse (jStr);
	    JsonObject jsonObject = jsonTree.getAsJsonObject ();
	    String username = jsonObject.get("username").getAsString();
	    System.out.println ("username=" + username);
	    

	    // Put the response together.
	    resp.setContentType("text/html");
	    resp.setCharacterEncoding("UTF-8");
	    resp.getWriter().write("Welcome " + username + " !");
	    // The javascript program receives this string as the
	    // "response" and uses that to plug into the variable
	    // that's specified in the HTML page. The HTML is then
	    // re-rendered by the browser.
	} 
	catch (Exception e) {
	    e.printStackTrace();
	}
    }

}
