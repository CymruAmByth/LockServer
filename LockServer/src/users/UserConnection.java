package users;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;


import fileDao.FileDao;
import locks.LockManager;

public class UserConnection implements Runnable {
	
    private final Socket mSocket;
    private final LockManager mLocksManager;

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
                
                TokenVerifier token = new TokenVerifier(data);
                FileDao.writeOutput("User connected: " + token.getClaim("email"));
                
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
