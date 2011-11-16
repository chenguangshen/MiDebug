package servlet;

import java.io.*;
import core.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GDBDaemon
 */
@WebServlet("/GDBDaemon")
public class GDBDaemon extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static GDBDebug gdb = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GDBDaemon() {
        super();
        // TODO Auto-generated constructor stub
        gdb = new GDBDebug("127.0.0.1", 3333);
        gdb.start();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		gdb.setClientOutput(new PrintWriter(response.getOutputStream()));
		
		String cmd = request.getParameter("type");
		if(cmd.equals("bk")){
			gdb.command("-break-insert main", 1);
		}
		if(cmd.equals("f")){
			gdb.command("file /home/nesl/test/test", 1);
		}
		if(cmd.equals("r")){
			gdb.command("-exec-run", 2);
		}
		if(cmd.equals("c")){
			gdb.command("-exec-continue", 2);
		}
		if(cmd.equals("ns")){
			gdb.command("-exec-next", 1);
		}
		if(cmd.equals("i")){
			gdb.command("-stack-info-frame", 1);
		}
		if(cmd.equals("kill")){
			gdb.stop();
		}
		
		//response.getOutputStream().flush();
		response.getOutputStream().close();
		
		//response.sendRedirect("/TestGDB.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
