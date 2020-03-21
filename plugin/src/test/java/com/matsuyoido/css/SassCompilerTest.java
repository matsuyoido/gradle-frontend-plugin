package com.matsuyoido.css;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;

import com.matsuyoido.LineEnd;
import com.matsuyoido.plugin.PathUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * SassCompilerTest
 */
public class SassCompilerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String SASS_FILE_DIR = PathUtil.classpathResourcePath("sassCompile");

    @Test
    public void compile_file() {
        File sassFile = new File(SASS_FILE_DIR, "test.scss");

        String result = new SassCompiler(LineEnd.PLATFORM).compile(sassFile);

        assertThat(result).contains("font-size: 1px;", "@media screen and (min-width: 1000px)");
    }

    @Test
    public void compile_string() {
        String css = String.join(System.lineSeparator(),
        ".test {",
        "    font-size: 1px;",
        "    p {",
        "        color: red;",
        "    }",
        "}"
        );
        String result = new SassCompiler(LineEnd.PLATFORM).compile(css);

        assertThat(result).contains(".test p {");
    }
}