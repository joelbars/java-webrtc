package org.webrtc.web;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.webrtc.common.ChatService;

public class DisconnectPageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(DisconnectPageServlet.class.getName());

    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String[] key = req.getParameterValues("from");
        if (key != null && key.length > 0) {
            ChatService.endUserSession(key[0]);
        }

    }
}
