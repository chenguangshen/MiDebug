package core;

import java.io.*;
import java.util.*;

public class AsynGDBDebug {
	//private static final String buildEnv = "/home/nesl/x-tools/arm-bare_newlib_cortex_m3_nommu-eabi/bin";
	private String lineRead;
	private Scanner inputStream;
	private PrintWriter outputStream;
	private PrintWriter clientOutput;
	private String result;
	private int count;
	private int current;
	
	public void setCount(int c){
		this.count = c;
	}
	
	private String target;
	private int port;
	
	public PrintWriter getClientOutput() {
		return clientOutput;
	}

	public void setClientOutput(PrintWriter clientOutput) {
		this.clientOutput = clientOutput;
	}
	
	public PrintWriter getOutputStream () {
		return outputStream;
	}

	public AsynGDBDebug() {
		clientOutput = null;
		result = "";
		count = 1;
		target = null;
		port = 0;
	}
	
	public AsynGDBDebug(String _target, int _port) {
		target = _target;
		port = _port;
	}
	
	public AsynGDBDebug(PrintWriter output) {
		clientOutput = output;
	}
	
	public AsynGDBDebug(OutputStream stream) {
		clientOutput = new PrintWriter(stream);
	}
	
	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	private void writeToClient(String str, int flag){
		try {
			if (flag == 1) {
				System.out.println(str);
				result += str + "\n";
				// if(clientOutput != null){
				// clientOutput.println(str);
				// clientOutput.flush();
				// }
			} else {
				System.out.print(str);
				result += str;
				// if(clientOutput != null){
				// clientOutput.print(str);
				// clientOutput.flush();
				// }
			}
		}
		catch (Exception ex){
			System.out.println(ex);
		}
		
	}
	
	public void waitForPrompt(){
		current = 0;
		lineRead = inputStream.next();
		
		while(lineRead != null && !lineRead.equals("")){
		//	writeToClient(lineRead + " " + lineRead.indexOf("\\n"));
			if(lineRead.equals("(gdb)")){
				writeToClient("\n" + lineRead, 1);
			}
			else if(lineRead.indexOf("\\n") >= 0){
				writeToClient(lineRead, 1);
			}
			else{
				writeToClient(lineRead + " ", 0);
			}
			
			if(lineRead.equals("(gdb)")){
				//System.out.println(current);
				current++;
				
				if(current < count){
					//writeToClient("\n", 1);			
					//waitForPrompt();
				}
				else{
					break;
				}
				
			}
			lineRead = inputStream.next();
		}
	}
	
	public void command(String str, int gcount){
		//System.out.println();
		outputStream.println(str);
		outputStream.flush();
		count = gcount;
		waitForPrompt();
	}
	
	
	public void start() {
		ProcessBuilder pb = new ProcessBuilder("/home/nesl/x-tools/arm-bare_newlib_cortex_m3_nommu-eabi/bin/arm-bare_newlib_cortex_m3_nommu-eabi-gdb", "-interpreter=mi");
		//Map<String, String> env = pb.environment();
		//env.put("PATH", buildEnv);
		pb.redirectErrorStream(true);
		try {
			Process p = pb.start();
			
			Thread.sleep(1000);
			
			inputStream = new Scanner(p.getInputStream());
			outputStream = new PrintWriter(p.getOutputStream());
			
//			new Thread(){
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					
//				}
//			}.run();
			waitForPrompt();
//			command("-file-exec-and-symbols /home/nesl/test/test");
//			command("-exec-run");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop(){
		ExecCmd exec = new ExecCmd();
		exec.execShellScript("bash " + Path.scriptPath + "haltgdb");
		//waitForPrompt();
	}
	
	public void initArmGDB(){
		if(target == null){
			command("target remote localhost:3333", 1);
			command("mon mwb 0x400FC040 0x01", 1);
		}
		else{
			//command("set tcp auto-retry off", 1);
			//command("set debug remote 1", 1);
			//command("set remotetimeout 10", 1);
			//command("set connection-timeout 60", 1);
			command("target remote " + target + ":" + port, 1);
			//System.out.println("target remote " + target + ":" + port);
			clientOutput.print("EOF");
			//clientOutput.flush();
			System.out.println("init connection");
		}
		//command("monitor reset halt", 1);
	}
	
	public static void main(String[] args) {
		ExecCmd exec = new ExecCmd();
		exec.execShellScript("bash " + Path.scriptPath + "connect");
		AsynGDBDebug gdb = new AsynGDBDebug();
		gdb.start();
		gdb.initArmGDB();
		gdb.command("file " + Path.buildPath + "firmware", 1);
		gdb.command("-break-insert 78", 1);
		gdb.command("-exec-continue", 2);
		System.out.println("Before stop");
		gdb.stop();
		System.out.println("After stop");
	}
}
