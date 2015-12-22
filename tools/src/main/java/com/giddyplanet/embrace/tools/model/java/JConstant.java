package com.giddyplanet.embrace.tools.model.java;

public class JConstant {
    private JTypeRef type;
    private String name;
    private String value;

    public JConstant(JTypeRef type, String name, String value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    public JTypeRef getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
