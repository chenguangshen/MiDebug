package core;

import java.net.*;
import java.io.*;

import com.sun.grizzly.websockets.WebSocket;

public class WebSocketProxy {

	private WebSocket webSocket;
	private Socket socket;
	private ServerSocket gdbSocket;
	private BufferedReader read;
	private PrintWriter output;
	
	public WebSocketProxy(){
		
	}
	
	public WebSocket getWebSocket() {
		return webSocket;
	}

	public void setWebSocket(WebSocket webSocket) {
		this.webSocket = webSocket;
	}

	public PrintWriter getOutput() {
		return output;
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
		while (true) {
			try {
				socket = gdbSocket.accept();
				System.out.println("Client connect to gdb daemon.");
				InetAddress inetAddress = socket.getInetAddress();
				String addr = inetAddress.getHostAddress();
				System.out.println("IP: " + addr);
				
				InputStreamReader input = new InputStreamReader(socket.getInputStream());
				read = new BufferedReader(input);
				output = new PrintWriter(socket.getOutputStream());
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	
	public void readCommand(){
		int a;
		int count = 0;
		//String str;
		try {
			while((a = read.read()) != -1){
				webSocket.send("" + (char)a);
				System.out.println((char)a);
			}
			if(count > 3){
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
