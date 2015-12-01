package com.giddyplanet.embrace.tools.webidl2java;

import com.giddyplanet.embrace.tools.javawriter.JavaWriter;
import com.giddyplanet.embrace.tools.model.webidl.Definition;
import com.giddyplanet.embrace.tools.model.webidl.Model;
import com.giddyplanet.embrace.webidl.parser.WebIDLLexer;
import com.giddyplanet.embrace.webidl.parser.WebIDLParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BailErrorStrategy;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;

/**
 * todo: union
 * todo: typedef
 * todo: dictionary
 * todo: promise
 * todo: sequence
 * todo: const
 */
public class Generator {

    public static void main(String[] args) throws IOException {
        ModelBuildingListener listener = new ModelBuildingListener();

        handleIdlFragments(listener, "src/main/html/dom.html");
        handleIdlFragments(listener, "src/main/html/whatwg.html");

        Model model = listener.getModel();
        JavaWriter writer = new JavaWriter(new File("generated/src/java"), "com.giddyplanet.js.client");
        for (Definition type : model.getTypes().values()) {
            writer.createSourceFile(type);
        }

    }

    private static void handleIdlFragments(ModelBuildingListener listener, String idlFileName) throws IOException {
        Document doc = Jsoup.parse(new File(idlFileName), "UTF-8");
        Elements fragments = doc.select("pre.idl:not(.extract)");
        for (Element fragment : fragments) {
            if (!fragment.hasClass("idl")) continue;;
            if (fragment.hasClass("extract")) continue;
            String idl = fragment.text();
            System.out.println();
            System.out.printf(idl);
            System.out.println();
            transpile(idl, listener);
        }
    }

    public static void transpile(String idl, ModelBuildingListener listener) throws IOException {
        WebIDLLexer lexer = new WebIDLLexer(new ANTLRInputStream(new StringReader(idl)));
        WebIDLParser parser = new WebIDLParser(new CommonTokenStream(lexer));
        parser.setErrorHandler(new BailErrorStrategy());

        try {
            WebIDLParser.WebIDLContext webIDL = parser.webIDL();
            ParseTreeWalker walker = new ParseTreeWalker();
            walker.walk(listener, webIDL);
        } catch (ParseCancellationException ex) {
            System.out.println(idl);
        }
    }

}
