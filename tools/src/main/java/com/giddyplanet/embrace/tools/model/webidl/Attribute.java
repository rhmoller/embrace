package com.giddyplanet.embrace.tools.model.webidl;

public class Attribute {
    boolean readOnly;
    String type;
    String name;

    public Attribute(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Attribute attribute = (Attribute) o;

        if (readOnly != attribute.readOnly) return false;
        if (!type.equals(attribute.type)) return false;
        return name.equals(attribute.name);

    }

    @Override
    public int hashCode() {
        int result = (readOnly ? 1 : 0);
        result = 31 * result + type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
