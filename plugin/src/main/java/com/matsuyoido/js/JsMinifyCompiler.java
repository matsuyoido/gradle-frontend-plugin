package com.matsuyoido.js;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.javascript.jscomp.CommandLineRunner;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.WarningLevel;
import com.google.javascript.jscomp.CompilerOptions.Environment;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.gradle.api.GradleException;

/**
 * JsMinifyCompiler
 */
public class JsMinifyCompiler {

    private Charset charset = StandardCharsets.UTF_8;
    private MinifyType type;

    public JsMinifyCompiler(MinifyType type) {
        this.type = type;
    }

    /**
     * 
     * @param jsFile JavaScript file
     * @return minified javascript
     */
    public String compile(File jsFile) {
        switch (type) {
            case GOOGLE:
                return convertString(Collections.singletonList(SourceFile.fromPath(jsFile.toPath(), charset)));
            case YUI:
                try (Reader reader = new FileReader(jsFile)) {
                    return convertString(reader);
                } catch (IOException e) {
                    throw new GradleException(e.getMessage(), e);
                }
            default:
                throw new GradleException();
        }
    }

    /**
     * 
     * @param jsFileList JavaScript file
     * @return minified and merged javascript
     */
    public String compile(List<File> jsFileList) {
        return convertString(jsFileList.stream()
                                       .map(file -> SourceFile.fromPath(file.toPath(), charset))
                                       .collect(Collectors.toList()));
    }

    private String convertString(List<SourceFile> inputs) {
        try {
            CompilerOptions option = new CompilerOptions();
            List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(Environment.BROWSER);
            Compiler compiler = new Compiler();
            CompilationLevel compileLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
            WarningLevel warnLevel = WarningLevel.QUIET;

            compileLevel.setOptionsForCompilationLevel(option);
            warnLevel.setOptionsForWarningLevel(option);

            compiler.compile(externs, inputs, option);
            if (compiler.hasErrors()) {
                String errMsg = compiler
                        .getErrorManager().getErrors().stream().map(error -> error.getSourceName() + " : "
                                + String.valueOf(error.getLineNumber()) + " - " + error.getDescription())
                        .collect(Collectors.joining(System.lineSeparator()));
                throw new GradleException(errMsg);
            }
            return compiler.toSource();
        } catch (IOException e) {
            throw new GradleException(e.getMessage(), e);
        }
    }

    private String convertString(Reader reader) throws IOException {
        boolean munge = false;
        boolean verbose = false;
        boolean preserveAllSemiColons = false;
        boolean disableOptimizations = false;
        File tempFile = File.createTempFile("file", ".tmp");
        tempFile.deleteOnExit();
        try (Writer writer = new FileWriter(tempFile)) {
            JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new ErrorHandle());
            compressor.compress(writer, -1, munge, verbose, preserveAllSemiColons, disableOptimizations); // no break line
        } catch (IndexOutOfBoundsException e) {
            // file is empty
        }
        return Files.readAllLines(tempFile.toPath()).stream().collect(Collectors.joining()); // Files.readString(tempFile.toPath());
    }

    private class ErrorHandle implements ErrorReporter {

        @Override
        public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {

        }

        @Override
        public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
            return new EvaluatorException(message);
        }

        @Override
        public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {

        }
    }
}