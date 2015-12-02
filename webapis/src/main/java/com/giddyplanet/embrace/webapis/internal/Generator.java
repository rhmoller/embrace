package com.giddyplanet.embrace.webapis.internal;

import com.giddyplanet.embrace.tools.WebIdlToJava;
import com.giddyplanet.embrace.tools.javawriter.JavaWriter;
import com.giddyplanet.embrace.tools.model.webidl.Definition;
import com.giddyplanet.embrace.tools.webidl2java.ModelBuildingListener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.net.ssl.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class Generator {

//        "https://dom.spec.whatwg.org/",
//        "https://html.spec.whatwg.org/"

    public static void main(String[] args) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        trustAllCerts();

        ModelBuildingListener listener = new ModelBuildingListener();
        addSpec(listener, new URL("https://dom.spec.whatwg.org/"));
        addSpec(listener, new URL("https://html.spec.whatwg.org/"));

        File srcFolder = new File("build/generated-src/java/main");
        srcFolder.mkdirs();

        JavaWriter writer = new JavaWriter(srcFolder, "com.giddyplanet.embrace.webapis");
        for (Definition definition : listener.getModel().getTypes().values()) {
            writer.createSourceFile(definition);
        }
    }

    private static void addSpec(ModelBuildingListener listener, URL url) throws IOException {
        Document doc = Jsoup.parse(url, 1000);
        Elements fragments = doc.select("pre.idl:not(.extract)");
        for (Element fragment : fragments) {
            String idl = fragment.text();
            StringReader reader = new StringReader(idl);
            WebIdlToJava.transpile(listener, reader);
        }
    }

    private static void trustAllCerts() throws NoSuchAlgorithmException, KeyManagementException {
        TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {  }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
                }
        };

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

}
