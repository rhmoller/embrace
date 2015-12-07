package com.giddyplanet.embrace.tools.javawriter;

import com.giddyplanet.embrace.tools.model.webidl.*;
import com.giddyplanet.embrace.tools.model.webidl.Enumeration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

// todo: another step should build a Java model from the Web IDL model
// todo: this class should only do a dumb serialization of this Java model
public class JavaWriter {
    public static final String INDENT = "    ";
    private Model model;
    private String javaPackage = "com.giddyplanet.webidl.dom";
    private File srcFolder;
    private File packageFolder;

    public JavaWriter(File srcFolder, String javaPackage) {
        this.srcFolder = srcFolder;
        this.javaPackage = javaPackage;
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

    public void createSourceFile(Definition type) throws IOException {
        if (type instanceof Interface) {
            Interface interface_ = (Interface) type;
            String src = createSource(interface_);
            File srcFile = new File(packageFolder, interface_.getName() + ".java");
            Files.write(srcFile.toPath(), src.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } else if (type instanceof Enumeration) {
            Enumeration e = (Enumeration) type;
            String src = createSource(e);
            System.out.println("Writing enum " + src);
            File srcFile = new File(packageFolder, e.getName() + ".java");
            Files.write(srcFile.toPath(), src.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        } else if (type instanceof Callback) {
            Callback callback = (Callback) type;
            String src = createSource(callback);
            File srcFile = new File(packageFolder, callback.getName() + ".java");
            Files.write(srcFile.toPath(), src.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        }
    }

    private String createSource(Enumeration e) {
        StringBuilder sb = new StringBuilder();
        if (javaPackage != null) {
            sb.append("package ").append(javaPackage).append(";\n");
            sb.append("public enum " + e.getName() + " {\n");
            for (Iterator<String> iterator = e.getValues().iterator(); iterator.hasNext(); ) {
                String s = iterator.next();
                sb.append(INDENT);
                sb.append(makeIdentifier(s)).append("(\"").append(s).append("\")");
                if (iterator.hasNext()) {
                    sb.append(",\n");
                } else {
                    sb.append(";\n");
                }
            }
            sb.append(INDENT).append("private final String text;\n");
            sb.append(INDENT).append("private ").append(e.getName()).append("(String text) {\n");
            sb.append(INDENT).append(INDENT).append("this.text = text;\n");
            sb.append(INDENT).append("}\n");
            sb.append(INDENT).append("public String toString() {\n");
            sb.append(INDENT).append(INDENT).append("return text;\n");
            sb.append(INDENT).append("}\n");
            sb.append("}\n");
        }

        return sb.toString();
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

    private String createSource(Callback callback) {
        StringBuilder sb = new StringBuilder();
        if (javaPackage != null) {
            sb.append("package ").append(javaPackage).append(";\n");
        }
        sb.append("import jsinterop.annotations.JsFunction;\n");
        sb.append("@JsFunction\n");
        sb.append("public interface " + callback.getName() + " {\n");
        sb.append("    ");
        String returnType = callback.getReturnType();
        returnType = fixType(returnType);
        sb.append(returnType);
        sb.append(" exec(");

        //noinspection Duplicates
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

    public String createSource(Interface type) {
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

        sb.append("@JsType(isNative = true, namespace = JsPackage.GLOBAL)\n");
        sb.append("public ");
        List<HasArguments> constructors = type.getConstructors();
        boolean hasSuperWithConstructors = hasSuperTypeWithConstructors(type);
        boolean isInterface = constructors.isEmpty() && !hasSuperWithConstructors;
        if (isInterface) {
            sb.append("interface ");
        } else {
            sb.append("class ");
        }
        sb.append(type.getName());
        Interface superType = type.getSuperType();
        boolean superIsInterface = superType != null && !superType.hasConstructors() && !hasSuperTypeWithConstructors(superType);
        if (superType != null) {
            if (isInterface) {
                sb.append(" extends ").append(superType.getName());
            } else {
                if (superIsInterface) {
                    sb.append(" implements ").append(superType.getName());
                } else {
                    sb.append(" extends ").append(superType.getName());
                }
            }
        }

        if (!type.getInterfaces().isEmpty())  {
            if (type.getInterfaces().size() > 1 || superType == null) {
                if ((superType != null && !isInterface && superIsInterface) || (superType != null && isInterface)) {
                    sb.append(", ");
                } else {
                    if (isInterface) {
                        sb.append(" extends ");
                    } else {
                        sb.append(" implements ");
                    }
                }

                for (Iterator<Interface> iterator = type.getInterfaces().iterator(); iterator.hasNext(); ) {
                    Interface anInterface = iterator.next();
                    if (anInterface.equals(superType)) continue;
                    sb.append(anInterface.getName());
                    if (iterator.hasNext()) {
                        sb.append(", ");
                    }
                }
            }
        }

        sb.append(" {\n");

        for (Constant constant : type.getConstants()) {
            sb.append(INDENT);
            sb.append("@JsOverlay ");
            sb.append("public static final ");
            sb.append(fixType(constant.getType()));
            sb.append(" ");
            sb.append(constant.getName());
            sb.append(" = ");
            sb.append(constant.getValue());
            sb.append(";\n");
        }

        HashSet<Interface> interfaces = new HashSet<>();
        collectInterfaces(type, interfaces);

        for (Attribute attribute : type.getAttributes()) {
            String name = attribute.getName();
            sb.append(INDENT);
            sb.append("@JsProperty(name = \"");
            sb.append(name);
            sb.append("\") ");

            if (!isInterface) {
                sb.append("public native ");
            }

            sb.append(" ");
            sb.append(fixType(attribute.getType()));
            sb.append(" get");
            sb.append(Character.toUpperCase(name.charAt(0)));
            sb.append(name.substring(1));
            sb.append("()");

            sb.append(";");

            sb.append("\n");

            if (!attribute.isReadOnly()) {
                sb.append(INDENT);
                sb.append("@JsProperty(name = \"");
                sb.append(name);
                sb.append("\") ");

                if (!isInterface) {
                    sb.append("public native ");
                }

                sb.append(" void ");
                sb.append(" set");
                sb.append(Character.toUpperCase(name.charAt(0)));
                sb.append(name.substring(1));
                sb.append("(");
                sb.append(fixType(attribute.getType()));
                sb.append(" value");
                sb.append(")");

                sb.append(";");

                sb.append("\n");
            }
        }


        for (Operation operation : type.getOperations()) {
            sb.append(INDENT);
            if (!isInterface) {
                sb.append("public native ");
            }
            String returnType = operation.getReturnType();
            returnType = fixType(returnType);

            // todo HACK: Element? HTMLCollection.namedItem(DOMString name) conflict with (RadioNodeList or Element)? HTMLFormControlsCollection.namedItem(DOMString name)
            if ("HTMLCollection".equals(type.getName()) && "namedItem".equals(operation.getName())) {
                returnType = "Object";
            }

            sb.append(returnType);
            sb.append(" ");
            sb.append(operation.getName());
            sb.append("(");
            //noinspection Duplicates
            for (Iterator<Argument> iterator = operation.getArguments().iterator(); iterator.hasNext(); ) {
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

            List<Argument> arguments = operation.getArguments();
            if (!arguments.isEmpty()) {
                Argument last = arguments.get(arguments.size() - 1);
                if (last.isOptional()) {


                    sb.append(INDENT);
                    if (!isInterface) {
                        sb.append("public native ");
                    }
                    returnType = operation.getReturnType();
                    returnType = fixType(returnType);

                    // todo HACK: Element? HTMLCollection.namedItem(DOMString name) conflict with (RadioNodeList or Element)? HTMLFormControlsCollection.namedItem(DOMString name)
                    if ("HTMLCollection".equals(type.getName()) && "namedItem".equals(operation.getName())) {
                        returnType = "Object";
                    }

                    sb.append(returnType);
                    sb.append(" ");
                    sb.append(operation.getName());
                    sb.append("(");
                    //noinspection Duplicates
                    for (int i = 0; i < arguments.size(); i++) {
                        Argument argument = arguments.get(i);
                        if (argument.isOptional()) break;
                        sb.append(fixType(argument.getType()));
                        if (argument.isVarArgs()) {
                            sb.append("...");
                        }
                        sb.append(" ");
                        sb.append(fixName(argument.getName()));
                        if (i < arguments.size() -1 && !arguments.get(i + 1).isOptional()) {
                            sb.append(", ");
                        }
                    }
                    sb.append(");\n");

                }
            }
        }


        if (!isInterface) {
            for (Interface t : interfaces) {
                for (Attribute attribute : t.getAttributes()) {
                    String name = attribute.getName();
                    sb.append(INDENT);
                    sb.append("@JsProperty(name = \"");
                    sb.append(name);
                    sb.append("\") ");

                    sb.append("public native ");

                    sb.append(" ");
                    sb.append(fixType(attribute.getType()));
                    sb.append(" get");
                    sb.append(Character.toUpperCase(name.charAt(0)));
                    sb.append(name.substring(1));
                    sb.append("()");

                    sb.append(";");

                    sb.append("\n");

                    if (!attribute.isReadOnly()) {
                        sb.append(INDENT);
                        sb.append("@JsProperty(name = \"");
                        sb.append(name);
                        sb.append("\") ");

                        sb.append("public native ");

                        sb.append(" void ");
                        sb.append(" set");
                        sb.append(Character.toUpperCase(name.charAt(0)));
                        sb.append(name.substring(1));
                        sb.append("(");
                        sb.append(fixType(attribute.getType()));
                        sb.append(" value");
                        sb.append(")");

                        sb.append(";");

                        sb.append("\n");
                    }
                }

                for (Operation operation : t.getOperations()) {
                    sb.append(INDENT);
                    sb.append("public native ");
                    String returnType = operation.getReturnType();
                    returnType = fixType(returnType);
                    sb.append(returnType);
                    sb.append(" ");
                    sb.append(operation.getName());
                    sb.append("(");
                    //noinspection Duplicates
                    for (Iterator<Argument> iterator = operation.getArguments().iterator(); iterator.hasNext(); ) {
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


                    List<Argument> arguments = operation.getArguments();
                    if (!arguments.isEmpty()) {
                        Argument last = arguments.get(arguments.size() - 1);
                        if (last.isOptional()) {


                            sb.append(INDENT);
                            if (!isInterface) {
                                sb.append("public native ");
                            }
                            returnType = operation.getReturnType();
                            returnType = fixType(returnType);

                            // todo HACK: Element? HTMLCollection.namedItem(DOMString name) conflict with (RadioNodeList or Element)? HTMLFormControlsCollection.namedItem(DOMString name)
                            if ("HTMLCollection".equals(type.getName()) && "namedItem".equals(operation.getName())) {
                                returnType = "Object";
                            }

                            sb.append(returnType);
                            sb.append(" ");
                            sb.append(operation.getName());
                            sb.append("(");
                            //noinspection Duplicates
                            for (int i = 0; i < arguments.size(); i++) {
                                Argument argument = arguments.get(i);
                                if (argument.isOptional()) break;
                                sb.append(fixType(argument.getType()));
                                if (argument.isVarArgs()) {
                                    sb.append("...");
                                }
                                sb.append(" ");
                                sb.append(fixName(argument.getName()));
                                if (i < arguments.size() -1 && !arguments.get(i + 1).isOptional()) {
                                    sb.append(", ");
                                }
                            }
                            sb.append(");\n");

                        }
                    }

                }
            }
        }

        sb.append("}\n");

        return sb.toString();
    }

    private void collectInterfaces(Interface type, Set<Interface> result) {
        Interface superType = type.getSuperType();
        if (superType != null && !superType.hasConstructors()) {
            result.add(superType);
            collectInterfaces(superType, result);
        }
        LinkedHashSet<Interface> interfaces = type.getInterfaces();
        result.addAll(interfaces);
        for (Interface anInterface : interfaces) {
            collectInterfaces(anInterface, result);
        }
    }

    private boolean hasSuperTypeWithConstructors(Interface type) {
        Interface superType = type.getSuperType();
        while (superType != null) {
            if (superType.hasConstructors()) {
                return true;
            }
            superType = superType.getSuperType();
        }
        return false;
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

    private String fixType(String returnType) {
        if (returnType.endsWith("?")) {
            returnType = returnType.substring(0, returnType.length() - 1);
        }

        if (returnType.startsWith("sequence<")) {
            returnType = "java.util.ArrayList<" + box(fixType(returnType.substring(9, returnType.length() - 1))) + ">";
        }

        if (returnType.startsWith("Promise<")) {
            return "Object";
        }

        switch (returnType) {
            case "DOMString":
            case "USVString":
            case "ByteString":
                return "String";

            case "any":
                return "Object";
            case "object":
                return "Object";

            case "unsignedshort":
                return "short";
            case "unsignedlong":
                return "int"; // long-int
            case "unrestricteddouble":
                return "double";

            case "ArrayBuffer":
                return "com.google.gwt.typedarrays.shared.ArrayBuffer";
            case "ArrayBufferView":
                return "com.google.gwt.typedarrays.shared.ArrayBufferView";

            case "Elements":
                return "Element[]";

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
            case "DOMTimeStamp":
            case "DOMHighResTimeStamp":
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

        return returnType;
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
}
