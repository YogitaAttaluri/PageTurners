
import com.google.gson.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;

// A test class
class Person {

    // Three basic types: int,double,String
    int id = 1;                                
    double height = 6.3;
    String name = "George Washington";       

    // An array.
    String[] likes = {"food", "sports"};       

    public String toString ()
    {
	return "Person: name=" + name + " id=" + id + " height=" + height;
    }

}


public class TestGson {

    public static void main (String[] argv)
    {
	test1 ();
	test2 ();
	test3 ();
    }

    static void test1 ()
    {
	// This will have the data initialized in the object.
	Person p = new Person (); 

	// Make  Gson object first.
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	// Convert an object into a Json string (which will have multiple lines).
	String jStr = gson.toJson (p);
	// Print.
	System.out.println ("JSON string representation of Person:\n" + jStr);

	// Go in the reverse direction: string to object:
	Person p2 = gson.fromJson (jStr, Person.class);
	System.out.println ("Person extracted from JSON: " + p2);
    }

    static void test2 ()
    {
	// Make a Person instance and convert to Json string:
	Person p = new Person ();
	Gson gson = new GsonBuilder().setPrettyPrinting().create();
	String jStr = gson.toJson (p);

	// We'll now do some file I/O:
	try {
	    // Write the Json to a file:
	    PrintWriter pw = new PrintWriter (new FileWriter ("test.json"));
	    pw.println (jStr);
	    pw.flush ();
	    pw.close ();

	    // Now read this.
	    LineNumberReader lnr = new LineNumberReader (new FileReader ("test.json"));
	    StringBuffer sBuf = new StringBuffer ();
	    String line = lnr.readLine ();
	    while (line !=null ) {
		sBuf.append (line);
		line = lnr.readLine ();
	    }
	    
	    // Make a single string and extract the Person instance.
	    String str = sBuf.toString ();
	    Person p2 = gson.fromJson (str, Person.class);
	    System.out.println ("Person instance from file: " + p2);
	}
	catch (Exception e) {
	    e.printStackTrace ();
	}

    }

    static void test3 ()
    {
	try {
	    // The file is already there from test2()
	    LineNumberReader lnr = new LineNumberReader (new FileReader ("test.json"));
	    StringBuffer sBuf = new StringBuffer ();
	    String line = lnr.readLine ();
	    while (line !=null ) {
		sBuf.append (line);
		line = lnr.readLine ();
	    }
	    
	    // Make a single string 
	    String str = sBuf.toString ();

	    // Now let's work with the parser:
	    JsonParser parser = new JsonParser();
	    JsonElement jsonTree = parser.parse(str);

	    // Once parsing is done, get the root.
	    JsonObject jsonObject = jsonTree.getAsJsonObject();

	    // Start extracting elements:
	    int id = jsonObject.get("id").getAsInt();
	    double height = jsonObject.get("height").getAsDouble();
	    String name = jsonObject.get("name").getAsString();
	    System.out.println ("id=" + id + " height=" + height + " name: " + name);

	    // The last one is an array:
	    JsonArray array = jsonObject.get("likes").getAsJsonArray();
	    for (JsonElement e: array) {
		String s = e.getAsString();
		System.out.println ("  " + s);
	    }
	}
	catch (Exception e) {
	    e.printStackTrace ();
	}
    }
}

