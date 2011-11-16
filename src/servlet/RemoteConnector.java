package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import core.*;

/**
 * Servlet implementation class RemoteDebugger
 */
@WebServlet("/RemoteConnector")
public class RemoteConnector extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static GDBDebug gdb = null;
	static ExecCmd exec;
	static String runpath;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoteConnector() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    private class ExecThread extends Thread {
    	public void run(){
    		exec.execShellScript(runpath);
			
    	}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter output = response.getWriter();
		
		String target = request.getParameter("target");
		int platform = Integer.parseInt(request.getParameter("platform"));
	
		//if(target.equals("127.0.0.1")){
			exec = new ExecCmd();
			runpath = null;
			if(platform == 0){
				runpath = "bash " + Path.scriptPath + "connect";
			}
			else{
				runpath = "bash " + Path.scriptPath + "connect_stm";
			}
			new ExecThread().start();
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		
		if(platform == 0){
			gdb = new GDBDebug("arm-bare");
		}
		else{
			gdb = new GDBDebug("arm-none");
		}
		gdb.start();
		gdb.initArmGDB(target);
		String firmpath = null;
		if(platform == 0){
			firmpath = "file " + Path.buildPath + "firmware";
		}
		else{
			firmpath = "file " + Path.buildPath_stm + "main.out";
		}
		gdb.command(firmpath, 1);
		
		gdb.command("load", 1);
		
		gdb.command("monitor reset run", 1);
		
		output.println("EOF_RUN");
		System.out.println("EOF_RUN");
		output.flush();
		
		ExecCmd exec = new ExecCmd();
		exec.execShellScript("bash " + Path.scriptPath + "haltgdb");
		gdb = null;
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
