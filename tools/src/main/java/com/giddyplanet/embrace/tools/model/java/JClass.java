package com.giddyplanet.embrace.tools.model.java;

import java.util.LinkedHashSet;

public class JClass {
    private String name;
    private AbstractionLevel abstraction = AbstractionLevel.CLASS;
    private String superType = null;
    private Visibility visibility = Visibility.PUBLIC;
    private LinkedHashSet<String> interfaces = new LinkedHashSet<>();
    private LinkedHashSet<JConstant> constants = new LinkedHashSet<>();
    private LinkedHashSet<JMethod> constructors = new LinkedHashSet<>();
    private LinkedHashSet<JMethod> methods = new LinkedHashSet<>();
    private LinkedHashSet<JField> fields = new LinkedHashSet<>();
    private boolean functional;
    private boolean noInterfaceObject;

    public JClass(String name) {
        this.name = name;
    }

    public AbstractionLevel getAbstraction() {
        return abstraction;
    }

    public void setAbstraction(AbstractionLevel abstraction) {
        this.abstraction = abstraction;
    }

    public String getName() {
        return name;
    }

    public String getSuperType() {
        return superType;
    }

    public void setSuperType(String superType) {
        this.superType = superType;
    }

    public void addInterface(JTypeRef ref) {
        interfaces.add(ref.getName());
    }

    public LinkedHashSet<String> getInterfaces() {
        return interfaces;
    }

    public void addMethod(JMethod method) {
        methods.add(method);
    }

    public LinkedHashSet<JMethod> getMethods() {
        return methods;
    }

    public void addConstructor(JMethod method) {
        constructors.add(method);
    }

    public LinkedHashSet<JMethod> getConstructors() {
        return constructors;
    }

    public void addConstant(JConstant constant) {
        constants.add(constant);
    }

    public LinkedHashSet<JConstant> getConstants() {
        return constants;
    }

    public void setFunctional(boolean functional) {
        this.functional = functional;
    }

    public boolean isFunctional() {
        return functional;
    }

    public void addField(JField field) {
        fields.add(field);
    }

    public LinkedHashSet<JField> getFields() {
        return fields;
    }

    public boolean isNoInterfaceObject() {
        return noInterfaceObject;
    }

    public void setNoInterfaceObject(boolean noInterfaceObject) {
        this.noInterfaceObject = noInterfaceObject;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
