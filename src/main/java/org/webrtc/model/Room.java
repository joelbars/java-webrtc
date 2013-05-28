package org.webrtc.model;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import org.webrtc.common.Helper;
import org.webrtc.common.SignalingWebSocket;

public class Room {
	
	private static final Logger logger = Logger.getLogger(Room.class.getName()); 
	private static final ConcurrentMap<String, Room> DB = new ConcurrentHashMap<String, Room>();
	
	/** Retrieve a {@link Room} instance from database */
	public static Room get_by_key_name(String room_key) {
		return DB.get(room_key);
	}
	
	/** Called to disconnect a user (eventually remove the reserved {@link Room}) 
	 * when one of the conference legs are cut, i.e. a WebSocket connection is closed */
	public static void disconnect(String token) {
		if(token != null) {
			String[] values = token.split("/");
			String room_key = values[0];
			String user = values[1];
		    logger.info("Removing user " + user + " from room " + room_key);

		    Room room = Room.get_by_key_name(room_key);
		    if(room!=null && room.has_user(user)) {
		    	String other_user = room.get_other_user(user);
		    	room.remove_user(user);
		    	logger.info("Room " + room_key + " has state " + room.toString());
		    	if(other_user!=null) {		    		
		    		SignalingWebSocket.send(Helper.make_token(room, other_user), "{'type':'bye'}");
			        logger.info("Sent BYE to " + other_user);
			    }else {
			    	room.delete();
			    }
		    } else
		    	logger.warning("Unknown room " + room_key);
		}
	}
	
	//All the data we store for a room
	protected String key_name;
	protected String user1;
	protected String user2;
	
	public Room() {}
	
	public Room(String room_key) {
		key_name = room_key;		
		put();
	}
	
	/** @return a {@link String} representation of this room */
	public String toString() {
		String str = "[";
		if(user1!=null && !user1.equals(""))		      
			str += user1;
		if(user2!=null && !user2.equals(""))		      
			str += ", " + user2;		   
		str += "]";		   
		return str;
	}
	
	/** @return number of participant in this room */
	public int get_occupancy() {
		int occupancy = 0;
	    if(user1!=null && !user1.equals(""))
	    	occupancy += 1;
	    if(user2!=null && !user2.equals(""))	
	    	occupancy += 1;
	    return occupancy;
	}
	
	/** @return the name of the other participant, null if none */
	public String get_other_user(String user) {		
	    if(user.equals(user1))	      
	    	return user2;
	    else if(user.equals(user2))
	    	return user1;
	    else
	      return null;
	}
	
	/** @return true if one the participant is named as the input parameter, false otherwise */
	public boolean has_user(String user) {	    
		return (user!=null && (user.equals(user1) || user.equals(user2)));
	}
	
	/** Add a new participant to this room 
	 * @return if participant is found */
	public boolean add_user(String user) {
		boolean success = true; 
	    if(user1==null || user1.equals(""))	        
	    	user1 = user;
	    else if(user2==null || user2.equals(""))
	        user2 = user;
	    else {
	        success = false;
	    	System.out.print("room is full");
	    }
	    return success;
	}

	/** Removed a participant form current room */
	public void remove_user(String user) {
	    if(user!=null && user.equals(user2))
	    	user2 = null;
	    if(user!=null && user.equals(user1)) {
	    	if(user2!=null && !user2.equals("")) {
	    		user1 = user2;
	    		user2 = null;
	    	} else
	    		user1 = null;
	    }  
	    if(get_occupancy() > 0)
	      put();
	    else
	      delete();	      
	}
	
	/**@return the key of this room. */
	public String key() {
		return key_name;
	}
	
	/** Store current instance into database */
	public void put() {
		logger.info("Saving current room instance (key: "+key_name+") to database.");
		DB.put(key_name, this);
	}
	
	/** Delete/Remove current {@link Room} instance from database */
	public void delete() {
		logger.info("Deleting current room instance (key: "+key_name+") from database.");
		if(key_name!=null) {
			DB.remove(key_name);
			key_name = null;
		}
	}
}
