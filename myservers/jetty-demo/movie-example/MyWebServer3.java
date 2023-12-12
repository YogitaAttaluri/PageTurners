import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.thread.*;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.handler.*;

public class MyWebServer3 {

    public static void main(String[] argv) {
        try {
            Server server = new Server(40013);   

            ResourceHandler rHandler = new ResourceHandler();
            rHandler.setResourceBase(".");                     
            ContextHandler cHandler = new ContextHandler("/"); 
            cHandler.setHandler(rHandler);

            ServletContextHandler sHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            sHandler.addServlet(new ServletHolder(new MovieServlet3()),"/movieservlet3");
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[] { cHandler, sHandler });
            server.setHandler(contexts);

            server.start();
            System.out.println("Webserver started, ready for browser connections");
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
