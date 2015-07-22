package locks;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import fileDao.FileDao;

public class LockDevice implements Runnable{

    private final SocketChannel socket;
    private final LockManager manager;
    private String deviceSerialNo;
    private String command;
    private ByteBuffer buf;
    private boolean run;
    private ScheduledThreadPoolExecutor checker;
    private final PeriodicPingCheck ppc;
	
    public LockDevice(SocketChannel socket, LockManager manager) {
        this.socket = socket;
        this.manager = manager;
        buf = ByteBuffer.allocate(80);
        buf.clear();
        run = true;
        checker = new ScheduledThreadPoolExecutor(1);
        ppc = new PeriodicPingCheck();
        checker.schedule(ppc, 2, TimeUnit.MINUTES);
    }
    
	@Override
	public void run() {
        try {
            while (run) {
                //sending data is a command is there
                if (command != null) {
                    buf.clear();
                    buf.put(command.getBytes());
                    buf.flip();
                    while (buf.hasRemaining()) {
                        socket.write(buf);
                    }
                    command = null;
                }
                //attempting to receive data
                buf.clear();
                if (socket.read(buf) > 0){
                    buf.flip();
                    String data = new String(buf.array(), buf.position(),buf.limit());
                    data = data.trim();
                    FileDao.writeOutput("Received: " + data );
                    if(data.equals("Hello")){
                    	command = "Hello there";
                    } else if(data.equals("Ping")){
                    	command = "Pong";
                    	checker.shutdownNow();
                    	checker = new ScheduledThreadPoolExecutor(1);
                    	//checker.remove(ppc);
                    	checker.schedule(ppc, 2, TimeUnit.MINUTES);
                    } else {
                    	String header = data.substring(0, 4);
                        String content = data.substring(4);
                        if(header.equals("DEV:")){
                        	deviceSerialNo = content;
                        	this.bindWithManager();
                        }
                        else {
                        	FileDao.writeOutput(header);
                        	FileDao.writeOutput(content);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            FileDao.writeOutput(ex.getMessage());
		}
        unbindWithManager();
	}
	
	private void bindWithManager() {
        manager.addDevice(deviceSerialNo, this);
    }
	
	private void unbindWithManager() {
		manager.removeDevice(deviceSerialNo);
	}
	
	public void stopDevice() {
		run = false;
	}
	
	private class PeriodicPingCheck implements Runnable {
		@Override
		public void run() {
			stopDevice();			
		}		
	}
}
