package com.giddyplanet.embrace.examples.client;

import com.giddyplanet.embrace.examples.client.canvas.CanvasExample;
import com.giddyplanet.embrace.examples.client.event.EventExample;
import com.giddyplanet.embrace.examples.client.hello.HelloExample;
import com.giddyplanet.embrace.examples.client.timer.TimerExample;
import com.giddyplanet.embrace.examples.client.websocket.WebSocketExample;
import com.giddyplanet.embrace.webapis.*;
import com.google.gwt.core.client.EntryPoint;

import java.util.HashMap;
import java.util.Map;

public class Examples implements EntryPoint {
    private Map<String, Example> examples;
    private Document doc;


    @Override
    public void onModuleLoad() {
        examples = new HashMap<>();
        addExample(new HelloExample());
        addExample(new EventExample());
        addExample(new CanvasExample());
        addExample(new TimerExample());
        addExample(new WebSocketExample());

        buildMenu();
    }

    private void buildMenu() {
        doc = getDocument();

        HTMLElement ul = (HTMLElement) doc.createElement("ul");
        for (Map.Entry<String, Example> entry : examples.entrySet()) {
            Example example1 = entry.getValue();

            Element li = doc.createElement("li");
            HTMLAnchorElement a = (HTMLAnchorElement) doc.createElement("a");
            a.href = "#";
            a.innerHTML = example1.getTitle();
            a.setAttribute("data-example", example1.getId());

            li.appendChild(a);
            ul.appendChild(li);
        }
        ul.style.setProperty("float", "left");
        ul.style.setProperty("margin-right", "2em");
        doc.body.appendChild(ul);

        Element container = doc.createElement("div");
        doc.body.appendChild(container);

        ul.addEventListener("click", (e) -> {
            HTMLElement target = (HTMLElement) e.target;
            String id = target.getAttribute("data-example");
            Example example = examples.get(id);

            Element old = container.firstElementChild;
            if (old != null) {
                old.remove();
            }

            HTMLElement root = example.setup();
            container.appendChild(root);
        }, false);
    }

    private void addExample(Example example) {
        examples.put(example.getId(), example);
    }

    public static native Document getDocument() /*-{
        return $doc;
    }-*/;

    public static native Window getWindow() /*-{
        return $wnd;
    }-*/;

}
