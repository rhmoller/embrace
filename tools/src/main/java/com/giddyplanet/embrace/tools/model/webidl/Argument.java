package com.giddyplanet.embrace.tools.model.webidl;

public class Argument {
    private String type;
    private String name;
    private boolean varArgs = false;

    public Argument(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Argument argument = (Argument) o;

        if (!type.equals(argument.type)) return false;
        return name.equals(argument.name);

    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public void setVarArgs(boolean varArgs) {
        this.varArgs = varArgs;
    }

    public boolean isVarArgs() {
        return varArgs;
    }
}
