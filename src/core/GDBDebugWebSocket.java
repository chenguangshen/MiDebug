package core;

import com.sun.grizzly.websockets.*;

public class GDBDebugWebSocket extends BaseServerWebSocket {
    public GDBDebugWebSocket(NetworkHandler handler, WebSocketListener... listeners) {
        super(handler, listeners);
    }
}
