package com.matsuyoido.plugin.frontend.task.css;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.caniuse.SupportStatus;
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
    
    @Test
    public void compile() {
        Path cssFilePath = new File(CSS_FILE_DIR, "test.css").toPath();//Path.of(CSS_FILE_DIR, "test.css");
        
        String result = new PhCssMinifyCompiler(false).compile(cssFilePath);

        assertThat(result).isEqualTo("@charset \"UTF-8\";p{font-size:1px}a{display:flex}");
    }

    @Test
    public void compile_withPrefixer() throws IOException {
        Path cssFilePath = new File(CSS_FILE_DIR, "test.css").toPath();//Path.of(CSS_FILE_DIR, "test.css");
        List<SupportData> supports = new ArrayList<>();
        supports.add(flexSupport());
        
        String result = new PhCssMinifyCompiler(supports, false).compile(cssFilePath);
        assertThat(result).isEqualTo("@charset \"UTF-8\";p{font-size:1px}a{display:flex;display:-webkit-flex}");
    }

    @Test
    public void execute() throws IOException {
        String cssDirectory = CSS_FILE_DIR;
        File outputDirectory = tempFolder.newFolder("compiledFolder");
        
        new PhCssMinifyCompiler(false).execute(new File(cssDirectory), outputDirectory);

        assertThat(outputDirectory.listFiles()).allSatisfy(file -> {
            if (file.getName().equals("nest")) {
                assertThat(file.listFiles()).allSatisfy(nestFile -> {
                    assertThat(nestFile).isFile()
                                        .satisfies(f -> assertThat(f.getName()).endsWith(".min.css"))
                                        .as("nest file content check")
                                        .hasContent("@charset \"UTF-8\";p{font-size:1rem;color:red}");
                }).hasSize(1);
            } else {
                assertThat(file).isFile()
                                .satisfies(f -> assertThat(f.getName()).endsWith(".min.css"))
                                .as("flat file content check")
                                .hasContent("@charset \"UTF-8\";p{font-size:1px}a{display:flex}");
            }
        }).hasSize(CSS_FILE_COUNT);
    }


    private SupportData flexSupport() {
        SupportStatus chromeStatus = new SupportStatus();
        chromeStatus.setBrowser("chrome", "webkit");

        List<SupportStatus> supports = new ArrayList<>();
        supports.add(chromeStatus);

        SupportData support = new SupportData();
        support.setKey("flexbox");
        support.setSupports(supports);
        return support;
    }
}