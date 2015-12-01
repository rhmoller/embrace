package com.giddyplanet.embrace.tools.model.webidl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Interface implements Definition {
    boolean resolved;
    String name;
    LinkedHashSet<HasArguments> constructors = new LinkedHashSet<>();
    LinkedHashSet<Operation> operations = new LinkedHashSet<>();
    LinkedHashSet<Attribute> attributes = new LinkedHashSet<>();
    Interface superType;
    LinkedHashSet<Interface> interfaces = new LinkedHashSet<>();

    public Interface(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addConstructor(HasArguments operation) {
        constructors.add(operation);
    }

    public List<HasArguments> getConstructors() {
        return new ArrayList<>(constructors);
    }

    public void addOperation(Operation operation) {
        operations.add(operation);
    }

    public List<Operation> getOperations() {
        return new ArrayList<>(operations);
    }

    public Interface getSuperType() {
        return superType;
    }

    public void setSuperType(Interface superType) {
        this.superType = superType;
    }

    public void addInterface(Interface i) {
        this.interfaces.add(i);
    }

    public LinkedHashSet<Interface> getInterfaces() {
        return interfaces;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public LinkedHashSet<Attribute> getAttributes() {
        return attributes;
    }

    public boolean hasConstructors() {
        return !constructors.isEmpty();
    }

    public boolean isResolved() {
        return resolved;
    }

    void markResolved() {
        this.resolved = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Interface that = (Interface) o;

        if (resolved != that.resolved) return false;
        if (!name.equals(that.name)) return false;
        if (!constructors.equals(that.constructors)) return false;
        if (!operations.equals(that.operations)) return false;
        if (!attributes.equals(that.attributes)) return false;
        if (superType != null ? !superType.equals(that.superType) : that.superType != null) return false;
        return interfaces.equals(that.interfaces);

    }

    @Override
    public int hashCode() {
        int result = (resolved ? 1 : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + constructors.hashCode();
        result = 31 * result + operations.hashCode();
        result = 31 * result + attributes.hashCode();
        result = 31 * result + (superType != null ? superType.hashCode() : 0);
        result = 31 * result + interfaces.hashCode();
        return result;
    }
}
