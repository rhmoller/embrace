package com.giddyplanet.embrace.tools.model.webidl;

public class TypeRef<D extends Definition> {
    private String name;
    private D resolved;

    public TypeRef(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public D getResolved() {
        return resolved;
    }

    public void setResolved(D resolved) {
        this.resolved = resolved;
    }

}
