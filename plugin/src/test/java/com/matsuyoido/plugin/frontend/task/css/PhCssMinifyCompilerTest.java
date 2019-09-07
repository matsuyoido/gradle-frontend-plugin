package com.matsuyoido.plugin.frontend.task.css;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.matsuyoido.caniuse.CanIUse;
import com.matsuyoido.plugin.PathUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * PhCssMinifyCompilerTest
 */
public class PhCssMinifyCompilerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String CSS_FILE_DIR = PathUtil.classpathResourcePath("minCompile");
    private static final int CSS_FILE_COUNT = 2;

    private PhCssMinifyCompiler compiler = new PhCssMinifyCompiler(false);
    
    @Test
    public void compile() {
        Path cssFilePath = Path.of(CSS_FILE_DIR, "test.css");
        
        String result = compiler.compile(cssFilePath);

        assertThat(result).isEqualTo("@charset \"UTF-8\";p{font-size:1px}a{display:flex}");
    }

    @Test
    public void compile_withPrefixer() throws IOException {
        Path cssFilePath = Path.of(CSS_FILE_DIR, "test.css");
        CanIUse canIUse = new CanIUse(new File(PathUtil.classpathResourcePath("caniuse/data.json")));
        compiler.setPrefixer(canIUse.getCssSupports(), x -> true);

        String result = compiler.compile(cssFilePath);
        assertThat(result).isEqualTo("@charset \"UTF-8\";p{font-size:1px}a{display:flex;display:-ms-flex;display:-moz-flex;display:-webkit-flex}");
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
                                .hasContent("p{font-size:1px}a{color:red}");
            }
        }).hasSize(CSS_FILE_COUNT);
    }

}