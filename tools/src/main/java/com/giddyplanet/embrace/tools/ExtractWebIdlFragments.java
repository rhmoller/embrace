package com.giddyplanet.embrace.tools;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;

/**
 * Takes a HTML file as input and outputs a file containing all detected WebIDL fragments.
 */
public class ExtractWebIdlFragments {

    public interface Options {
        @Option(description = "HTML file containing WebIDL fragments")
        File getIn();

        @Option(description = "Write WebIDL fragments to this file. If not specified output will be printed to console",
                defaultToNull = true)
        File getOut();
    }

    public static void main(String[] args) throws IOException {
        Options options = CliFactory.parseArguments(Options.class, args);

        File inFile = options.getIn();
        File outFile = options.getOut();

        if (!inFile.exists() || !inFile.isFile()) {
            System.err.println(inFile + " does not exist or is not a file");
            System.exit(1);
        }

        Writer writer;
        if (outFile != null) {
            writer = new FileWriter(outFile);
        } else {
            writer = new OutputStreamWriter(System.out);
        }

        Document doc = Jsoup.parse(inFile, "UTF-8");
        Elements fragments = doc.select("pre.idl:not(.extract)");
        for (Element fragment : fragments) {
            if (!fragment.hasClass("idl")) continue;;
            if (fragment.hasClass("extract")) continue;
            String idl = fragment.text();
            writer.append(idl);
            writer.append("\n");
        }

        writer.close();
    }

}
