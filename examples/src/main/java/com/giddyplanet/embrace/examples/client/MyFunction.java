package com.giddyplanet.embrace.examples.client;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface MyFunction<T> {
    void execute(T ... args);
}
