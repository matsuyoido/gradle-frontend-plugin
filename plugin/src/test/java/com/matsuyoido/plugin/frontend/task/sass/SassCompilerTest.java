package com.matsuyoido.plugin.frontend.task.sass;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import com.matsuyoido.plugin.PathUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * SassCompileTaskTest
 */
public class SassCompilerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String SASS_FILE_DIR = PathUtil.classpathResourcePath("sassCompile");
    private static final int SASS_FILE_COUNT = 2;
    private SassCompiler compiler = new SassCompiler();

    @Test
    public void compile() {
        Path sassFilePath = Path.of(SASS_FILE_DIR, "test.scss");

        String result = compiler.compile(sassFilePath);

        assertThat(result).contains("font-size: 1px;", "@media screen and (min-width: 1000px)");
    }
    
    @Test
    public void execute() throws IOException {
        String scssDirectory = SASS_FILE_DIR;
        File outputDirectory = tempFolder.newFolder("compiledFolder");
        
        compiler.execute(new File(scssDirectory), outputDirectory);

        assertThat(outputDirectory.listFiles()).allSatisfy(file -> {
            if (file.getName().equals("nest")) {
                assertThat(file.listFiles()).allSatisfy(nestFile -> {
                    assertThat(nestFile).isFile()
                                        .hasExtension("css");
                    assertContains(nestFile, "div p {");
                }).hasSize(1);
            } else {
                assertThat(file).isFile()
                                .hasExtension("css");
                assertContains(file, "font-size: 1px;", "@media screen and (min-width: 1000px)");
            }
        }).hasSize(SASS_FILE_COUNT);
    }


    private void assertContains(File file, String... expected) {
        try {
            StringBuilder sb = new StringBuilder();
            try (FileReader reader = new FileReader(file);
                    BufferedReader breader = new BufferedReader(reader)) {
                boolean readable = true;
                do {
                    String line = breader.readLine();
                    if (line == null) {
                        readable = false;
                    } else {
                        sb.append(line);
                    }
                } while(readable);
            }
            for (String expect : expected) {
                assertThat(sb.indexOf(expect)).isGreaterThan(-1);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}