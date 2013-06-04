package org.webrtc.model;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * The room definition with its participants and all necessary control methods.
 */
public class Room implements Serializable {

    /**
     * Max number of users allowed in rooms.
     */
    public static final int MAX_USERS = 2;
    /**
     * The room key.
     */
    protected String key;

    /**
     * The room participants.
     */
    protected List<String> users = new LinkedList<String>();

    /**
     * The class constructor to createRoom a new room passing the desired key.
     *
     * @param key the room key
     */
    public Room(String key) {
        this.key = key;
    }

    /**
     * Shows the actual number of users in this room.
     *
     * @return the number of users in room.
     */
    public int countUsers() {
        return users.size();
    }

    /**
     * Return the other participant on this room.
     *
     * @param user the username.
     * @return the other username.
     */
    public String getParticipant(String user) {
        if (user != null && user.contains("/")) {
            user = filterToken(user)[1];
        }
        int i = users.indexOf(user) ^ 1;
        String participant = null;
        if (i <= users.size()) {
            participant = users.get(i);
        }
        return participant;
    }

    /**
     * Verifies is a specified user is in this room
     *
     * @param user the user to be verified
     * @return <code>true</code> if user is in this room, or else <code>false</code>
     */
    public boolean hasUser(String user) {
        if (user != null && user.contains("/")) {
            user = filterToken(user)[1];
        }

        return user != null && users.contains(user);
    }

    /**
     * Filters a specified token and returns the room and user.
     *
     * @param token the token to be filtered
     * @return a string array containing the room and user.
     */
    private String[] filterToken(String token) {
        String[] res = null;
        if (token != null && token.contains("/")) {
            res = token.split("/");
        }
        return res;
    }

    /**
     * Adds an user in to this room.
     *
     * @param user the user to be added
     * @return <code>true</code> if user was successfully added, or else <code>false</code>
     */
    public boolean addUser(String user) {
        boolean added = false;
        if (!users.contains(user) && users.size() <= MAX_USERS) {
            added = users.add(user);
        }
        return added;
    }

    /**
     * Removes an user from this room.
     *
     * @param user the user to be removed.
     */
    public void removeUser(String user) {
        users.remove(user);
    }

    /**
     * Returns user token if it is in this room.
     *
     * @param user the user to create the token
     * @return the token if the user exists in this room, or else <code>null</code>
     */
    public String getUserToken(String user) {
        String token = null;
        if (users.contains(user)) {
            token = this.key + "/" + user;
        }
        return token;
    }

    /**
     * Gets the room key.
     *
     * @return the room key.
     */
    public String getKey() {
        return this.key;
    }

    /**
     * @return a {@link String} representation of this room
     */
    public String toString() {
        StringBuffer str = new StringBuffer("[");
        for (String user : users) {
            if (user != null && !user.isEmpty()) {
                str.append(user);
            }
        }
        str.append("]");
        return str.toString();
    }
}
