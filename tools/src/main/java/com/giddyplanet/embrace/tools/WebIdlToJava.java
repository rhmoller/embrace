package com.giddyplanet.embrace.tools;

import com.giddyplanet.embrace.tools.javawriter.JavaWriter;
import com.giddyplanet.embrace.tools.model.webidl.Definition;
import com.giddyplanet.embrace.tools.model.webidl.SimpleTypeResolver;
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
                transpileFile(listener, idlFile, FileType.AUTO);
            }
        }

        if (idlFolder != null) {
            File[] files = idlFolder.listFiles();
            for (File file : files) {
                transpileFile(listener, file, FileType.AUTO);
            }
        }

        JavaWriter writer = new JavaWriter(outDir, javaPackage, new SimpleTypeResolver(listener.getModel()));
        for (Definition definition : listener.getModel().getTypes().values()) {
            writer.createSourceFile(definition);
        }
    }

    public static void transpileFile(ModelBuildingListener listener, File idlFile, FileType type) throws IOException {
        if ((type == FileType.AUTO && idlFile.getName().endsWith(".html")) || type == FileType.HTML) {
            handleIdlFragments(listener, idlFile);
        } else if ((type == FileType.AUTO && idlFile.getName().endsWith(".idl")) || type == FileType.WEBIDL) {
            handleIdlFile(listener, idlFile);
        }
    }

    private static void handleIdlFile(ModelBuildingListener listener, File idlFile) throws IOException {
        transpile(listener, new FileReader(idlFile));
    }

    public static void handleIdlFragments(ModelBuildingListener listener, File htmlFile) throws IOException {
        Document doc = Jsoup.parse(htmlFile, "UTF-8");
        Elements fragments = doc.select("pre.idl:not(.extract),pre.extraidl");
        for (Element fragment : fragments) {
            String idl = fragment.text();
            StringReader reader = new StringReader(idl);
            transpile(listener, reader);
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
