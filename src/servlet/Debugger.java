package servlet;

import java.io.*;
import java.util.Scanner;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.*;

/**
 * Servlet implementation class RoboVeroDebugger
 */
@WebServlet("/Debugger")
public class Debugger extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static GDBDebug gdb = null;
	private static AsynGDBDebug agdb = null;
	private static GDBServerProxy gdbproxy;
	static String res;
	static PrintWriter output;
	static ExecCmd exec;
	static String runpath;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Debugger() {
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
		
		String type = request.getParameter("type");
		
		if(type.equals("start")){
			int platform = Integer.parseInt(request.getParameter("platform"));
			String target = request.getParameter("target");
			
			if(target.equals("127.0.0.1")){
				exec = new ExecCmd(output);
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
			}
			
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
			output.println(gdb.getResult());
			output.flush();
		}
		
		if(type.equals("quit")){
			gdb.setResult("");
			ExecCmd exec = new ExecCmd();
			exec.execShellScript("bash " + Path.scriptPath + "haltgdb");
			output.println(gdb.getResult());
			output.flush();
			gdb = null;
			//output.println(0);
		}
		
		if(type.equals("cont")){
			gdb.setResult("");
			gdb.command("-exec-continue", 2);
			output.println(gdb.getResult());
			output.flush();
			//output.println(0);
		}
		
		if(type.equals("info")){
			gdb.setResult("");
			System.out.println("info requested");
			gdb.command("-stack-info-frame", 1);
			output.println(gdb.getResult());
			output.flush();
		}
		
		if(type.equals("next")){
			gdb.setResult("");
			System.out.println("next requested");
			gdb.command("-exec-next", 2);
			output.println(gdb.getResult());
			output.flush();
		}
		
		if(type.equals("bk")){
			gdb.setResult("");
			int lineNum = Integer.parseInt(request.getParameter("line"));
			gdb.command("-break-insert " + lineNum, 1);
			output.println(gdb.getResult());
			output.flush();
		}
		
		if(type.equals("print")){
			gdb.setResult("");
			String vname = (request.getParameter("vname"));
			gdb.command("print " + vname, 1);
			output.println(gdb.getResult());
			output.flush();
		}
		
		if(type.equals("lframe")){
			gdb.setResult("");
			gdb.command("-stack-list-frames", 1);
			output.println(gdb.getResult());
			output.flush();
		}
		
		if(type.equals("probe_device")){
			
			String result = gen_result();
			//result += "192.168.1.106 ";
			output.print(result);
			output.flush();
		}
	}
	
	private String gen_result(){
		String result = "127.0.0.1 ";
		ExecCmd exec = new ExecCmd();
		exec.execShellScript("bash " + Path.scriptPath + "probe_device");
		System.out.println("after probe");
		File file = new File(Path.scriptPath + "result");
		try {
			Scanner input = new Scanner(file);
			while(input.hasNext()){
				String cur = input.nextLine();
				String ip = "";
				String temp = "";
				if(cur.indexOf("Nmap scan report") == 0){
					ip = cur.substring(21);
					input.nextLine();
					input.nextLine();
					temp = input.nextLine();
					System.out.println(ip + " " + temp.substring(9, 9 + 5));
					if(temp.charAt(9) == 'o' || temp.charAt(9) == 'f'){
						result += ip + " ";
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		output = response.getWriter();
		
		String type = request.getParameter("type");
		
		if(type.equals("startlocal")){
			GDBProxyWrapper.currentInst = "init";
			new ProxyThread().start();
			new GDBThread().start();
		
			System.out.println("Both Works!");
			
			
		}
		
		
		if(type.equals("haltlocal")){
			GDBProxyWrapper.currentInst = "halt";
			GDBProxyWrapper.agdb.setResult("");
			GDBProxyWrapper.agdb.getOutputStream().println("monitor reset halt");
			GDBProxyWrapper.agdb.getOutputStream().flush();
		}
		
		if(type.equals("filelocal")){
			System.out.println("File load requested.");
			GDBProxyWrapper.currentInst = "file";
			GDBProxyWrapper.agdb.setResult("");
			GDBProxyWrapper.agdb.getOutputStream().println("file " + Path.buildPath + "firmware");
			GDBProxyWrapper.agdb.getOutputStream().flush();
	    	GDBProxyWrapper.proxy.readCommand();
		}
		
		if(type.equals("bklocal")){
			int lineNum = Integer.parseInt(request.getParameter("line"));
			GDBProxyWrapper.currentInst = "bk";
			GDBProxyWrapper.agdb.getOutputStream().println("-break-insert " + lineNum);
			GDBProxyWrapper.agdb.getOutputStream().flush();
	    	GDBProxyWrapper.proxy.readCommand();
		}
		
		
		if(type.equals("contlocal")){
			GDBProxyWrapper.sychronizedf = false;
			GDBProxyWrapper.sychronizedp = false;
			System.out.println("Continue local requested");
			GDBProxyWrapper.currentInst = "cont";
			GDBProxyWrapper.agdb.setResult("");
			GDBProxyWrapper.agdb.getOutputStream().println("-exec-continue");
			GDBProxyWrapper.agdb.getOutputStream().flush();
			GDBProxyWrapper.proxy.readCommand();
		}
		
		if(type.equals("nextlocal")){
			GDBProxyWrapper.sychronizedf = false;
			GDBProxyWrapper.sychronizedp = false;
			System.out.println("Next local requested");
			GDBProxyWrapper.currentInst = "cont";
			GDBProxyWrapper.agdb.setResult("");
			GDBProxyWrapper.agdb.getOutputStream().println("-exec-next");
			GDBProxyWrapper.agdb.getOutputStream().flush();
			GDBProxyWrapper.proxy.readCommand();
		}
		
		if(type.equals("infolocal")){
			System.out.println("Info local requested");
			GDBProxyWrapper.currentInst = "info";
			GDBProxyWrapper.agdb.setResult("");
			GDBProxyWrapper.agdb.command("-stack-info-frame", 1);
			WebSocketWrapper.webSocket.send("INFO: " + GDBProxyWrapper.agdb.getResult());
		}
		
		if(type.equals("framelocal")){
			System.out.println("frame local requested");
			GDBProxyWrapper.currentInst = "frame";
			if(GDBProxyWrapper.sychronizedf == false){
				GDBProxyWrapper.sychronizedf = true;
				GDBProxyWrapper.agdb.setResult("");
				GDBProxyWrapper.agdb.getOutputStream().println("-stack-list-frames");
				GDBProxyWrapper.agdb.getOutputStream().flush();
				GDBProxyWrapper.proxy.readCommand();
			}
			else{
				GDBProxyWrapper.agdb.setResult("");
				GDBProxyWrapper.agdb.command("-stack-list-frames", 1);
				WebSocketWrapper.webSocket.send("FRAME: " + GDBProxyWrapper.agdb.getResult());
			}
			
		}
		
		if(type.equals("printlocal")){
			System.out.println("Print local requested");
			String vname = request.getParameter("vname");
			System.out.println(vname);
			GDBProxyWrapper.currentInst = "print";
			if(GDBProxyWrapper.sychronizedf == false){
				GDBProxyWrapper.sychronizedf = true;
				GDBProxyWrapper.agdb.setResult("");
				GDBProxyWrapper.agdb.getOutputStream().println("print " + vname);
				GDBProxyWrapper.agdb.getOutputStream().flush();
				GDBProxyWrapper.proxy.readCommand();
			}
			else{
				GDBProxyWrapper.agdb.setResult("");
				GDBProxyWrapper.agdb.command("print " + vname, 1);
				WebSocketWrapper.webSocket.send("PRINT_RES: " + GDBProxyWrapper.agdb.getResult());
			}
		}
		
		if(type.equals("exitlocal")){
			agdb.stop();
			agdb = null;
			
			gdbproxy.stopCurret();
			gdbproxy = null;
			new ProxyThread().start();
		}
	}
	
    private class ProxyThread extends Thread {
    	public void run(){
    		gdbproxy = new GDBServerProxy();
    		GDBProxyWrapper.proxy = gdbproxy;
    		gdbproxy.initSocket();
    		gdbproxy.startServerLoop();
    	}
    }
    
    private class GDBThread extends Thread {
    	public void run(){
    		agdb = new AsynGDBDebug("127.0.0.1", 3359);
    		GDBProxyWrapper.agdb = agdb;
			agdb.setClientOutput(output);
    		agdb.start();
    		try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			agdb.initArmGDB();
			System.out.println("After init");
    	}
    }
}