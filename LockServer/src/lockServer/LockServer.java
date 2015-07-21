package lockServer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import fileDao.FileDao;
import locks.LockManager;

/**
 * Servlet implementation class LockServer
 */
@WebServlet("/LockServer")
public class LockServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	LockManager lock;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LockServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		lock = new LockManager();
		Thread t = new Thread(lock);
		t.start();
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//int count = lock.noOfDevices();
		response.setContentType("text/plain");
	    PrintWriter out = response.getWriter();
	    FileDao dao = new FileDao();
	    for(String line : dao.getCount()){
	    	out.println(line);
	    }
	    //out.println("Number of connected devices: " + count);
	}
	
	
	
	

}
