package com.giddyplanet.embrace.tools.model.webidl;

import java.util.ArrayList;
import java.util.List;

public class Enumeration implements Definition {
    private List<String> values = new ArrayList<>();
    private String name;

    public Enumeration(String name) {
        this.name = name;
    }

    public void addValue(String text) {
        values.add(text);
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }
}
