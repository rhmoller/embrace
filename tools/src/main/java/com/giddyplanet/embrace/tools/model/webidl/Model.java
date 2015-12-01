package com.giddyplanet.embrace.tools.model.webidl;

import java.util.HashMap;
import java.util.Map;

public class Model {
    private Map<String, Definition> types = new HashMap<>();

    public Map<String, Definition> getTypes() {
        return types;
    }

    public void addType(Interface type) {
        types.put(type.getName(), type);
    }

    public Definition getType(String name) {
        return types.get(name);
    }

    public Interface getOrCreateInterface(String name) {
        Definition type = types.get(name);
        if (type == null) {
            type = new Interface(name);
            types.put(name, type);
        } else {
            if (!(type instanceof Interface)) throw new IllegalArgumentException("Definition with id " + name + " is not an interface");
        }
        return (Interface) type;
    }

    public void addType(Enumeration currentEnum) {
        types.put(currentEnum.getName(), currentEnum);
    }

    public void addType(Callback callback) {
        types.put(callback.getName(), callback);
    }
}
