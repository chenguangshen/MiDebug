package servlet;

import java.io.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.*;

/**
 * Servlet implementation class RoboVeroConnector
 */
@WebServlet("/Connector")
public class Connector extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Connector() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter output = response.getWriter();
		String type = request.getParameter("type");
		if(type != null && type.equals("run")){
			String pjname = request.getParameter("pjname");
			int platform = Integer.parseInt(request.getParameter("platform"));
			if(Path.compileFlag && Path.currentBinary.equals(pjname)){
				System.out.println("Before connecting");
				ExecCmd exec1 = new ExecCmd(output);
				String runpath = null;
				if(platform == 0){
					runpath = "bash " + Path.scriptPath + "connect | " + "bash " + Path.scriptPath + "flash_run";
				}
				else{
					runpath = "bash " + Path.scriptPath + "connect_stm | " + "bash " + Path.scriptPath + "flash_run_stm";
				}
				exec1.execShellScriptWithoutHalt(runpath);
				System.out.println("After running");
			}
			else{
				output.print(-2);
			}
		}		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}