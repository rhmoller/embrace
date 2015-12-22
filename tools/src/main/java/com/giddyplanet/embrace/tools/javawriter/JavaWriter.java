package com.giddyplanet.embrace.tools.javawriter;

import com.giddyplanet.embrace.tools.model.TypeResolver;
import com.giddyplanet.embrace.tools.model.java.*;
import com.giddyplanet.embrace.tools.model.webidl.*;
import com.giddyplanet.embrace.tools.model.webidl.Enumeration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class JavaWriter {
    private static final String INDENT = "    ";
    private final File srcFolder;
    private File packageFolder;
    private String javaPackage;
    private TypeResolver resolver;

    public JavaWriter(File srcFolder, String javaPackage, TypeResolver resolver) {
        this.srcFolder = srcFolder;
        this.javaPackage = javaPackage;
        this.resolver = resolver;
        if (srcFolder != null && javaPackage != null) {
            packageFolder = getPackagePath(srcFolder, javaPackage);
        } else {
            packageFolder = srcFolder;
        }
    }

    private File getPackagePath(File srcFolder, String javaPackage) {
        String[] parts = javaPackage.split("\\.");
        File parent = srcFolder;
        for (String part : parts) {
            File folder = new File(parent, part);
            folder.mkdirs();
            parent = folder;
        }
        return parent;
    }

    public String createSource(JClass jClass) {
        StringBuilder sb = new StringBuilder();
        if (javaPackage != null) {
            sb.append("package ").append(javaPackage).append(";\n");
            sb.append("\n");
        }

        if (jClass.isFunctional()) {
            sb.append("import jsinterop.annotations.JsFunction;\n");
        }
        sb.append("import jsinterop.annotations.JsIgnore;\n");
        sb.append("import jsinterop.annotations.JsOverlay;\n");
        sb.append("import jsinterop.annotations.JsPackage;\n");
        sb.append("import jsinterop.annotations.JsProperty;\n");
        if (!jClass.isFunctional()) {
            sb.append("import jsinterop.annotations.JsType;\n");
        }
        sb.append("\n");

        if (jClass.isFunctional()) {
            sb.append("@JsFunction\n");
        } else if (!jClass.isNoInterfaceObject()) {
            sb.append("@JsType(isNative = true, namespace = JsPackage.GLOBAL)\n");
        }
        AbstractionLevel absLvl = jClass.getAbstraction();
        switch (absLvl) {
            case INTERFACE: {
                sb.append("public interface ").append(jClass.getName());
                LinkedHashSet<String> interfaces = jClass.getInterfaces();
                writeInterfaces(sb, interfaces, " extends ");
                sb.append(" {\n");
                break;
            }
            case ABSTRACT_CLASS: {
                sb.append("public abstract class ").append(jClass.getName());
                String superTypeName = jClass.getSuperType();
                if (superTypeName != null) {
                    sb.append(" extends ").append(superTypeName).append(" ");
                }
                LinkedHashSet<String> interfaces = jClass.getInterfaces();
                writeInterfaces(sb, interfaces, " implements ");
                sb.append(" {\n");
                break;
            }
            case CLASS: {
                sb.append("public class ").append(jClass.getName());
                String superTypeName = jClass.getSuperType();
                if (superTypeName != null) {
                    sb.append(" extends ").append(superTypeName).append(" ");
                }
                LinkedHashSet<String> interfaces = jClass.getInterfaces();
                writeInterfaces(sb, interfaces, " implements ");
                sb.append(" {\n");
                break;
            }
        }

        for (JConstant jConstant : jClass.getConstants()) {
            switch (absLvl) {
                case INTERFACE:
                    if (jClass.isNoInterfaceObject()) {
                        String type = fixType(jConstant.getType().getName());
                        sb.append(INDENT).append("public static ").append(type).append(" ").append(jConstant.getName()).append(" = ").append(jConstant.getValue()).append(";\n");
                    }
                    break;
                case ABSTRACT_CLASS:
                case CLASS:
                    String type = fixType(jConstant.getType().getName());
                    sb.append(INDENT).append("public static ").append(type).append(" ").append(jConstant.getName()).append("; // = ").append(jConstant.getValue()).append("\n");
                    break;
            }
        }

        for (JField field : jClass.getFields()) {
            switch (absLvl) {
                case INTERFACE:
                    break;
                case ABSTRACT_CLASS:
                case CLASS:
                    sb.append(INDENT).append("public ").append(field.getType().getName()).append(" ").append(field.getName()).append(";\n");
                    break;
            }
        }

        for (JMethod method : jClass.getConstructors()) {
            sb.append(INDENT).append(method.getVisibility()).append(" ").append(method.getName()).append("(");
            writeArguments(sb, method);
            sb.append(") {}\n");
        }

        for (JMethod method : jClass.getMethods()) {
            switch (absLvl) {
                case INTERFACE:
                    sb.append(INDENT).append(method.getReturnType().getName()).append(" ").append(method.getName()).append("(");
                    writeArguments(sb, method);
                    sb.append(");\n");
                    break;
                case ABSTRACT_CLASS:
                    sb.append(INDENT).append("public ");
                    if (method.isaStatic()) {
                        sb.append("static ");
                    }
                    sb.append("native ").append(method.getReturnType().getName()).append(" ").append(method.getName()).append("(");
                    writeArguments(sb, method);
                    sb.append(");\n");
                    break;
                case CLASS:
                    sb.append(INDENT).append("public ");
                    if (method.isaStatic()) {
                        sb.append("static ");
                    }
                    sb.append("native ").append(method.getReturnType().getName()).append(" ").append(method.getName()).append("(");
                    writeArguments(sb, method);
                    sb.append(");\n");
                    break;
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private void writeInterfaces(StringBuilder sb, LinkedHashSet<String> interfaces, String keyword) {
        if (!interfaces.isEmpty()) {
            sb.append(keyword);
            for (Iterator<String> iterator = interfaces.iterator(); iterator.hasNext(); ) {
                String anInterface = iterator.next();
                sb.append(anInterface);
                if (iterator.hasNext()) {
                    sb.append(", ");
                }
            }
        }
    }


    public void createSourceFile(Definition definition) throws IOException {
        if (definition instanceof Interface) {
            String src = createSource((Interface) definition);
            File srcFile = new File(packageFolder, ((Interface) definition).getJavaName() + ".java");
            Files.write(srcFile.toPath(), src.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } else if (definition instanceof Enumeration) {
            Enumeration e = (Enumeration) definition;
            String src = createSource(e);
            File srcFile = new File(packageFolder, e.getName() + ".java");
            Files.write(srcFile.toPath(), src.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        } else if (definition instanceof Callback) {
            Callback callback = (Callback) definition;
            String src = createSource(callback);
            File srcFile = new File(packageFolder, callback.getName() + ".java");
            Files.write(srcFile.toPath(), src.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        }
    }

    public void createSourceFile(JClass definition) throws IOException {
        String src = createSource(definition);
        File srcFile = new File(packageFolder, definition.getName() + ".java");
        Files.write(srcFile.toPath(), src.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private String createSource(Enumeration e) {
        StringBuilder sb = new StringBuilder();
        if (javaPackage != null) {
            sb.append("package ").append(javaPackage).append(";\n");
            sb.append("\n");
        }

        sb.append("import jsinterop.annotations.JsIgnore;\n");
        sb.append("import jsinterop.annotations.JsOverlay;\n");
        sb.append("import jsinterop.annotations.JsPackage;\n");
        sb.append("import jsinterop.annotations.JsProperty;\n");
        sb.append("import jsinterop.annotations.JsType;\n");
        sb.append("\n");

        sb.append("public interface " + e.getName() + " {\n");
        for (String s : e.getValues()) {
            sb.append(INDENT).append("String ").append(makeIdentifier(s).toUpperCase()).append(" = \"").append(s).append("\";\n");
        }
        sb.append("}\n");
        return sb.toString();
    }

    private String createSource(Callback callback) {
        StringBuilder sb = new StringBuilder();
        if (javaPackage != null) {
            sb.append("package ").append(javaPackage).append(";\n");
            sb.append("\n");
        }
        sb.append("import jsinterop.annotations.JsFunction;\n");
        sb.append("import jsinterop.annotations.JsIgnore;\n");
        sb.append("import jsinterop.annotations.JsOverlay;\n");
        sb.append("import jsinterop.annotations.JsPackage;\n");
        sb.append("import jsinterop.annotations.JsProperty;\n");
        sb.append("import jsinterop.annotations.JsType;\n");
        sb.append("\n");

        sb.append("@JsFunction\n");

        sb.append("public interface ").append(callback.getName()).append(" {\n");
        sb.append(INDENT).append(fixType(callback.getReturnType()));
        sb.append(" ").append("execute").append("(");
        for (Iterator<Argument> iterator = callback.getArguments().iterator(); iterator.hasNext(); ) {
            Argument argument = iterator.next();
            sb.append(fixType(argument.getType()));
            if (argument.isVarArgs()) {
                sb.append("...");
            }
            sb.append(" ");
            sb.append(fixName(argument.getName()));
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append(");\n");
        sb.append("}\n");
        return sb.toString();
    }


    public String createSource(Interface definition) {
        StringBuilder sb = new StringBuilder();

        if (javaPackage != null) {
            sb.append("package ").append(javaPackage).append(";\n");
            sb.append("\n");
        }
        sb.append("import jsinterop.annotations.JsFunction;\n");
        sb.append("import jsinterop.annotations.JsIgnore;\n");
        sb.append("import jsinterop.annotations.JsOverlay;\n");
        sb.append("import jsinterop.annotations.JsPackage;\n");
        sb.append("import jsinterop.annotations.JsProperty;\n");
        sb.append("import jsinterop.annotations.JsType;\n");
        sb.append("\n");

        if (definition.isCallback()) {
            sb.append("@JsFunction\n");
        } else {
            if (definition.getName().equals(definition.getJavaName())) {
                sb.append("@JsType(isNative = true, namespace = JsPackage.GLOBAL)\n");
            } else {
                sb.append("@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = \"" + definition.getName() + "\")\n");
            }
        }

        Set<String> extendedAttributes = definition.getExtendedAttributes();
        long constructorCount = definition.getConstructors().size();
        Interface superType = definition.getSuperType().getResolved();

        boolean isAbstract = false;
        boolean isInterface = false;

        String typeName = definition.getJavaName();

        if (constructorCount > 0) {
            isAbstract = false;
            isInterface = false;
            sb.append("public class ").append(typeName);
            boolean superIsInterface = false;
            if (superType != null) {
                superIsInterface = superType.isCallback() || superType.getExtendedAttributes().stream().anyMatch(s -> "NoInterfaceObject".equals(s));
                if (superIsInterface) {
                    if (definition.getInterfaces().isEmpty()) {
                        sb.append(" implements ").append(superType.getName());
                    }
                } else {
                    sb.append(" extends ").append(superType.getName());
                }
            }
            writeInterfaces((Interface) definition, sb, " implements ");
            if (superIsInterface && !definition.getInterfaces().isEmpty()) {
                sb.append(", ").append(superType.getName());
            }
            sb.append(" {\n");
        } else {
            if (definition.isCallback() || extendedAttributes.stream().anyMatch(s -> "NoInterfaceObject".equals(s))) {
                isAbstract = false;
                isInterface = true;
                sb.append("public interface ").append(typeName);
                writeInterfaces(definition, sb, " extends ");
                if (superType != null) {
                    if (definition.getInterfaces().isEmpty()) {
                        sb.append(" extends ");
                    }
                    sb.append(superType.getName());
                }
                sb.append(" {\n");
            } else {
                isAbstract = true;
                isInterface = false;
                sb.append("public abstract class ").append(typeName);
                if (superType != null) {
                    sb.append(" extends ").append(superType.getName());
                }
                writeInterfaces((Interface) definition, sb, " implements ");
                sb.append(" {\n");
            }
        }

        for (Constant constant : ((Interface) definition).getConstants()) {
            if (isInterface) {
                if (!definition.isCallback()) {
                    sb.append(INDENT).append("@JsOverlay ");
                }
                sb.append(INDENT).append("public static final ").append(fixType(constant.getType())).append(" ").append(constant.getName()).append("= ").append(constant.getValue()).append(";\n");
            } else {
                sb.append(INDENT).append("public static ").append(fixType(constant.getType())).append(" ").append(constant.getName()).append("; // = ").append(constant.getValue()).append("\n");
            }
        }

        if (!isInterface) {
            for (Attribute attribute : definition.getAttributes()) {
                sb.append(INDENT).append("public ").append(fixType(attribute.getType())).append(" ").append(fixName(attribute.getName())).append(";\n");
            }

            HashSet<Attribute> attributes = new HashSet<>();
            collectNoInterfaceObjectAttributes(definition, attributes);
            for (Attribute attribute : attributes) {
                sb.append(INDENT).append("public ").append(fixType(attribute.getType())).append(" ").append(fixName(attribute.getName())).append(";\n");
            }
        }


        boolean missingDefaultConstructor = (!isAbstract && !isInterface && constructorCount > 0);

        for (Operation constructor : definition.getConstructors()) {
            boolean wroteEmpty = writeConstructor(definition, sb, constructor);
            missingDefaultConstructor &= !wroteEmpty;
        }

        if (missingDefaultConstructor) {
            sb.append(INDENT).append("protected ").append(definition.getName()).append("() {}\n");
        }

        for (Operation operation : ((Interface) definition).getOperations()) {
            // todo: hack for HTMLFormControlsCollection
            if ("namedItem".equals(operation.getName()) && "HTMLCollection".equals(definition.getName())) {
                operation.setReturnType("Object");
            }
            writeOperation(sb, isInterface, operation);
        }

        if (!(isAbstract || isInterface)) {
            HashSet<Operation> abstracts = new HashSet<>();
            collectAbstractMethods((Interface) definition, abstracts);
            for (Operation operation : abstracts) {
                writeOperation(sb, isInterface, operation);
            }
        }

        sb.append("}\n");
        return sb.toString();
    }

    private boolean writeConstructor(Interface definition, StringBuilder sb, Operation constructor) {
        boolean wroteEmptyConstructor = false;

        sb.append(INDENT).append("public ").append(definition.getName()).append("(");
        writeArguments(sb, constructor);
        sb.append(") {}\n");
        List<Argument> arguments = constructor.getArguments();
        if (arguments.isEmpty()) {
            wroteEmptyConstructor = true;
        }

        if (arguments.size() > 0 && arguments.get(arguments.size() - 1).isOptional()) {
            Operation op2 = new Operation(constructor.getName());
            for (Iterator<Argument> iterator = arguments.iterator(); iterator.hasNext(); ) {
                Argument arg = iterator.next();
                if (iterator.hasNext()) {
                    op2.addArgument(arg.getType(), arg.getName());
                }
            }
            wroteEmptyConstructor = writeConstructor(definition, sb, op2);
        }

        return wroteEmptyConstructor;
    }

    private void collectNoInterfaceObjectAttributes(Interface definition, Set<Attribute> attributes) {
        if (definition.getExtendedAttributes().contains("NoInterfaceObject")) {
            attributes.addAll(definition.getAttributes());
        }
        LinkedHashSet<Interface> interfaces = definition.getInterfaces();
        for (Interface anInterface : interfaces) {
            collectNoInterfaceObjectAttributes(anInterface, attributes);
        }
        Interface superType = definition.getSuperType().getResolved();
        if (superType != null && superType.getExtendedAttributes().contains("NoInterfaceObject")) {
            collectNoInterfaceObjectAttributes(superType, attributes);
        }
    }

    private void collectAbstractMethods(Interface definition, Set<Operation> ops) {
        if (definition.getExtendedAttributes().contains("NoInterfaceObject") || definition.getConstructors().isEmpty()) {
            ops.addAll(definition.getOperations());
        }

        LinkedHashSet<Interface> interfaces = definition.getInterfaces();
        for (Interface anInterface : interfaces) {
            collectAbstractMethods(anInterface, ops);
        }

        Interface superType = definition.getSuperType().getResolved();
        if (superType != null) {
            collectAbstractMethods(superType, ops);
        }
    }

    private void writeOperation(StringBuilder sb, boolean isInterface, Operation operation) {
        if (isInterface) {
            sb.append(fixType(operation.getReturnType()));
        } else {
            if (operation.isStatic()) {
                sb.append(INDENT).append("public static native ");
            } else {
                sb.append(INDENT).append("public native ");
            }
            sb.append(fixType(operation.getReturnType()));
        }
        sb.append(" ").append(operation.getName()).append("(");
        List<Argument> arguments = writeArguments(sb, operation);
        sb.append(");\n");

        if (arguments.size() > 0 && arguments.get(arguments.size() - 1).isOptional()) {
            Operation op2 = new Operation(operation.getName());
            op2.setReturnType(operation.getReturnType());
            for (Iterator<Argument> iterator = arguments.iterator(); iterator.hasNext(); ) {
                Argument arg = iterator.next();
                if (iterator.hasNext()) {
                    op2.addArgument(arg.getType(), arg.getName());
                }
            }
            writeOperation(sb, isInterface, op2);
        }
    }

    private void writeArguments(StringBuilder sb, JMethod method) {
        LinkedList<JArgument> arguments = method.getArguments();
        for (Iterator<JArgument> iterator = arguments.iterator(); iterator.hasNext(); ) {
            JArgument argument = iterator.next();
            sb.append(argument.getType().getName());
            if (argument.isVarArgs()) {
                sb.append("...");
            }
            sb.append(" ");
            sb.append(fixName(argument.getName()));
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
    }

    private List<Argument> writeArguments(StringBuilder sb, Operation operation) {
        List<Argument> arguments = operation.getArguments();
        for (Iterator<Argument> iterator = arguments.iterator(); iterator.hasNext(); ) {
            Argument argument = iterator.next();
            sb.append(fixType(argument.getType()));
            if (argument.isVarArgs()) {
                sb.append("...");
            }
            sb.append(" ");
            sb.append(fixName(argument.getName()));
            if (iterator.hasNext()) {
                sb.append(", ");
            }
        }
        return arguments;
    }

    private void writeInterfaces(Interface definition, StringBuilder sb, String prefix) {
        LinkedHashSet<Interface> interfaces = definition.getInterfaces();
        if (!interfaces.isEmpty()) {
            sb.append(prefix);
            for (Iterator<Interface> iterator = interfaces.iterator(); iterator.hasNext(); ) {
                Interface anInterface = iterator.next();
                sb.append(anInterface.getName());
                if (iterator.hasNext()) sb.append(", ");
            }
        }
    }

    private String fixType(String type) {
        if (type.endsWith("?")) {
            // todo: nullable primitives should be boxed
            type = type.substring(0, type.length() - 1);
        }

        if (type.startsWith("sequence<")) {
            type = box(fixType(type.substring(9, type.length() - 1))) + "[]";
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

}
