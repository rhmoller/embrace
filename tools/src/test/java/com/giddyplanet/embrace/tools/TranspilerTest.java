package com.giddyplanet.embrace.tools;

import com.giddyplanet.embrace.tools.javawriter.JavaWriter;
import com.giddyplanet.embrace.tools.model.webidl.Definition;
import com.giddyplanet.embrace.tools.model.webidl.Interface;
import com.giddyplanet.embrace.tools.webidl2java.ModelBuildingListener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TranspilerTest {

    @Parameters(name = "{0}")
    public static Collection<Object[]> collectFiles() throws IOException {
        Path resourcePath = Paths.get("src", "test", "resources");
        return Files
                .list(resourcePath).filter(path -> path.toString().endsWith(".idl"))
                .map(path -> new Object[]{ path.getFileName().toString(), path })
                .collect(Collectors.toList());
    }

    @Parameter
    public String name;

    @Parameter(value = 1)
    public Path idlPath;

    @Test
    public void transpile() throws IOException {
        Path javaPath = getJavaPath(idlPath);
        String expectedJava = new String(Files.readAllBytes(javaPath), "UTF-8");
        ModelBuildingListener listener = new ModelBuildingListener();
        WebIdlToJava.transpile(listener, new FileReader(idlPath.toFile()));

        JavaWriter writer = new JavaWriter(null, null);
        StringBuilder sb = new StringBuilder();
        for (Definition type : listener.getModel().getTypes().values()) {
            sb.append(writer.createSource((Interface) type));
        }
        String actualJava = sb.toString();
        assertEquals(expectedJava, actualJava);
    }

    private Path getJavaPath(Path path) {
        Path folder = path.getParent();
        String fileName = this.idlPath.getFileName().toString();
        String base = fileName.substring(0, fileName.indexOf('.'));
        return folder.resolve(base + ".java");
    }

}
