package locks;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;

import fileDao.FileDao;

public class LockDevice implements Runnable{

    private final SocketChannel socket;
    private final LockManager manager;
    private String deviceSerialNo;
    private String command;
    private ByteBuffer buf;
    private FileDao file;
	
    public LockDevice(SocketChannel socket, LockManager manager) {
        this.socket = socket;
        this.manager = manager;
        buf = ByteBuffer.allocate(80);
        buf.clear();
        file = new FileDao();
    }
    
	@Override
	public void run() {
        try {
            while (true) {
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
                    //System.out.println("Received: " + data + " @ " + new Date().toString());
                    file.save("Received: " + data + " @ " + new Date().toString());
                    if(data.equals("Hello"))
                    	command = "Hello there";
                    else if(data.equals("Ping"))
                    	command = "Pong";
                    else {
                    	String header = data.substring(0, 4);
                        String content = data.substring(4);
                        if(header.equals("DEV:")){
                        	deviceSerialNo = content;
                        	this.bindWithManager();
                        }
                        else {
                        	System.out.println(content);
                            System.out.println(header);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            //System.out.println(ex.getMessage());
        	try {
				file.save(ex.getMessage());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void bindWithManager() {
        manager.addDevice(deviceSerialNo, this);
    }
}
