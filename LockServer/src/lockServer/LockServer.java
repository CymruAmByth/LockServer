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
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// get required action
		String action = request.getParameter("action");

		// set up output
		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();

		if (action == null) {
			response.addHeader("Refresh", "5");
			this.showStatus(out);
		} else {
			// execute required action
			if (action.equals("showStatus")) {
				response.addHeader("Refresh", "5");
				this.showStatus(out);
			} else if (action.equals("start")) {
				this.start(out);
				response.sendRedirect("http://soerendonk.iwa.nu:7080/LockServer/LockServer?action=showStatus");
			} else if (action.equals("stop")) {
				this.stop(out);
			} else {
				response.sendRedirect("?action=showStatus");
			}
		}
	}

	private void showStatus(PrintWriter out) {
		// retrieve and print number of connected devices, if manager is not
		// null, else show warning
		if (lock != null) {
			int count = lock.noOfDevices();
			out.println("LockServer is running");
			out.println("Number of connected devices: " + count);
		} else {
			out.println("Lock server has not been started, add ?action=start to your url to start the server");
		}

		// print all output from error file
		out.println("Message file contents:");
		for (String line : FileDao.getOutput()) {
			out.println(line);
		}
	}

	private void start(PrintWriter out) {
		// stop execution if manager is already running
		if (lock != null){
			out.println("LockServer already running");
			this.showStatus(out);
			return;
		}

		// create new manager
		lock = new LockManager();
		Thread t = new Thread(lock);
		t.start();
	}

	private void stop(PrintWriter out) {
		// shut down running server and devices
		out.println("Devices stopped: " + lock.shutDownServer());
		lock = null;
		FileDao.emptyFile();
	}

}
