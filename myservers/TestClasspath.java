
import com.google.gson.*;
import java.sql.*;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.thread.*;
import org.eclipse.jetty.http.*;
import org.eclipse.jetty.server.handler.*;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.*;
import org.apache.lucene.util.*;

public class TestClasspath {

    public static void main (String[] argv)
    {
	try { 
	    // Test the path to gson.jar
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    System.out.println (">> GSON jar found");

	    // Test the path to h2.jar
	    Class.forName("org.h2.Driver");
	    System.out.println (">> H2 jar found");

	    // Test the path to jetty-all.jar
	    Server server = new Server (40013);   
	    System.out.println (">> Jetty jar found");

	    // Test the path to the two lucene jars. This code
	    Analyzer analyzer = new StandardAnalyzer ();
	    //File indexDir = new File ("luceneMovieIndex");
	    //Directory directory = FSDirectory.open (indexDir.toPath());
	    //IndexReader indexReader = DirectoryReader.open(directory);
	    //IndexSearcher searcher = new IndexSearcher(indexReader);
	    QueryParser parser = new QueryParser ("title", analyzer);
	    Query query = parser.parse ("hello");
	    System.out.println (">> Lucene jars found");
	    System.out.println (">> CLASSPATH is correct");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
