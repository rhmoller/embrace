package com.giddyplanet.embrace.tools.model.webidl;

import java.util.ArrayList;
import java.util.List;

public class Operation implements HasArguments {
    private String name;
    private String returnType;
    private List<Argument> arguments = new ArrayList<>();
    private boolean isStatic;

    public Operation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

    @Override
    public Argument addArgument(String type, String name) {
        Argument argument = new Argument(type, name);
        arguments.add(argument);
        return argument;
    }

    @Override
    public List<Argument> getArguments() {
        return arguments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation operation = (Operation) o;

        if (!name.equals(operation.name)) return false;
        return arguments.equals(operation.arguments);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + arguments.hashCode();
        return result;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

}
