package com.giddyplanet.embrace.examples.client;

import com.giddyplanet.embrace.webapis.HTMLElement;

public interface Example {

    String getId();

    String getTitle();

    HTMLElement setup();

    void start();
}
