package com.giddyplanet.embrace.examples.client.hello;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.webapis.Document;
import com.giddyplanet.embrace.webapis.Element;
import com.giddyplanet.embrace.webapis.HTMLDivElement;
import com.giddyplanet.embrace.webapis.HTMLElement;

import static com.giddyplanet.embrace.examples.client.Examples.getDocument;

public class HelloExample implements Example {

    @Override
    public String getId() {
        return "hello";
    }

    @Override
    public String getTitle() {
        return "Hello, World";
    }

    @Override
    public HTMLElement setup() {
        Document document = getDocument();
        HTMLDivElement div = (HTMLDivElement) document.createElement("div");
        div.innerHTML = "Hello, World!";
        return div;
    }

    @Override
    public void start() {

    }
}
