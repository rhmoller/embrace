package com.giddyplanet.embrace.webapis.internal;

import com.giddyplanet.embrace.tools.WebIdlToJava;
import com.giddyplanet.embrace.tools.javawriter.JavaWriter;
import com.giddyplanet.embrace.tools.model.java.JClass;
import com.giddyplanet.embrace.tools.model.java.JavaModel;
import com.giddyplanet.embrace.tools.model.webidl.Definition;
import com.giddyplanet.embrace.tools.model.webidl.Model;
import com.giddyplanet.embrace.tools.model.webidl.SimpleTypeResolver;
import com.giddyplanet.embrace.tools.webidl2java.ModelBuildingListener;
import com.giddyplanet.embrace.tools.webidl2java.ModelConverter;
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

    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        Trust.trustAllCertificates();

        ModelBuildingListener listener = new ModelBuildingListener();
        File folder = new File("data");
        File[] files = folder.listFiles();
        for (File file : files) {
            addSpec(listener, file);
        }

        File srcFolder = new File("build/generated-src/java/main");
        srcFolder.mkdirs();

        Model model = listener.getModel();
        SimpleTypeResolver resolver = new SimpleTypeResolver(model);
        ModelConverter converter = new ModelConverter(model, resolver);
        JavaModel javaModel = converter.bind();

        JavaWriter writer = new JavaWriter(srcFolder, "com.giddyplanet.embrace.webapis", resolver);
        for (JClass definition : javaModel.getTypes().values()) {
            writer.createSourceFile(definition);
        }
    }

    private static void addSpec(ModelBuildingListener listener, File file) throws IOException {
        if (file.getName().endsWith(".idl")) {
            String idl = new String(Files.readAllBytes(file.toPath()), "UTF-8");
            handleIdl(listener, idl);
        } else if (file.getName().endsWith(".html")) {
            Document doc = Jsoup.parse(file, "UTF-8");
            Elements fragments = doc.select("pre.idl,pre.extraidl");
            for (Element fragment : fragments) {
                if (fragment.hasClass("extract")) continue;
                String idl = fragment.text();
                handleIdl(listener, idl);
            }
        }
    }

    private static void handleIdl(ModelBuildingListener listener, String idl) throws IOException {
        StringReader reader = new StringReader(idl);
        WebIdlToJava.transpile(listener, reader);
    }

}
