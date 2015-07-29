package users;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

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
        	ServerSocketChannel server = ServerSocketChannel.open();
        	server.socket().bind(new InetSocketAddress(44445));
        	server.configureBlocking(false);
            while(run){
                SocketChannel s = server.accept();
                if(s!=null){
                	FileDao.writeOutput("Connecting user");
                	UserConnection uConnection = new UserConnection(s, mLocksManager);
                	Thread tUser = new Thread(uConnection);
                	tUser.start();
                }
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
