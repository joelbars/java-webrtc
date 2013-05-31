package org.webrtc;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;

/**
 * Application Lifecycle Listener implementation for start/stop Embedding Jetty
 * Server configured to manage Signaling WebSocket with {@link WebRTCWebSocketHandler}.
 */
@WebListener
@SuppressWarnings("unused")
public class WebRTCServerServletContextListener implements ServletContextListener {

    private Server server = null;

    /**
     * Start Embedding Jetty server when WEB Application is started.
     */
    public void contextInitialized(ServletContextEvent event) {
        try {
            // 1) Create a Jetty server with the 8091 port.
            this.server = new Server(8091);
            // 2) Register SingalingWebSocketHandler in the Jetty server instance.
            WebRTCWebSocketHandler webRTCWebSocketHandler = new WebRTCWebSocketHandler();
            webRTCWebSocketHandler.setHandler(new DefaultHandler());
            server.setHandler(webRTCWebSocketHandler);
            // 2) Start the Jetty server.
            server.start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Stop Embedding Jetty server when WEB Application is stopped.
     */
    public void contextDestroyed(ServletContextEvent event) {
        if (server != null) {
            try {// stop the Jetty server.
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
