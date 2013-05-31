package org.webrtc.web;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webrtc.common.ChatService;
import org.webrtc.common.Helper;
import org.webrtc.common.SignalingWebSocket;
import org.webrtc.model.Room;

public class MessagePageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MessagePageServlet.class.getName());

    /**
     * @see {@link HttpServlet}.
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String message = Helper.getStringFromStream(req.getInputStream());
        logger.info("Delivering received message: " + message);
        Map<String, String> params = Helper.generateQueryMap(req.getQueryString());
        String room_key = params.get("r");
        Room room = ChatService.findRoom(room_key);
        if (room != null) {
            String user = params.get("u");
            String other_user = room.getParticipant(user);
            if (other_user != null && !other_user.equals("")) {
                // special case the loopback scenario
                if (other_user.equals(user)) {
                    message = message.replace("\"offer\"", "\"answer\"");
                    message = message.replace("a=crypto:0 AES_CM_128_HMAC_SHA1_32", "a=xrypto:0 AES_CM_128_HMAC_SHA1_32");
                }
                SignalingWebSocket.send(room.getUserToken(other_user), message);
                logger.info("Delivered message to user " + other_user);
            }
        } else {
            logger.log(Level.WARNING, "Unknown room " + room_key);
        }
    }


}
