package com.giddyplanet.embrace.tools.model;

import com.giddyplanet.embrace.tools.model.webidl.Definition;

public interface TypeResolver {
    Definition resolve(String type);
}
