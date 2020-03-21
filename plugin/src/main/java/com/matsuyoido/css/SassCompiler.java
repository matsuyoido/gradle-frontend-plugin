package com.matsuyoido.css;

import java.io.File;
import java.io.IOException;

import com.matsuyoido.LineEnd;

import org.gradle.api.GradleException;

import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import io.bit3.jsass.OutputStyle;

/**
 * SassCompiler
 */
public class SassCompiler {

    private Compiler compiler;
    private Options option;

    public SassCompiler(LineEnd lineEnd) {
        this.compiler = new Compiler();
        this.option = setupOption(lineEnd);
    }

    public String compile(File cssFile) {
        try {
            Output output = this.compiler.compileFile(cssFile.toURI(),
                    File.createTempFile("temp", ".css").toURI(),
                    this.option);
            return output.getCss();
        } catch (CompilationException | IOException e) {
            throw new GradleException("compile failed", e);
        }
    }

    public String compile(String cssText) {
        try {
            Output output = this.compiler.compileString(cssText, this.option);
            return output.getCss();
        } catch (CompilationException e) {
            throw new GradleException("compile failed", e);
        }
    }

    private Options setupOption(LineEnd lineEnd) {
        Options option = new Options();
        option.setOutputStyle(OutputStyle.EXPANDED);
        // sourcemap not generate
        option.setSourceMapContents(false);
        option.setSourceMapEmbed(false);
        option.setOmitSourceMapUrl(true);
        option.setLinefeed(lineEnd.get());
        return option;
    }
}