package com.giddyplanet.embrace.tools;

import com.giddyplanet.embrace.tools.javawriter.JavaWriter;
import com.giddyplanet.embrace.tools.model.webidl.Definition;
import com.giddyplanet.embrace.tools.webidl2java.ModelBuildingListener;
import com.giddyplanet.embrace.webidl.parser.WebIDLLexer;
import com.giddyplanet.embrace.webidl.parser.WebIDLParser;
import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.List;

/**
 * todo: union
 * todo: typedef
 * todo: dictionary
 * todo: promise
 * todo: sequence
 * todo: exception
 * todo: how to handle longs and unsigned longs
 */
public class WebIdlToJava {

    public interface Options {
        @Option(description = "HTML (.html) files containing WebIDL fragments or WebIDL (.idl) files", defaultToNull = true)
        List<File> getIdlFiles();

        @Option(description = "Folder containing the .idl files to convert", defaultToNull = true)
        File getIdlFolder();

        @Option(description = "Root folder for generated sources",
                defaultToNull = true)
        File getOutDir();

        @Option(description = "Java package for generated sources", defaultToNull = true)
        String getPackage();
    }

    public static void main(String[] args) throws IOException {
        Options options = CliFactory.parseArguments(Options.class, args);

        ModelBuildingListener listener = new ModelBuildingListener();

        List<File> idlFiles = options.getIdlFiles();
        File idlFolder = options.getIdlFolder();
        File outDir = options.getOutDir();
        String javaPackage = options.getPackage();

        if (outDir == null) {
            System.err.println("outDir is a required parameter.");
            System.exit(1);
        }

        if (idlFiles == null && idlFolder == null) {
            System.err.println("must specify either idlFiles or idlFolder");
            System.exit(1);
        }

        if (idlFiles != null) {
            for (File idlFile : idlFiles) {
                handleFile(listener, idlFile);
            }
        }

        if (idlFolder != null) {
            File[] files = idlFolder.listFiles();
            for (File file : files) {
                handleFile(listener, file);
            }
        }

        JavaWriter writer = new JavaWriter(outDir, javaPackage);
        for (Definition definition : listener.getModel().getTypes().values()) {
            writer.createSourceFile(definition);
        }
    }

    private static void handleFile(ModelBuildingListener listener, File idlFile) throws IOException {
        if (idlFile.getName().endsWith(".html")) {
            handleIdlFragments(listener, idlFile);
        } else if (idlFile.getName().endsWith(".idl")) {
            handleIdlFile(listener, idlFile);
        }
    }

    private static void handleIdlFile(ModelBuildingListener listener, File idlFile) throws IOException {
        transpile(listener, new FileReader(idlFile));
    }

    private static void handleIdlFragments(ModelBuildingListener listener, File htmlFile) throws IOException {
        Document doc = Jsoup.parse(htmlFile, "UTF-8");
        Elements fragments = doc.select("pre.idl:not(.extract)");
        for (Element fragment : fragments) {
            String idl = fragment.text();
            transpile(listener, new StringReader(idl));
        }
    }

    public static void transpile(ModelBuildingListener listener, Reader reader) throws IOException {
        WebIDLLexer lexer = new WebIDLLexer(new ANTLRInputStream(reader));
        WebIDLParser parser = new WebIDLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        try {
            WebIDLParser.WebIDLContext webIDL = parser.webIDL();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, webIDL);
        } catch (ParseCancellationException ex) {
            ex.printStackTrace();
        }
    }

}
