package com.giddyplanet.embrace.examples.client;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface MyFunction1<T> {
    void execute(T arg);
}
