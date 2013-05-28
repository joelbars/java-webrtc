package org.webrtc.common;

import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.webrtc.model.Room;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    public static final String SERVER = "http://localhost:8080";
    private static final Configuration cfg = new Configuration();

    /**
     * Used to generate a random room number
     */
    public static String generate_random(int len) {
        String generated = "";
        for (int i = 0; i < len; i++) {
            int index = ((int) Math.round(Math.random() * 10)) % 10;
            generated += "0123456789".charAt(index);
        }
        return generated;
    }

    public static String sanitize(String key) {
        return key.replace("[^a-zA-Z0-9\\-]", "-");
    }

    /**
     * @return a token for a given room instance and a participant
     */
    public static String make_token(Room room, String user) {
        return room.key() + "/" + user;
    }

    /**
     * @return a token for a given room key and a participant
     */
    public static String make_token(String room_key, String user) {
        return room_key + "/" + user;
    }

    /**
     * Check if the token in parameter corresponds to an existent room that has a participant identified in the token
     *
     * @return true if token is valid, false otherwise.
     */
    public static boolean is_valid_token(String token) {
        boolean valid = false;
        Room room = Room.get_by_key_name(get_room_key(token));
        String user = get_user(token);
        if (room != null && room.has_user(user))
            valid = true;
        return valid;
    }

    /**
     * @return room key from the token parameter
     */
    public static String get_room_key(String token) {
        String room_key = null;
        if (token != null) {
            String[] values = token.split("/");
            if (values != null && values.length > 0)
                room_key = values[0];
        }
        return room_key;
    }

    /**
     * @return user from the token parameter
     */
    public static String get_user(String token) {
        String user = null;
        if (token != null) {
            String[] values = token.split("/");
            if (values != null && values.length > 1)
                user = values[1];
        }
        return user;
    }

    public static String make_pc_config(String stun_server) {
        if (stun_server != null && !stun_server.equals(""))
            return "STUN " + stun_server;
        else
            return "STUN stun.l.google.com:19302";
    }


    /**
     * Create a {@link Map} from a {@link String} representing an URL query
     */
    public static Map<String, String> get_query_map(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
        return map;
    }

    /**
     * Create a {@link String} from an {@link InputStream}
     */
    public static String get_string_from_stream(InputStream input) {
        String output = null;
        try {
            StringWriter writer = new StringWriter();
            IOUtils.copy(input, writer);
            output = writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * Generate an HTML file by using JTPL template engine to replace variables with their values provided in the map.
     */
    public static String generatePage(File file, Map<String, String> values) {
        //String block = "main";
        String output = null;
        try {
            cfg.setDirectoryForTemplateLoading(file.getParentFile());
            Template tpl = cfg.getTemplate(file.getName());
            StringWriter sw = new StringWriter();
            tpl.process(values, sw);
            output = sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
