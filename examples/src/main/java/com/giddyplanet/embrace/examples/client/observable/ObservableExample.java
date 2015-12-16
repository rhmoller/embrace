package com.giddyplanet.embrace.examples.client.observable;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.examples.client.util.function.UnaryVoidFunction;
import com.giddyplanet.embrace.webapis.*;

import static com.giddyplanet.embrace.examples.client.Examples.getDocument;

public class ObservableExample implements Example {

    @Override
    public String getId() {
        return "observable";
    }

    @Override
    public String getTitle() {
        return "Object.observe (only supported by chrome)";
    }

    @Override
    public HTMLElement setup() {
        Document document = getDocument();
        HTMLDivElement div = (HTMLDivElement) document.createElement("div");

        Model model = new Model();
        model.x = 1;

        HTMLButtonElement button = (HTMLButtonElement) document.createElement("button");
        button.innerHTML = "Bump";
        button.addEventListener("click", e -> model.x++);
        div.appendChild(button);

        HTMLDivElement status = (HTMLDivElement) document.createElement("div");
        status.innerHTML = "Initial value " + model.x;
        JsObject.observe(model, (UnaryVoidFunction<String[]>) changes -> status.innerHTML = "Changed to " + model.x, new String[] {"update"});
        div.appendChild(status);

        return div;
    }

    @Override
    public void start() {

    }
}
