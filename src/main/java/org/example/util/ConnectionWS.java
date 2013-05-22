package org.example.util;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;
import org.example.chat.ChatSocketServlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public final class ConnectionWS extends MessageInbound {

    private final String username;

    public ConnectionWS(String username) {
        this.username = username;
    }

    @Override
    protected void onOpen(WsOutbound outbound) {
        ChatSocketServlet.getConnections().add(this);
        String message = String.format("\"%s\" se conectou.", username);
        ChatSocketServlet.broadcast(message);
    }

    @Override
    protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
        throw new RuntimeException("Metodo não aceito");
    }

    @Override
    protected void onTextMessage(CharBuffer msg) throws IOException {
        String message = String.format("\"%s\": %s", username, msg.toString());
        ChatSocketServlet.broadcast(message);
    }

}