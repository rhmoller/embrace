package com.giddyplanet.embrace.examples.client.timer;

import com.giddyplanet.embrace.examples.client.Example;
import com.giddyplanet.embrace.examples.client.MyFunction;
import com.giddyplanet.embrace.webapis.Document;
import com.giddyplanet.embrace.webapis.HTMLDivElement;
import com.giddyplanet.embrace.webapis.HTMLElement;
import com.giddyplanet.embrace.webapis.Window;

import static com.giddyplanet.embrace.examples.client.Examples.getDocument;
import static com.giddyplanet.embrace.examples.client.Examples.getWindow;

public class TimerExample implements Example {

    @Override
    public String getId() {
        return "timer";
    }

    @Override
    public String getTitle() {
        return "Timer Example";
    }

    @Override
    public HTMLElement setup() {
        Document document = getDocument();
        HTMLDivElement div = (HTMLDivElement) document.createElement("div");
        div.innerHTML = "Soon, you will see a popup";

        Window window = getWindow();
        window.setTimeout((MyFunction) (args) -> window.alert("Time is up! " + args), 2000);

        return div;
    }
}
