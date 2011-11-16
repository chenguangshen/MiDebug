package core;

import java.net.*;
import java.util.regex.*;
import java.io.*;

public class GDBServerProxy {
    static Pattern pattern = Pattern.compile("\\$[\\w | ?]+");
	private ServerSocket gdbSocket;
	private String result;
	private PrintWriter output;
	private BufferedReader read;
	int count;
	String last;
	String type;
	int total;
	int all_length;
	Socket socket;


	public void setType(String type) {
		this.type = type;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = new String(result);
	}

	public PrintWriter getOutput() {
		return output;
	}

	public void setOutput( PrintWriter output) {
		this.output = output;
	}

	public GDBServerProxy() {
		type = "";
		count = 0;
		result = "";
	}

	public int initSocket() {
		try {
			gdbSocket = new ServerSocket(3359);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			return -1;
		}
		return 0;
	}

	public void startServerLoop() {
		System.out.println("GDB daemon starts");
		result = "";
		while (true) {
			try {
				socket = gdbSocket.accept();
				System.out.println("Client connect to gdb daemon.");
				InetAddress inetAddress = socket.getInetAddress();
				String addr = inetAddress.getHostAddress();
				System.out.println("IP: " + addr);
				
				count = 0;
				
				InputStreamReader input = new InputStreamReader(socket.getInputStream());
				read = new BufferedReader(input);
				output = new PrintWriter(socket.getOutputStream());
				
				readCommand();
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void stopCurret(){
		try {
			read.close();
			output.close();
			socket.close();
			gdbSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void haltConn(){
		output.print("$#00");
		output.flush();
	}
	
	public String writeToGDB(String content){
		result = "";
		last = "";
		try {
			System.out.println("Content to write:" + content);
			output.print(content);
			output.flush();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		readCommand();
		return result;
	}
	
	public void readCommand(){
		int a;
		result = "";
		total = 0;
		all_length = 0;
		try {
			while ((a = read.read()) != -1) {
				char cur = (char) a;
				//System.out.print(cur);
				result += cur;
				if(cur == '#'){
					result += (char)read.read();
					result += (char)read.read();
					
					System.out.println("Result to send:" + result);
					WebSocketWrapper.webSocket.send(result);
					
					if(GDBProxyWrapper.currentInst.equals("init") && result.indexOf("qTStatus") >= 0){
						WebSocketWrapper.webSocket.send("EOF");
						GDBProxyWrapper.currentInst = "halt";
						GDBProxyWrapper.agdb.setResult("");
						GDBProxyWrapper.agdb.getOutputStream().println("monitor reset halt");
						GDBProxyWrapper.agdb.getOutputStream().flush();
					}

					break;
				}
				if(GDBProxyWrapper.currentInst.equals("halt") && cur == '-'){
					WebSocketWrapper.webSocket.send("EMPTY");
					break;
				}
				/*if(cur == '-'){
					System.out.println();
					if(type.equals("monitor")){
						if(result.equals("")){
							result = "CUT";
						}
						break;
					}
					else{
						int st = -1;
						Matcher matcher = pattern.matcher(result);
						if(matcher.find()){
							st = matcher.start();
							System.out.println(matcher.start());
						}
						//int st = reg.matcher(result).start();
						//int st = result.indexOf("$");
						int end = result.indexOf('#');
						if(st >= 0 && end > 0){
							result = result.substring(st, end + 3);
//							result = "+" + result;
							System.out.println(result);
							
							System.out.println("Result to send:" + result);
							WebSocketWrapper.webSocket.send(result);
							
							break;
						}
					}
				}
				result += cur;
				all_length++;
				if(cur == '$'){
					total = 1;
				}
				else{
					total++;
				}
				
				if(type.equals("file") && total == 13){
					result = result.substring(result.indexOf('$'));
					System.out.println(result);
					output.print("$#00");
					output.flush();
					break;
				}
				if(type.equals("clear_file") && all_length == 18){
					System.out.println(result);
					break;
				}
				if(type.equals("cont") && result.equals("$c#63")){
					System.out.println(result);
					break;
				}*/
//				if(cur == '$'){
//					if(start == 0 && prev == 1){
//						start = 1;
//					}
//					count ++;
//					if(count == 2){
//						System.out.println();
//						start = 0;
//						prev = 0;
//						if(result.length() >= 1){
////							if(result.charAt(0) != '+'){
////								result = "+" + result;
////							}
//							if(result.charAt(result.length() - 1) == '+'){
//								result = result.substring(0, result.length() - 1);
//							}
//							int ppos = result.indexOf('#');
//							if(ppos >= 0){
//								result = result.substring(0, ppos + 3);
//							}
//						}
//						System.out.println("Last:" + last);
//						System.out.println("Result:" + result);
//						if(!result.equals(last)){
//							last = new String(result);
//							System.out.println("Last:" + last);
//							break;
//						}
//						else{
//							readCommand();
//						}
//					}
//				}
//				if(start == 1){
//					//System.out.print(cur);
//					result += cur;
//				}
//				if(cur == '+'){
//					prev = 1;
//				}
			}
			//System.out.println();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
//
//	private class HandleAClient extends Thread {
//		private Socket socket;
//
//		public HandleAClient(Socket socket) {
//			this.socket = socket;
//		}
//
//		public void run() {
//			try {
//				count = 0;
//				InputStreamReader input = new InputStreamReader(socket.getInputStream());
//				BufferedReader read = new BufferedReader(input);
//				DataOutputStream output = new DataOutputStream(socket.getOutputStream());
//				String str;
//				int a;
//				while ((a = read.read()) != -1) {
//					if(Character.toChars(a)[0] == '$'){
//						count ++;
//						if(count == 2){
//							break;
//						}
//					}
//					System.out.print(Character.toChars(a));					
//				}
//			} catch (Exception e) {
//				System.err.println(e.toString());
//			}
//		}
//	}
	
	public static void main(String[] args){
//		Matcher matcher = pattern.matcher("aasdf$$$O4a54414720");
//		if(matcher.find()){
//			System.out.println(matcher.start());
//		}
//		System.exit(0);
		GDBServerProxy gdbproxy = new GDBServerProxy();	
		gdbproxy.initSocket();
		gdbproxy.startServerLoop();
	}
}