package org.webrtc;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import org.webrtc.common.SignalingWebSocket;

public class WebRTCWebSocketHandler extends WebSocketHandler {
	
	public WebSocket doWebSocketConnect(HttpServletRequest request,	String protocol) {
		return new SignalingWebSocket();
	}

}