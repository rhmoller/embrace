package com.giddyplanet.embrace.examples.client.util.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface UnaryFunction<T, R> {
    R apply(T arg);
}
