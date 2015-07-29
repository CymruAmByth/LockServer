package users;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import fileDao.FileDao;
import locks.LockManager;

public class UserManager implements Runnable{

    private final LockManager mLocksManager;
    private boolean run = true;

	public UserManager(LockManager mLocksManager) {
		this.mLocksManager = mLocksManager;
	}

	@Override
	public void run() {
        try{
        	ServerSocket server = new ServerSocket(44445);
            while(run){
                Socket s = server.accept();
                UserConnection uConnection = new UserConnection(s, mLocksManager);
                Thread tUser = new Thread(uConnection);
                tUser.start();
            }
            server.close();
        } catch (IOException ex) {
        	FileDao.writeOutput("Server error: " + ex.getMessage());
        }
	}
	
	public void shutDownServer(){
		run = false;
	}

    
    
}
