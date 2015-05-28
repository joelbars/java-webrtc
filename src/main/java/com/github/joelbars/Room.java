package com.github.joelbars;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.websocket.Session;

/**
 * As described on effective java second edition.
 *  
 * @author joel
 *
 */
public enum Room {
	INSTANCE;
	
	private static final ConcurrentMap<String, Set<Session>> rooms = new ConcurrentHashMap<>();
	
	public ConcurrentMap<String, Set<Session>> map() {
		return rooms;
	}
}
