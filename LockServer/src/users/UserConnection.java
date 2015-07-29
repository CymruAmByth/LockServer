package users;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


import fileDao.FileDao;
import locks.LockManager;
import users.Message.Type;

public class UserConnection implements Runnable {
	
    private final SocketChannel mSocket;
    private final LockManager mLocksManager;
    private ByteBuffer buf;
    

	public UserConnection(SocketChannel s, LockManager mLocksManager) {
		this.mSocket = s;
		this.mLocksManager = mLocksManager;
		buf = ByteBuffer.allocate(1500);
		buf.clear();
	}

	@Override
	public void run() {
		try{
			boolean run = true;
			Message m = new Message();
			Message reply;
			//receiving input
			while(run){				
				if(mSocket.read(buf)>0){
					buf.flip();
					String data = new String(buf.array(), buf.position(), buf.limit());
					data = data.trim();
					FileDao.writeOutput("Received: " + data);
					m = new Message(data);
					
					TokenVerifier token = new TokenVerifier(m.getToken());
					FileDao.writeOutput(token.getClaim("email"));
					run = false;
				}				
			}
			//sending reply
			switch(m.getType()){
				case CONN:
					//send back message with device info
					reply = new Message(Type.INFO, "nA", String.valueOf(mLocksManager.noOfDevices()));
					break;
				case OPEN:
					//send open command to device, send back confirmation
					//TODO: send open command to device
					reply = new Message(Type.CONFIRM, "nA", "Door opening");
					break;
				default:
					//print received message, send back error message					
					FileDao.writeOutput(m.toString());
					reply = new Message(Type.ERROR, "nA", "unexpected message: " + m.toString());
					break;				
			}
			buf.clear();
			buf.put(reply.toString().getBytes());
			buf.flip();
			while(buf.hasRemaining()){
				mSocket.write(buf);
			}
        } catch (IOException ex) {
            FileDao.writeOutput("User Socket IO Error: " + ex.getMessage());
        }

		
	}

	public LockManager getmLocksManager() {
		return mLocksManager;
	}

}
