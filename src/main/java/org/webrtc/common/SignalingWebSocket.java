package org.webrtc.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.eclipse.jetty.websocket.WebSocket;
import org.webrtc.model.Room;

import static org.webrtc.common.ChatService.findRoom;
import static org.webrtc.common.ChatService.isValidToken;

/**
 * Class that does all the control signaling messages.
 */
public class SignalingWebSocket implements WebSocket.OnTextMessage {
    /**
     * Logger instance.
     */
    private static final Logger logger = Logger.getLogger(SignalingWebSocket.class.getName());
    /**
     * List of current oppened channels.
     */
    private static final ConcurrentMap<String, SignalingWebSocket> channels = new ConcurrentHashMap<String, SignalingWebSocket>();
    /**
     * Web Socket connection.
     */
    private Connection connection;
    /**
     * Token that identifies this connection.
     */
    private String token;

    /**
     * Sends a message to a channel based on a specified token.
     *
     * @param token   the channel token
     * @param message the message to be sent
     * @return <code>true</code> if the message was successfully sent, or else <code>false</code>
     */
    public static boolean send(String token, String message) {
        logger.info("Signalisation: sending out message (" + message + ") for " + token);
        boolean success = false;
        SignalingWebSocket ws = channels.get(token);
        if (ws != null)
            success = ws.send(message);
        return success;
    }

    /**
     * Stores the opened connection.
     *
     * @param connection the opened connection.
     */
    public void onOpen(Connection connection) {
        logger.info("A new connection opened.");
        this.connection = connection;
    }

    /**
     * Check if message is a token declaration and then store mapping between the token and this ws.
     *
     * @param data the message data
     */
    public void onMessage(String data) {
        try {
            if (data.startsWith("token")) { // peer declaration
                int index = data.indexOf(":");
                token = data.substring(index + 1);
                channels.put(token, this);
                logger.info(String.format("Adding token (valid = %s): %s", isValidToken(token), token));
            } else { // signaling messages exchange --> route it to the other peer
                Room room = findRoom(token);
                String participant = room.getUserToken(room.getParticipant(token));
                send(participant, data);
            }
        } catch (Exception x) {
            // Error was detected, close the WebSocket client side
            this.connection.disconnect();
        }
    }

    /**
     * Removes ChatWebSocket of the global list of SignalingWebSocket instance.
     *
     * @param closeCode the connection close status code
     * @param message   the close message
     */
    public void onClose(int closeCode, String message) {
        logger.info("Connection (token:" + token + ") closed with code " + closeCode + ": " + message);
        if (token != null) {
            ChatService.endUserSession(token);
            channels.remove(token);
        }
    }

    /**
     * Sends a single message.
     *
     * @param message the message to be sent.
     * @return <code>true</code> if the message was successfully sent, or else <code>false</code>
     */
    public boolean send(String message) {
        logger.info("Sending message ... " + message);
        boolean success = false;
        if (connection != null) {
            try {
                connection.sendMessage(message);
                success = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return success;
    }
}
