package com.matsuyoido.plugin.frontend.task.css;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.matsuyoido.plugin.PathUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * YuiCssMinifyCompilerTest
 */
public class YuiCssMinifyCompilerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String CSS_FILE_DIR = PathUtil.classpathResourcePath("minCompile");
    private static final int CSS_FILE_COUNT = 2;

    private YuiCssMinifyCompiler compiler = new YuiCssMinifyCompiler(false);
    
    @Test
    public void compile() {
        Path cssFilePath = new File(CSS_FILE_DIR, "test.css").toPath();//Path.of(CSS_FILE_DIR, "test.css");
        
        String result = compiler.compile(cssFilePath);

        assertThat(result).isEqualTo("p{font-size:1px}a{display:flex}");
    }

    @Test
    public void execute() throws IOException {
        String cssDirectory = CSS_FILE_DIR;
        File outputDirectory = tempFolder.newFolder("compiledFolder");
        
        compiler.execute(new File(cssDirectory), outputDirectory);

        assertThat(outputDirectory.listFiles()).allSatisfy(file -> {
            if (file.getName().equals("nest")) {
                assertThat(file.listFiles()).allSatisfy(nestFile -> {
                    assertThat(nestFile).isFile()
                                        .satisfies(f -> assertThat(f.getName()).endsWith(".min.css"))
                                        .hasContent("p{font-size:1rem;color:red}");
                }).hasSize(1);
            } else {
                assertThat(file).isFile()
                                .satisfies(f -> assertThat(f.getName()).endsWith(".min.css"))
                                .hasContent("p{font-size:1px}a{display:flex}");
            }
        }).hasSize(CSS_FILE_COUNT);
    }

}