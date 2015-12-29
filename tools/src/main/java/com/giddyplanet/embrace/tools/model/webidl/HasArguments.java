package com.giddyplanet.embrace.tools.model.webidl;

import java.util.List;

public interface HasArguments {
    void setReturnType(String returnType);

    String getReturnType();

    Argument addArgument(String type, String name);

    List<Argument> getArguments();

    String getName();
}
