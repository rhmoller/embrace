package com.giddyplanet.embrace.webapis.internal;

import com.giddyplanet.embrace.tools.WebIdlToJava;
import com.giddyplanet.embrace.tools.javawriter.JavaWriter;
import com.giddyplanet.embrace.tools.model.webidl.Definition;
import com.giddyplanet.embrace.tools.model.webidl.SimpleTypeResolver;
import com.giddyplanet.embrace.tools.webidl2java.ModelBuildingListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public class Generator {

//        "https://dom.spec.whatwg.org/",
//        "https://html.spec.whatwg.org/"

    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        Trust.trustAllCertificates();

        ModelBuildingListener listener = new ModelBuildingListener();
        addSpec(listener, new File("data/dom.html"));
        addSpec(listener, new File("data/serialization.html"));
        addSpec(listener, new File("data/whatwg.html"));
        addSpec(listener, new File("data/cssom.html"));
        addSpec(listener, new File("data/builtin.idl"));
//        addSpec(listener, new URL("https://html.spec.whatwg.org/"));
//        addSpec(listener, new URL("https://dom.spec.whatwg.org/"));

        File srcFolder = new File("build/generated-src/java/main");
        srcFolder.mkdirs();

        JavaWriter writer = new JavaWriter(srcFolder, "com.giddyplanet.embrace.webapis", new SimpleTypeResolver(listener.getModel()));
        for (Definition definition : listener.getModel().getTypes().values()) {
            writer.createSourceFile(definition);
        }
    }

    private static void addSpec(ModelBuildingListener listener, URL url) throws IOException {
        Document doc = Jsoup.parse(url, 300000);
        Elements fragments = doc.select("pre.idl,pre.extraidl");
        for (Element fragment : fragments) {
            if (fragment.hasClass("extract")) continue;
            String idl = fragment.text();
            StringReader reader = new StringReader(idl);
            WebIdlToJava.transpile(listener, reader);
        }
    }

    private static void addSpec(ModelBuildingListener listener, File file) throws IOException {
        if (file.getName().endsWith(".idl")) {
            String idl = new String(Files.readAllBytes(file.toPath()), "UTF-8");
            StringReader reader = new StringReader(idl);
            WebIdlToJava.transpile(listener, reader);
        } else {
            Document doc = Jsoup.parse(file, "UTF-8");
            Elements fragments = doc.select("pre.idl,pre.extraidl");
            for (Element fragment : fragments) {
                if (fragment.hasClass("extract")) continue;
                String idl = fragment.text();
                StringReader reader = new StringReader(idl);
                WebIdlToJava.transpile(listener, reader);
            }
        }
    }

}
