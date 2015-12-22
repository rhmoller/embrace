package com.giddyplanet.embrace.tools.model.java;

public class JArgument {
    private String name;
    private JTypeRef type;
    private boolean varArgs;

    public JArgument(String name, JTypeRef type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public JTypeRef getType() {
        return type;
    }

    public boolean isVarArgs() {
        return varArgs;
    }

    public void setVarArgs(boolean varArgs) {
        this.varArgs = varArgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JArgument jArgument = (JArgument) o;

        if (varArgs != jArgument.varArgs) return false;
        if (name != null ? !name.equals(jArgument.name) : jArgument.name != null) return false;
        return !(type != null ? !type.equals(jArgument.type) : jArgument.type != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (varArgs ? 1 : 0);
        return result;
    }
}
