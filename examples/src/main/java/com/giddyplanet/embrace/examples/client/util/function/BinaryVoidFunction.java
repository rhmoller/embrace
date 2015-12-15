package com.giddyplanet.embrace.examples.client.util.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface BinaryVoidFunction<T, U> {
    void apply(T arg1, U arg2);
}
