package com.giddyplanet.embrace.examples.client.util.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface UnaryVoidFunction<T> {
    void apply(T arg);
}
