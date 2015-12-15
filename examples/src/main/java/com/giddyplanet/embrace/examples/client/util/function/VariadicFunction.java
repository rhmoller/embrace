package com.giddyplanet.embrace.examples.client.util.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface VariadicFunction<T, R> {
    R apply(T... args);
}
