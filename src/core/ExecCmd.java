package core;

import java.io.*;
import java.util.*;

public class ExecCmd {
	private ArrayList<String> returnInfo;
	private PrintWriter output;
	
	public ExecCmd() {
		returnInfo = new ArrayList<String>();
	}
	
	public ExecCmd(PrintWriter pout) {
		output = pout;
		returnInfo = new ArrayList<String>();
	}
	
	public int execShellScript(String cmd){	
		int res = -1;
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", cmd);
		pb.redirectErrorStream(true);
		Map<String, String> env = pb.environment();
		env.put("PATH", Path.arm_bare_buildEnv);
		//env.put("TOOLPATH_STM32", "/usr/local/stm32");
		env.remove("OTHERVAR");
		try {
			Process p = pb.start();
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			String lineRead;
			while((lineRead = br.readLine()) != null){
				returnInfo.add(lineRead);
				System.out.println(lineRead);
				if(output != null){
					output.println(lineRead);
				}
				
				if(lineRead.equals("") || (lineRead.contains("lpc1768.cpu") && lineRead.contains("breakpoint"))){
					break;
				}
			}
			br.close();
			isr.close();
			//res = p.waitFor();
			//System.out.println(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public int execShellScriptWithoutHalt(String cmd){	
		int res = -1;
		ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c", cmd);
		pb.redirectErrorStream(true);
		Map<String, String> env = pb.environment();
		env.put("PATH", Path.arm_bare_buildEnv);
		//env.put("TOOLPATH_STM32", "/usr/local/stm32");
		env.remove("OTHERVAR");
		try {
			Process p = pb.start();
			InputStreamReader isr = new InputStreamReader(p.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			String lineRead;
			while((lineRead = br.readLine()) != null){
				returnInfo.add(lineRead);
				System.out.println(lineRead);
				if(output != null){
					output.println(lineRead);
				}
			}
			br.close();
			isr.close();
			//res = p.waitFor();
			//System.out.println(res);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public ArrayList<String> getReturnInfo() {
		return returnInfo;
	}

	public void setReturnInfo(ArrayList<String> returnInfo) {
		this.returnInfo = returnInfo;
	}

	public static void main(String[] args){
//		//execShellScript("/home/nesl/robovero/firmware/script/build");
//		//new ExecCmd().execShellScript("bash /home/nesl/robovero/firmware/script/connect | bash /home/nesl/robovero/firmware/script/flash_run");
//		System.out.println("Before");
//		ExecCmd exec = new ExecCmd();
//		exec.execShellScript("bash /home/nesl/robovero/firmware/script/connect");
//		System.out.println("After");
		ExecCmd exec = new ExecCmd();
		exec.execShellScript("bash " + Path.scriptPath + "probe_device");
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		exec.execShellScript(Path.scriptPath + "probe_device");
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
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
