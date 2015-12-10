package com.giddyplanet.embrace.tools.model.webidl;

import com.giddyplanet.embrace.tools.model.TypeResolver;

public class SimpleTypeResolver implements TypeResolver {
    Model model;

    public SimpleTypeResolver(Model model) {
        this.model = model;
    }

    @Override
    public Definition resolve(String type) {
        return model.getType(type);
    }
}
