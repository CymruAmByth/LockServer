package fileDao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public abstract class FileDao {
	public static List<String> getOutput() {
	    // Load the file with the counter
		List<String> result = new ArrayList<String>();
	    FileReader fileReader = null;
	    BufferedReader bufferedReader = null;
	    PrintWriter writer = null;
	    try {
	      File f = new File("FileCounter.initial");
	      if (!f.exists()) {
	        f.createNewFile();
	        writer = new PrintWriter(new FileWriter(f));
	        writer.println("Empty file read @ " + new Date().toString());
	      }
	      if (writer != null) {
	        writer.close();
	      }

	      fileReader = new FileReader(f);
	      bufferedReader = new BufferedReader(fileReader);
	      boolean isNotNull = true; 
	      while(isNotNull){
	    	  String line = bufferedReader.readLine();
	    	  if(line != null){
	    		  result.add(line);
	    	  } else {
	    		  isNotNull = false;
	    	  }
	      }
	    } catch (Exception ex) {
	      if (writer != null) {
	        writer.close();
	      }
	    }
	    if (bufferedReader != null) {
	      try {
	        bufferedReader.close();
	      } catch (IOException e) {
	        e.printStackTrace();
	      }
	    }
	    Collections.reverse(result);
	    return result;
	  }

	  public static void writeOutput(String message) {
		FileWriter fileWriter = null;
		PrintWriter printWriter = null;
	    try {
			fileWriter = new FileWriter("FileCounter.initial", true);
		    printWriter = new PrintWriter(fileWriter);
		    printWriter.println(new Date().toString() + ":" + message);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	    finally{
	    	if (printWriter != null) {
	    		printWriter.close();
	    	}
	    }
	  }
	  
	  public static void emptyFile() {
		  File f = new File("FileCounter.initial");
		  f.delete();
	  }

}