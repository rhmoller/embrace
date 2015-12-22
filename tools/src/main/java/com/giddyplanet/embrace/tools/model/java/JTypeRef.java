package com.giddyplanet.embrace.tools.model.java;

public class JTypeRef {
    private String name;

    public JTypeRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JTypeRef jTypeRef = (JTypeRef) o;

        return !(name != null ? !name.equals(jTypeRef.name) : jTypeRef.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
