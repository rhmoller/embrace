package com.giddyplanet.embrace.examples.client.util.function;

import jsinterop.annotations.JsFunction;

@JsFunction
public interface BinaryFunction<T, U, R> {
    R apply(T arg1, U arg2);
}
