package com.giddyplanet.embrace.tools.webidl2java;

import com.giddyplanet.embrace.tools.model.java.*;
import com.giddyplanet.embrace.tools.model.webidl.*;
import com.giddyplanet.embrace.tools.model.webidl.Enumeration;

import java.util.*;

public class ModelConverter {
    private Model idlModel;
    private SimpleTypeResolver resolver;
    private JavaModel javaModel;

    public ModelConverter(Model model, SimpleTypeResolver resolver) {
        this.idlModel = model;
        this.resolver = resolver;
        this.javaModel = new JavaModel();
    }

    public JavaModel bind() {
        convert();
        fixTransferable();
        fixHtmlCollection();
        addMethodImplementations();
        addConstructors();
        addPackagDefaultConstructors();
        pushInterfaceFields();
        return javaModel;
    }

    private void pushInterfaceFields() {
        for (JClass jClass : javaModel.getTypes().values()) {
            if (jClass.getAbstraction() != AbstractionLevel.INTERFACE) {
                String superType = jClass.getSuperType();
                LinkedHashSet<JField> fields = new LinkedHashSet<>();
                if (superType != null) {
                    if (javaModel.getTypes().get(superType).getAbstraction() == AbstractionLevel.INTERFACE) {
                        collectInterfaceFields(javaModel.getTypes(), fields, superType);
                    }
                }
                for (String s : jClass.getInterfaces()) {
                    collectInterfaceFields(javaModel.getTypes(), fields, s);
                }

                for (JField field : fields) {
                    jClass.addField(field);
                }
            }
        }
    }

    private void fixHtmlCollection() {
        JClass jclass = javaModel.getTypes().get("HTMLCollection");
        if (jclass != null) {
            for (JMethod jMethod : jclass.getMethods()) {
                if ("namedItem".equals(jMethod.getName())) {
                    jMethod.setReturnType(new JTypeRef("Object"));
                }
            }
        }
    }

    // todo: generalize to fix any attempt to implement non-interface type
    private void fixTransferable() {
        for (JClass jClass : javaModel.getTypes().values()) {
            if ("Transferable".equals(jClass.getSuperType())) {
                jClass.setSuperType(null);
            }

            for (Iterator<String> iterator = jClass.getInterfaces().iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                if (s.equals("Transferable")) {
                    iterator.remove();
                }
            }
        }
    }

    private void convert() {
        for (Definition definition : idlModel.getTypes().values()) {
            if (definition instanceof Interface) {
                Interface idef = (Interface) definition;
                String name = idef.getJavaName();
                JClass java = new JClass(name);

                if (idef.isCallback() || idef.getExtendedAttributes().contains("NoInterfaceObject")) {
                    java.setAbstraction(AbstractionLevel.INTERFACE);
                } else if (idef.getConstructors().isEmpty()) {
                    java.setAbstraction(AbstractionLevel.ABSTRACT_CLASS);
                }

                if (idef.isCallback()) {
                    java.setFunctional(true);
                }

                if (idef.getSuperType() != null) {
                    java.setSuperType(idef.getSuperType().getName());
                }

                LinkedHashSet<Interface> interfaces = idef.getInterfaces();
                for (Interface anInterface : interfaces) {
                    java.addInterface(new JTypeRef(anInterface.getName()));
                }

                for (Constant constant : idef.getConstants()) {
                    JConstant jc = new JConstant(new JTypeRef(fixType(constant.getType(), true)), constant.getName(), constant.getValue());
                    java.addConstant(jc);
                }

                for (Attribute attribute : idef.getAttributes()) {
                    JField field = new JField(fixName(attribute.getName()), new JTypeRef(fixType(attribute.getType(), false)));
                    java.addField(field);
                }

                convertConstructors(java, idef.getConstructors());
                convertOperations(java, idef.getOperations());

                javaModel.put(name, java);
            } else if (definition instanceof Callback) {
                // todo: test for callback
                String name = ((Callback) definition).getName();
                JClass java = new JClass(name);
                java.setAbstraction(AbstractionLevel.INTERFACE);
                java.setFunctional(true);

                JMethod method = new JMethod("execute");
                method.setReturnType(new JTypeRef(fixType(((Callback) definition).getReturnType(), true)));
                List<Argument> arguments = ((Callback) definition).getArguments();
                convertArguments(method, arguments);
                java.addMethod(method);

                javaModel.put(name, java);
            } else if (definition instanceof Enumeration) {
                Enumeration enumeration = (Enumeration) definition;
                String name = enumeration.getName();
                JClass java = new JClass(name);
                java.setAbstraction(AbstractionLevel.INTERFACE);
                java.setNoInterfaceObject(true);
                for (String value : enumeration.getValues()) {
                    String id = makeIdentifier(value).toUpperCase();
                    java.addConstant(new JConstant(new JTypeRef("DOMString"), id, "\"" + value + "\""));
                }
                javaModel.put(name, java);
            }
        }
    }

    private void convertConstructors(JClass java, List<Operation> constructors) {
        for (Operation operation : constructors) {
            JMethod method = new JMethod(java.getName());

            List<Argument> arguments = new ArrayList<>(operation.getArguments());
            convertArguments(method, arguments);
            java.addConstructor(method);

            while (arguments.size() > 0 && arguments.get(arguments.size() - 1).isOptional()) {
                arguments.remove(arguments.size() - 1);
                System.out.println("Cut one off " + method.getName());

                method = new JMethod(java.getName());

                convertArguments(method, arguments);
                java.addConstructor(method);
            }
        }
    }

    private void convertOperations(JClass java, List<Operation> operations) {
        for (Operation operation : operations) {
            JMethod method = new JMethod(operation.getName());
            method.setReturnType(new JTypeRef(fixType(operation.getReturnType(), true))); // todo: test for optional return
            method.setStatic(operation.isStatic());

            List<Argument> arguments = new ArrayList<>(operation.getArguments());
            convertArguments(method, arguments);
            java.addMethod(method);

            while (arguments.size() > 0 && arguments.get(arguments.size() - 1).isOptional()) {
                arguments.remove(arguments.size() - 1);
                System.out.println("Cut one off " + method.getName());

                method = new JMethod(operation.getName());
                method.setReturnType(new JTypeRef(fixType(operation.getReturnType(), true))); // todo: test for optional return

                convertArguments(method, arguments);
                java.addMethod(method);
            }
        }
    }

    private void convertArguments(JMethod method, List<Argument> arguments) {
        for (Argument argument : arguments) {
            JArgument jarg = new JArgument(argument.getName(), new JTypeRef(fixType(argument.getType(), false)));
            jarg.setVarArgs(argument.isVarArgs()); // todo: test for varargs
            method.addArgument(jarg);
        }
    }

    private String fixType(String type, boolean returnValue) {
        if (type.endsWith("?")) {
            // todo: nullable primitives should be boxed
            type = type.substring(0, type.length() - 1);
        }

        if (type.startsWith("sequence<")) {
            type = box(fixType(type.substring(9, type.length() - 1), returnValue)) + "[]";
//            returnType = "java.util.ArrayList<" + box(fixType(returnType.substring(9, returnType.length() - 1))) + ">";
        }

        if (type.startsWith("Promise<")) {
            return "Object";
        }

        Definition resolved = resolver.resolve(type);
        if (resolved != null) {
            if (resolved instanceof Enumeration) {
                return "String";
            }

            if (resolved instanceof Callback) {

            }
        }

        switch (type) {
            case "void":
                return "void";

            case "DOMString":
            case "USVString":
            case "ByteString":
                return "String";

            case "short":
            case "unsignedshort":
                return "short";
            case "int":
            case "long":
            case "unsignedlong":
                return "int"; // long-int
            case "float":
                return "float";
            case "double":
            case "unrestricteddouble":
                return "double";
            case "boolean":
                return "boolean";

            case "ArrayBuffer":
                return "com.google.gwt.typedarrays.shared.ArrayBuffer";
            case "ArrayBufferView":
                return "com.google.gwt.typedarrays.shared.ArrayBufferView";

            case "Elements":
                return "Element[]";

            case "DOMTimeStamp":
            case "DOMHighResTimeStamp":
                return "Object"; // this really should be long


            case "any":
            case "object":

            case "Uint8ClampedArray":
            case "EventHandler":
            case "OnErrorEventHandler":
            case "OnBeforeUnloadEventHandler":
            case "FileList":
            case "MediaProvider":
            case "MediaStream":
            case "MediaSource":
            case "DOMMatrix":
            case "DOMMatrixInit":
            case "Function":
            case "WindowProxy":
            case "RenderingContext":
            case "WebGLRenderingContext":
            case "MutationObserverInit":
            case "Transferable":
            case "CanvasImageSource":
            case "HitRegionOptions":
            case "ImageBitmapSource":
            case "Blob": // https://html.spec.whatwg.org/#refsFILEAPI
            case "File": // https://html.spec.whatwg.org/#refsFILEAPI
                return "Object";
        }

        return resolved == null || "Object".equals(type) ? "Object" : resolved instanceof Interface ? ((Interface) resolved).getJavaName() : type;
    }

    private String box(String type) {
        switch (type) {
            case "int":
            case "integer":
                return "Integer";
            case "double":
                return "Double";
        }
        return type;
    }

    private String fixName(String name) {
        switch (name) {
            case "interface":
                return "interface_";
            case "default":
                return "return_";
            default:
                return name;
        }
    }

    private String makeIdentifier(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == 0 && Character.isJavaIdentifierStart(c)) {
                sb.append(c);
            } else if (Character.isJavaIdentifierPart(c)) {
                sb.append(c);
            } else {
                sb.append("_");
            }
        }
        return sb.toString();
    }


    private void addMethodImplementations() {
        Map<String, JClass> types = javaModel.getTypes();
        for (JClass jClass : types.values()) {
            if (jClass.getAbstraction() == AbstractionLevel.CLASS) {
                Set<JMethod> methods = new LinkedHashSet<>();
                String superTypeId = jClass.getSuperType();
                if (superTypeId != null) {
                    collectMethods(types, methods, superTypeId);
                }

                for (String anInterface : jClass.getInterfaces()) {
                    collectMethods(types, methods, anInterface);
                }

                for (JMethod method : methods) {
                    jClass.addMethod(method);
                }
            }
        }
    }

    private void addConstructors() {
        Map<String, JClass> types = javaModel.getTypes();
        for (JClass jClass : types.values()) {
            if (jClass.getAbstraction() != AbstractionLevel.INTERFACE) {
                Set<JMethod> methods = new LinkedHashSet<>();

                String superTypeId = jClass.getSuperType();
                if (superTypeId != null) {
                    collectConstructors(types, methods, superTypeId);
                }

                for (String anInterface : jClass.getInterfaces()) {
                    collectConstructors(types, methods, anInterface);
                }

                for (JMethod method : methods) {
                    JMethod ctor = new JMethod(jClass.getName());
                    for (JArgument argument : method.getArguments()) {
                        ctor.addArgument(argument);
                    }
                    jClass.addConstructor(ctor);
                }
            }
        }
    }

    private void addPackagDefaultConstructors() {
        Map<String, JClass> types = javaModel.getTypes();
        for (JClass jClass : types.values()) {
            if (jClass.getAbstraction() != AbstractionLevel.INTERFACE) {
                JMethod ctor = new JMethod(jClass.getName());
                ctor.setVisibility(Visibility.PROTECTED);
                jClass.addConstructor(ctor);
            }
        }
    }

    private void collectConstructors(Map<String, JClass> types, Set<JMethod> methods, String typeId) {
        JClass type = types.get(typeId);
        if (type.getAbstraction() != AbstractionLevel.INTERFACE) {
            methods.addAll(type.getConstructors());
        }
        String superType = type.getSuperType();
        if (superType != null) {
            collectConstructors(types, methods, superType);
        }
    }

    private void collectMethods(Map<String, JClass> types, Set<JMethod> methods, String typeId) {
        JClass type = types.get(typeId);
        if (type.getAbstraction() != AbstractionLevel.CLASS) {
            methods.addAll(type.getMethods());
        }
        for (String anInterface : type.getInterfaces()) {
            collectMethods(types, methods, anInterface);
        }
        String superType = type.getSuperType();
        if (superType != null) {
            collectMethods(types, methods, superType);
        }
    }

    private void collectInterfaceFields(Map<String, JClass> types, Set<JField> fields, String typeId) {
        JClass type = types.get(typeId);
        if (type.getAbstraction() == AbstractionLevel.INTERFACE) {
            fields.addAll(type.getFields());
        }
        for (String anInterface : type.getInterfaces()) {
            collectInterfaceFields(types, fields, anInterface);
        }
        String superType = type.getSuperType();
        if (superType != null) {
            collectInterfaceFields(types, fields, superType);
        }
    }

}
