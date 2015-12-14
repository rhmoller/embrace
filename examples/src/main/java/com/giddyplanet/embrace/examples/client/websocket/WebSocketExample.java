package com.giddyplanet.embrace.examples.client.websocket;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.examples.client.MyFunction;
import com.giddyplanet.embrace.examples.client.MyFunction1;
import com.giddyplanet.embrace.webapis.*;

import static com.giddyplanet.embrace.examples.client.Examples.getDocument;

public class WebSocketExample implements Example {
    @Override
    public String getId() {
        return "echo";
    }

    @Override
    public String getTitle() {
        return "WebSocket echo";
    }

    @Override
    public HTMLElement setup() {
        Document doc = getDocument();
        HTMLElement panel = (HTMLElement) doc.createElement("div");

        HTMLTextAreaElement textbox = (HTMLTextAreaElement) doc.createElement("textarea");
        textbox.readOnly = true;
        panel.appendChild(textbox);

        HTMLInputElement input = (HTMLInputElement) doc.createElement("input");
        panel.appendChild(input);

        WebSocket webSocket = new WebSocket("ws://localhost:8080/examples/echo");

        webSocket.onopen = new MyFunction() {
            @Override
            public void execute(Object[] args) {
                textbox.textContent += "Opened connection\n";
            }
        };

        webSocket.onerror = new MyFunction<Object>() {
            @Override
            public void execute(Object... args) {
                textbox.textContent += "Error. Do you have a websocket server running?\n";
            }
        };

        webSocket.onmessage = new MyFunction1<MessageEvent>() {
            @Override
            public void execute(MessageEvent e) {
                textbox.textContent += e.data;
            }
        };

        input.onchange = new MyFunction() {
            @Override
            public void execute(Object[] args) {
                webSocket.send(input.value);
            }
        };

        return panel;
    }
}
