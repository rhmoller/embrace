package com.giddyplanet.embrace.tools;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VarArgLambdaTest {

    interface MyFunction<R, T> {
        R exe(T... args);
    }

    Object doIt(Object mf, Object... args) {
        return ((MyFunction)mf).exe(args);
    }

    @Test
    public void invoke() {
        Object x = doIt((MyFunction)args -> args[1], "foo", "bar");
        assertEquals("bar", x);
    }
}
