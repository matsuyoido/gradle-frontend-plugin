package com.matsuyoido.js;

import static org.assertj.core.api.Assertions.assertThat;
import java.io.File;

import com.matsuyoido.plugin.PathUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * JsMinifyCompilerTest
 */
public class JsMinifyCompilerTest {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    private static final String JS_FILE_DIR = PathUtil.classpathResourcePath("minCompile");

    @Test
    public void compile_google() {
        File jsFile = new File(JS_FILE_DIR, "test.js");

        String result = new JsMinifyCompiler(MinifyType.GOOGLE).compile(jsFile);

        assertThat(result).isEqualTo("'use strict';function Test(){this.name=0};");
    }

    @Test
    public void compile_yui() {
        File jsFile = new File(JS_FILE_DIR, "test.js");

        String result = new JsMinifyCompiler(MinifyType.YUI).compile(jsFile);

        assertThat(result).isEqualTo("function Test(){this.name=0};");
    }
}