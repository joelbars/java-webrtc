package org.webrtc.common;

import org.webrtc.model.Room;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

/**
 * The room service that controls the creation and access to all chat rooms in the application.
 */
public final class ChatService implements Serializable {

    /**
     * {@link Logger}  instance.
     */
    private static final Logger logger = Logger.getLogger(Room.class.getName());
    /**
     * Simulated database to store all created rooms.
     */
    private static final ConcurrentMap<String, Room> DB = new ConcurrentHashMap<String, Room>();

    /**
     * Disconnects a user from a room.
     *
     * @param token the token in format "room/user" to be disconnected.
     */
    public static void endUserSession(String token) {
        if (token != null) {
            String[] values = token.split("/");
            String key = values[0];
            String user = values[1];
            logger.info("Removing user " + user + " from room " + key);

            Room room = findRoom(key);
            if (room != null && room.hasUser(user)) {
                String other = room.getParticipant(user);
                room.removeUser(user);
                logger.info("Room " + key + " has state " + room.toString());
                if (other != null) {
                    SignalingWebSocket.send(room.getUserToken(other), "{'type':'bye'}");
                    logger.info("Sent BYE to " + other);
                } else {
                    ChatService.deleteRoom(room);
                }
            } else
                logger.warning("Unknown room " + key);
        }
    }

    /**
     *
     * @param token
     * @return
     */
    public static boolean isValidToken(String token) {
        Room room = ChatService.findRoom(token);
        return room != null && room.hasUser(token);
    }

    /**
     * Gets a room by its key or token.
     *
     * @param key the room key.
     * @return the room or, <code>null</code> if it does not exists.
     */
    public static Room findRoom(String key) {
        if (key != null && key.contains("/")) {
            String[] values = key.split("/");
            if (values != null && values.length > 0)
                key = values[0];
        }

        return DB.get(key);
    }

    /**
     * Adds a new room on the service.
     *
     * @param room the room to be added.
     */
    public static void addRoom(Room room) {
        DB.put(room.getKey(), room);
    }

    /**
     * Creates a new room to be connected by others.
     *
     * @param key the id of the room to be created.
     * @return a newly created room controlled by this service.
     */
    public static Room createRoom(String key) {
        Room room = new Room(key);
        ChatService.addRoom(room);
        return room;
    }

    /**
     * Removes a room from the service.
     *
     * @param room the room to be removed.
     */
    public static void deleteRoom(Room room) {
        DB.remove(room.getKey());
    }
}
