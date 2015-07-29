package users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.Scanner;


import fileDao.FileDao;
import locks.LockManager;

public class UserConnection implements Runnable {
	
    private final Socket mSocket;
    private final LockManager mLocksManager;
    private static final String SERVER_CLIENT_ID = "635248478115-khks0610shmbkpk8qh4btdgeos4c2n3e.apps.googleusercontent.com";


	public UserConnection(Socket s, LockManager mLocksManager) {
		this.mSocket = s;
		this.mLocksManager = mLocksManager;
	}

	@Override
	public void run() {
        try{
            Scanner scanner = new Scanner(mSocket.getInputStream());
            PrintWriter writer = new PrintWriter(mSocket.getOutputStream(), true);
            if(scanner.hasNextLine()){
                String data = scanner.nextLine();
                FileDao.writeOutput("Received from app: " + data);
                
                URL url = new URL("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + data);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                InputStreamReader input = new InputStreamReader(con.getInputStream());
                BufferedReader reader = new BufferedReader(input);
                
                String line;
                String result = "";
                while((line = reader.readLine()) != null){
                	result += line;
                }
                FileDao.writeOutput(result);
                writer.println("Received");
            }
            scanner.close();
        } catch (IOException ex) {
            FileDao.writeOutput("User Socket IO Error: " + ex.getMessage());
        }

		
	}

	public LockManager getmLocksManager() {
		return mLocksManager;
	}

}
