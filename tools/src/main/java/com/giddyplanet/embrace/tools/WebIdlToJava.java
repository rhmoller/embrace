package com.giddyplanet.embrace.tools;

import com.lexicalscope.jewel.cli.CliFactory;
import com.lexicalscope.jewel.cli.Option;

import java.io.File;
import java.util.List;

public class WebIdlToJava {

    public interface Options {
        @Option(description = "HTML file containing WebIDL fragments", defaultToNull = true)
        List<File> getIdlFiles();

        @Option(description = "Folder containing the .idl files to convert")
        File getIdlFolder();

        @Option(description = "Root folder for generated sources",
                defaultToNull = true)
        File getOutDir();

        @Option(description = "Java package for generated sources", defaultToNull = true)
        String getPackage();
    }

    public static void main(String[] args) {
        Options options = CliFactory.parseArguments(Options.class, args);


    }
}
