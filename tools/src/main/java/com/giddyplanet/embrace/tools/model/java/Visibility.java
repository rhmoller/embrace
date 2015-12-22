package com.giddyplanet.embrace.tools.model.java;

public enum Visibility {
    PUBLIC("public"), PROTECTED("protected"), PACKAGE(""), PRIVATE("private");

    private String text;
    Visibility(String text) {
        this.text = text;
    }


    @Override
    public String toString() {
        return text;
    }

}
