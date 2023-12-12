import java.io.*;
import org.eclipse.jetty.server.Handler;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.thread.*;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.handler.*;

public class MyServer {

    public static void main(String[] argv) {
        try {
            Server server = new Server(40112); // Using port 40112

            // Resource handler for serving static files (e.g., HTML, CSS, JS)
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setDirectoriesListed(true);
            resourceHandler.setWelcomeFiles(new String[] { "index.html" });
            resourceHandler.setResourceBase("."); // Set to the directory where your static files are.

            ContextHandler staticContextHandler = new ContextHandler("/");
            staticContextHandler.setHandler(resourceHandler);

            // Servlet context handler
            ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            servletContextHandler.setContextPath("/");

            // Add your CustomerServlet to the server
            servletContextHandler.addServlet(new ServletHolder(new CustomerServlet()), "/customer");

            // Add your AdminServlet to the server
            servletContextHandler.addServlet(new ServletHolder(new AdminServlet()), "/admin");

            // Combine handlers
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[] { staticContextHandler, servletContextHandler });

            server.setHandler(contexts);

            // Start the server
            server.start();
            System.out.println("CustomerServer started, ready for browser connections");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
