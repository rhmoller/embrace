package com.giddyplanet.embrace.examples.server.websocket;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ClientEndpoint
@ServerEndpoint(value = "/echo")
public class EchoEndpoint {

    @OnMessage
    public String onMessage(String message, Session session) {
        return message;
    }

}
