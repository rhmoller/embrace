package com.giddyplanet.embrace.tools.model.java;

public class JField {
    String name;
    JTypeRef type;

    public JField(String name, JTypeRef type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public JTypeRef getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JField jField = (JField) o;

        if (name != null ? !name.equals(jField.name) : jField.name != null) return false;
        return !(type != null ? !type.equals(jField.type) : jField.type != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
