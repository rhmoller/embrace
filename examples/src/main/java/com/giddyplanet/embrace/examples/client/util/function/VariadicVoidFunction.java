package com.giddyplanet.embrace.examples.client.util.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface VariadicVoidFunction<T> {
    void apply(T ... args);
}
