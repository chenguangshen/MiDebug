package core;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.grizzly.websockets.*;
import com.sun.grizzly.tcp.Request;

public class GDBDebugApplication extends WebSocketApplication {
//	private WebSocketProxy proxy;
//	private WebSocket socket;
//	private AsynGDBDebug agdb;

    @Override
    public WebSocket createSocket(NetworkHandler handler,
            WebSocketListener... listeners) throws IOException {
        return new GDBDebugWebSocket(handler, listeners);
    }
    
    @Override
    public void onConnect(WebSocket socket) {
    	super.onConnect(socket);
    	System.out.println("OnConnect");
    	WebSocketWrapper.webSocket = socket;
    	
    }
    
    @Override
    public void onMessage(WebSocket socket, DataFrame frame) throws IOException {
        final String data = frame.getTextPayload();
        System.out.println("Message received from OpenOCD: " + data);
        System.out.println("Message length: " + data.length());
        
        String cur = GDBProxyWrapper.currentInst;
        
        
        if(cur.equals("print") && data.length() == 132){
        	GDBProxyWrapper.proxy.getOutput().print(data);
			GDBProxyWrapper.proxy.getOutput().flush();
			GDBProxyWrapper.agdb.setResult("");
        	GDBProxyWrapper.agdb.waitForPrompt();
        	System.out.println(GDBProxyWrapper.agdb.getResult());
        	WebSocketWrapper.webSocket.send("PRINT_RES " + GDBProxyWrapper.agdb.getResult());
        }
        else if(cur.equals("frame") && data.length() == 12){
        	GDBProxyWrapper.proxy.getOutput().print(data);
			GDBProxyWrapper.proxy.getOutput().flush();
			GDBProxyWrapper.agdb.setResult("");
        	GDBProxyWrapper.agdb.waitForPrompt();
        	System.out.println(GDBProxyWrapper.agdb.getResult());
        	WebSocketWrapper.webSocket.send("FRAME: " + GDBProxyWrapper.agdb.getResult());
        }
        else if(cur.equals("cont") && data.length() == 83){
        	GDBProxyWrapper.proxy.getOutput().print(data);
			GDBProxyWrapper.proxy.getOutput().flush();
        	WebSocketWrapper.webSocket.send("EMPTY");
        }
        else if(cur.equals("cont") && data.length() == 12){
        	GDBProxyWrapper.proxy.getOutput().print(data);
			GDBProxyWrapper.proxy.getOutput().flush();
        	GDBProxyWrapper.agdb.setResult("");
        	GDBProxyWrapper.agdb.waitForPrompt();
        	GDBProxyWrapper.agdb.waitForPrompt();
        	System.out.println(GDBProxyWrapper.agdb.getResult());
        	WebSocketWrapper.webSocket.send("CONT_EOF " + GDBProxyWrapper.agdb.getResult());
        }
        else if(cur.equals("bk") && data.length() == 8){
        	GDBProxyWrapper.proxy.getOutput().print(data);
			GDBProxyWrapper.proxy.getOutput().flush();
			WebSocketWrapper.webSocket.send("BK_EOF");
			GDBProxyWrapper.agdb.waitForPrompt();
        }
        else if(cur.equals("file")){
        	if(!data.equals("$#00")){
        		GDBProxyWrapper.proxy.getOutput().print(data);
				GDBProxyWrapper.proxy.getOutput().flush();
				WebSocketWrapper.webSocket.send("FILE_EOF");
				GDBProxyWrapper.agdb.waitForPrompt();
        	}
        	else{
        		GDBProxyWrapper.proxy.writeToGDB(data);
        	}
        }
        else if(cur.equals("halt")){
        	String cmd = data;
        	ArrayList<String> coms = new ArrayList<String>();
     		int st = -1;
     		int en = 0;
     		while((st = cmd.indexOf('$')) >= 0){
     			en = cmd.indexOf('#');
     			coms.add(cmd.substring(st, en + 3));
     			if(en + 3 < cmd.length()){
     				cmd = cmd.substring(en + 3);
     			}
     			else{
     				break;
     			}
     		}
     		
     		if(coms.size() > 1){
     			for(String strr:coms){
     				if(strr != null){
     					System.out.println("Send To GDB:" + strr);
     					GDBProxyWrapper.proxy.getOutput().print(strr);
     					GDBProxyWrapper.proxy.getOutput().flush();
     				}
     			}
     			
				WebSocketWrapper.webSocket.send("HALT_EOF");
				GDBProxyWrapper.agdb.waitForPrompt();
				GDBProxyWrapper.currentInst = "file";
				GDBProxyWrapper.agdb.setResult("");
				GDBProxyWrapper.agdb.getOutputStream().println("file " + Path.buildPath + "firmware");
				GDBProxyWrapper.agdb.getOutputStream().flush();
		    	GDBProxyWrapper.proxy.readCommand();
     		}
     		else{
     			GDBProxyWrapper.proxy.writeToGDB(cmd);
     		}
        }
        else{      	
        	//if(data.equals("$80b586b0#ff")){
        	//	GDBProxyWrapper.proxy.writeToGDB("$80b5#ff");
            //}
        	//else{
        		GDBProxyWrapper.proxy.writeToGDB(data);
        	//}
        	
       	}
    }
    
    public boolean isApplicationRequest(Request request){
    	return true;
    }
}