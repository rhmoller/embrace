package com.giddyplanet.embrace.examples.client.event;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.webapis.*;

import static com.giddyplanet.embrace.examples.client.Examples.getDocument;
import static com.giddyplanet.embrace.examples.client.Examples.getWindow;

public class EventExample implements Example {

    @Override
    public String getId() {
        return "event";
    }

    @Override
    public String getTitle() {
        return "Event handler";
    }

    @Override
    public HTMLElement setup() {
        Document document = getDocument();
        HTMLDivElement div = (HTMLDivElement) document.createElement("div");

        HTMLButtonElement button = (HTMLButtonElement) document.createElement("button");
        button.addEventListener("click", (e) -> {
            getWindow().alert("Hello, happy clicker!");
        }, false);
        button.innerHTML = "Click Me!";

        div.appendChild(button);
        return div;
    }

    @Override
    public void start() {

    }
}
