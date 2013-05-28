package org.webrtc.web;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webrtc.common.Helper;
import org.webrtc.common.SignalingWebSocket;
import org.webrtc.model.Room;

public class MessagePageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(MessagePageServlet.class.getName()); 
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {		
		String message = Helper.get_string_from_stream(req.getInputStream());
		logger.info("Delivering received message: " + message);
		Map<String, String> params = Helper.get_query_map(req.getQueryString());
		String room_key = params.get("r");
		Room room = Room.get_by_key_name(room_key);
		if(room!=null) {
			String user = params.get("u");
		    String other_user = room.get_other_user(user);
		    if(other_user!=null && !other_user.equals("")) {
		    	// special case the loopback scenario
		    	if(other_user.equals(user)) {
		            message = message.replace("\"offer\"", "\"answer\"");
		            message = message.replace("a=crypto:0 AES_CM_128_HMAC_SHA1_32", "a=xrypto:0 AES_CM_128_HMAC_SHA1_32");
		    	}
		    	SignalingWebSocket.send(Helper.make_token(room, other_user), message);
		        logger.info("Delivered message to user " + other_user);
		    }
		}else {
			logger.log(Level.WARNING, "Unknown room " + room_key);
		}
	}
	
	 
}
