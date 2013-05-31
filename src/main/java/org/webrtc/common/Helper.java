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

    /**
     * Template default configuration.
     */
    private static final Configuration CFG = new Configuration();

    /**
     * Creates a random string with desired length.
     *
     * @param len the random string length.
     * @return a random string.
     */
    public static String randomize(int len) {
        String generated = "";
        for (int i = 0; i < len; i++) {
            int index = ((int) Math.round(Math.random() * 10)) % 10;
            generated += "0123456789".charAt(index);
        }
        return generated;
    }

    /**
     * Removes invalid characters from a specified string.
     *
     * @param str the string to be cleaned.
     * @return the new string with no invalid characters.
     */
    public static String sanitize(String str) {
        return str.replace("[^a-zA-Z0-9\\-]", "-");
    }

    /**
     *
     * @param stun_server
     * @return
     */
    public static String generatePCConfig(String stun_server) {
        StringBuffer config = new StringBuffer("STUN ");
        if (stun_server != null && !stun_server.equals("")) {
            config.append(stun_server);
        } else {
            config.append("stun.l.google.com:19302");
        }
        return config.toString();
    }

    public static String generatePCConstraints(boolean compat) {
        StringBuffer constraints = new StringBuffer("{optional:[");
        if (compat) {
            constraints.append("{'DtlsSrtpKeyAgreement':true}");
        }
        constraints.append("]}");
        return constraints.toString();
    }

    public static String generateOfferConstraints(boolean compat) {
        StringBuffer constraints = new StringBuffer("{optional:[],mandatory:{");
        if (compat) {
            constraints.append("'MozDontOfferDataChannel':true");
        }
        constraints.append("}}");
        return constraints.toString();
    }

    public static String generateMediaConstraints(String minResolution, String maxResolution) {
        StringBuffer constraints = new StringBuffer("{optional:[],mandatory:{");
        if (minResolution != null && !minResolution.isEmpty()) {
            String[] res = minResolution.split("x");
            constraints.append("minWidth:" + res[0] + ",minHeight:" + res[1]);
        }
        if (maxResolution != null && !maxResolution.isEmpty()) {
            String[] res = maxResolution.split("x");
            constraints.append("maxWidth:" + res[0] + ",maxHeight:" + res[1]);
        }
        constraints.append("}}");
        return constraints.toString();
    }

    /**
     * Create a {@link Map} from a {@link String} representing an URL query
     */
    public static Map<String, String> generateQueryMap(String query) {
        String[] params = query.split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String[] entry = param.split("=");
            map.put(entry[0], entry[1]);
        }
        return map;
    }

    /**
     * Create a {@link String} from an {@link InputStream}
     */
    public static String getStringFromStream(InputStream input) {
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
            CFG.setDirectoryForTemplateLoading(file.getParentFile());
            Template tpl = CFG.getTemplate(file.getName());
            StringWriter sw = new StringWriter();
            tpl.process(values, sw);
            output = sw.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
}
