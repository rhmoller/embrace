package com.giddyplanet.embrace.tools.model.java;

import java.util.LinkedList;

public class JMethod {
    private String name;
    private LinkedList<JArgument> arguments = new LinkedList<>();
    private JTypeRef returnType;
    private boolean aStatic;
    private Visibility visibility = Visibility.PUBLIC;

    public JMethod(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void addArgument(JArgument argument) {
        arguments.add(argument);
    }

    public LinkedList<JArgument> getArguments() {
        return arguments;
    }

    public JTypeRef getReturnType() {
        return returnType;
    }

    public void setReturnType(JTypeRef returnType) {
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JMethod jMethod = (JMethod) o;

        if (name != null ? !name.equals(jMethod.name) : jMethod.name != null) return false;
        if (arguments != null ? !arguments.equals(jMethod.arguments) : jMethod.arguments != null) return false;
        return !(returnType != null ? !returnType.equals(jMethod.returnType) : jMethod.returnType != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (arguments != null ? arguments.hashCode() : 0);
        result = 31 * result + (returnType != null ? returnType.hashCode() : 0);
        return result;
    }

    public void setStatic(boolean aStatic) {
        this.aStatic = aStatic;
    }

    public boolean isaStatic() {
        return aStatic;
    }

    public void setaStatic(boolean aStatic) {
        this.aStatic = aStatic;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
