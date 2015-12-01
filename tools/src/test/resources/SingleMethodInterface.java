package com.giddyplanet.webidl.dom;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public interface SingleMethodInterface {
    void hello();
}
