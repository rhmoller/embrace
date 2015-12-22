package com.giddyplanet.embrace.tools.model.java;

import java.util.HashMap;
import java.util.Map;

public class JavaModel {
    private Map<String, JClass> types = new HashMap<>();

    public Map<String, JClass> getTypes() {
        return types;
    }

    public void put(String name, JClass javaClass) {
        types.put(name, javaClass);
    }

}
