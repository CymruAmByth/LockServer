package locks;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.TreeMap;

import fileDao.FileDao;

public class LockManager implements Runnable{
	
	private final TreeMap<String, LockDevice> devices = new TreeMap<String, LockDevice>();

	@Override
	public void run() {
		ServerSocketChannel serverSocket;
        try {
        	serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(44444));
            serverSocket.configureBlocking(false);
            while (true) {
                SocketChannel s = serverSocket.accept();
                if (s != null) {
                	FileDao.writeOutput("Connecting device");
                    LockDevice device = new LockDevice(s, this);
                    Thread t = new Thread(device);
                    t.start();
                }
            }
        } catch (IOException ex) {
        	FileDao.writeOutput(ex.getMessage());
		}
	}
	
    public void addDevice(String deviceSerialNo, LockDevice device) {
        devices.put(deviceSerialNo, device);
    }
    
    public int noOfDevices(){
    	return devices.size();
    }
}
