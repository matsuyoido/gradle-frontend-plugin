package com.matsuyoido.plugin.frontend.task.js;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.matsuyoido.plugin.PathUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class YuiJsMinifyCompilerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String JS_FILE_DIR = PathUtil.classpathResourcePath("minCompile");
    private static final int JS_FILE_COUNT = 2;

    private YuiJsMinifyCompiler compiler = new YuiJsMinifyCompiler(false);


    @Test
    public void compile() {
        Path jsFilePath = Path.of(JS_FILE_DIR, "test.js");

        String result = compiler.compile(jsFilePath);

        assertThat(result).isEqualTo("function Test(){this.name=0};");
    }

    @Test
    public void execute() throws IOException {
        String jsDirectory = JS_FILE_DIR;
        File outputDirectory = tempFolder.newFolder("compiledFolder");
        
        compiler.execute(new File(jsDirectory), outputDirectory);

        assertThat(outputDirectory.listFiles()).allSatisfy(file -> {
            if (file.getName().equals("nest")) {
                assertThat(file.listFiles()).allSatisfy(nestFile -> {
                    assertThat(nestFile).isFile()
                                        .satisfies(f -> assertThat(f.getName()).endsWith(".min.js"))
                                        .hasContent("(function(){console.log(\"hello world\")})();");
                }).hasSize(1);
            } else {
                assertThat(file).isFile()
                                .satisfies(f -> assertThat(f.getName()).endsWith(".min.js"))
                                .hasContent("function Test(){this.name=0};");
            }
        }).hasSize(JS_FILE_COUNT);
    }
}