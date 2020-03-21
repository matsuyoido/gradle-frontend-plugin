package com.matsuyoido.css;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import com.matsuyoido.LineEnd;
import com.matsuyoido.plugin.PathUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * CssMinifyCompilerTest
 */
public class CssMinifyCompilerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String CSS_FILE_DIR = PathUtil.classpathResourcePath("minCompile");


    @Test
    public void compile_simple() {
        File cssFile = new File(CSS_FILE_DIR, "test.css");
        
        String result = new CssMinifyCompiler(LineEnd.PLATFORM, MinifyType.SIMPLE).compile(cssFile);

        assertThat(result).isEqualTo("@charset \"UTF-8\";p{font-size:1px}a{display:flex}");
    }

    @Test
    public void compile_yui() {
        File cssFile = new File(CSS_FILE_DIR, "test.css");
        
        String result = new CssMinifyCompiler(LineEnd.PLATFORM, MinifyType.YUI).compile(cssFile);

        assertThat(result).isEqualTo("p{font-size:1px}a{display:flex}");
    }
}