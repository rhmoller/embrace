package com.giddyplanet.embrace.examples.client.websocket;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.examples.client.util.function.VariadicVoidFunction;
import com.giddyplanet.embrace.examples.client.util.function.UnaryVoidFunction;
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

        webSocket.onopen = new VariadicVoidFunction() {
            @Override
            public void apply(Object[] args) {
                textbox.textContent += "Opened connection\n";
            }
        };

        webSocket.onerror = new VariadicVoidFunction<Object>() {
            @Override
            public void apply(Object... args) {
                textbox.textContent += "Error. Do you have a websocket server running?\n";
            }
        };

        webSocket.onmessage = new UnaryVoidFunction<MessageEvent>() {
            @Override
            public void apply(MessageEvent e) {
                textbox.textContent += e.data;
            }
        };

        input.onchange = new VariadicVoidFunction() {
            @Override
            public void apply(Object[] args) {
                webSocket.send(input.value);
            }
        };

        return panel;
    }
}
