package fileDao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class FileDao {
	public List<String> getCount() {
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
	        writer.println("No messages");
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
	    return result;
	  }

	  public void save(String message) throws Exception {
	    FileWriter fileWriter = null;
	    PrintWriter printWriter = null;
	    fileWriter = new FileWriter("FileCounter.initial", true);
	    printWriter = new PrintWriter(fileWriter);
	    printWriter.println(message);

	    // make sure to close the file
	    if (printWriter != null) {
	      printWriter.close();
	    }
	  }

}