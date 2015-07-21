package locks;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.TreeMap;

import fileDao.FileDao;

public class LockManager implements Runnable{
	
	private final TreeMap<String, LockDevice> devices = new TreeMap<String, LockDevice>();
	private FileDao file;

	@Override
	public void run() {
		file = new FileDao();
		ServerSocketChannel serverSocket;
        try {
        	serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(44444));
            file.save("Port used is:44444");
            serverSocket.configureBlocking(false);
            while (true) {
                SocketChannel s = serverSocket.accept();
                if (s != null) {
                    //System.out.println("Connecting device");
                	file.save("Connecting device");
                    LockDevice device = new LockDevice(s, this);
                    Thread t = new Thread(device);
                    t.start();
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
	
    public void addDevice(String deviceSerialNo, LockDevice device) {
        devices.put(deviceSerialNo, device);
    }
    
    public int noOfDevices(){
    	return devices.size();
    }
}
