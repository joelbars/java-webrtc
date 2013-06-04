package org.webrtc.web;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webrtc.common.ChatService;
import org.webrtc.common.Helper;
import org.webrtc.model.Room;


/**
 * The main UI page, renders the 'index.html' template.
 */
public class MainPageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MainPageServlet.class.getName());

    /**
     * Renders the main page. When this page is shown, we createRoom a new channel to push asynchronous updates to the client.
     */
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String PATH = req.getContextPath().replace("/", "");
        String query = req.getQueryString();
        if (query == null) {
            String redirect = String.format("/%s/main?r=%s", PATH, Helper.randomize(8));
            logger.info("Null Query -> Redirecting visitor to base URL to " + redirect);
            resp.sendRedirect(redirect);
            return;
        }
        Map<String, String> params = Helper.generateQueryMap(query);
        String room_key = Helper.sanitize(params.get("r"));
        String debug = params.get("debug");
        String stun_server = params.get("ss");
        String turn_server = params.get("ts");
        String min_resolution = params.get("minre");
        String max_resolution = params.get("maxre");
        String hd_video = params.get("hd");
        Boolean compat = params.containsKey("compat") && Boolean.valueOf(params.get("compat"));
        if (room_key == null || room_key.isEmpty()) {
            room_key = Helper.randomize(8);
            String redirect = "/" + PATH + "/main?r=" + room_key;
            if (debug != null)
                redirect += ("&debug=" + debug);
            if (stun_server != null || !stun_server.isEmpty())
                redirect += ("&ss=" + stun_server);
            if (turn_server != null || !turn_server.isEmpty())
                redirect += ("&ts=" + turn_server);
            if (min_resolution != null || !min_resolution.isEmpty())
                redirect += ("&minre=" + turn_server);
            if (max_resolution != null || !max_resolution.isEmpty())
                redirect += ("&maxre=" + turn_server);
            if (hd_video != null || !hd_video.isEmpty())
                redirect += ("&hd=" + hd_video);
            logger.info("Absent room key -> Redirecting visitor to base URL to " + redirect);
            resp.sendRedirect(redirect);
        } else {
            String user;
            int initiator;
            Room room = ChatService.findRoom(room_key);
            if (room == null && (debug == null || !"full".equals(debug))) {
                logger.info("New room " + room_key);
                user = Helper.randomize(8);
                room = ChatService.createRoom(room_key);
                room.addUser(user);
                if (!"loopback".equals(debug))
                    initiator = 0;
                else {
                    room.addUser(user);
                    initiator = 1;
                }
            } else if (room != null && room.countUsers() == 1 && !"full".equals(debug)) {
                logger.info("Room " + room_key + " with 1 occupant.");
                user = Helper.randomize(8);
                room.addUser(user);
                initiator = 1;
            } else {
                logger.info("Room " + room_key + " with 2 occupants (full).");
                Map<String, String> template_values = new HashMap<String, String>();
                template_values.put("room_key", room_key);
                String filepath = getServletContext().getRealPath("/full.html");
                File file = new File(filepath);
                resp.getWriter().print(Helper.generatePage(file, template_values));
                return;
            }

            String server_name = req.getServerName();
            int server_port = req.getServerPort();
            String room_link = String.format("http://%s:%d/%s/main?r=%s", server_name, server_port, PATH, room_key);
            if (debug != null)
                room_link += ("&debug=" + debug);
            if (stun_server != null && !stun_server.isEmpty())
                room_link += ("&ss=" + stun_server);
            if (turn_server != null && !turn_server.isEmpty())
                room_link += ("&ts=" + turn_server);
            if (min_resolution != null && !min_resolution.isEmpty())
                room_link += ("&minre=" + turn_server);
            if (max_resolution != null && !max_resolution.isEmpty())
                room_link += ("&maxre=" + turn_server);
            if (hd_video != null && !hd_video.isEmpty())
                room_link += ("&hd=" + hd_video);

            String token = room.getUserToken(user);
            String pc_config = Helper.generatePCConfig(stun_server);
            String pc_constraints = Helper.generatePCConstraints(compat);
            String offer_constraints = Helper.generateOfferConstraints(compat);
            String media_constraints = Helper.generateMediaConstraints(min_resolution, max_resolution);
            Map<String, String> template_values = new HashMap<String, String>();
            template_values.put("server_name", server_name);
            template_values.put("server_port", server_port + "");
            template_values.put("PATH", PATH);
            template_values.put("token", token);
            template_values.put("me", user);
            template_values.put("room_key", room_key);
            template_values.put("room_link", room_link);
            template_values.put("initiator", "" + initiator);
            template_values.put("pc_config", pc_config);
            template_values.put("pc_constraints", pc_constraints);
            template_values.put("media_constraints", media_constraints);
            template_values.put("offer_constraints", offer_constraints);
            File file = new File(getServletContext().getRealPath("/main.html"));
            resp.getWriter().print(Helper.generatePage(file, template_values));
            logger.info("User " + user + " added to room " + room_key);
            logger.info("Room " + room_key + " has state " + room);
        }
    }

}
