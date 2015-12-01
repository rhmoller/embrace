package com.giddyplanet.embrace.tools.model.webidl;

import java.util.ArrayList;
import java.util.List;

public class Callback implements Definition, HasArguments {
    private String name;
    private String returnType;
    private List<Argument> arguments = new ArrayList<>();

    public Callback(String name) {
        this.name = name;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getReturnType() {
        return returnType;
    }

    public Argument addArgument(String type, String name) {
        Argument argument = new Argument(type, name);
        arguments.add(argument);
        return argument;
    }

    public List<Argument> getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }
}
