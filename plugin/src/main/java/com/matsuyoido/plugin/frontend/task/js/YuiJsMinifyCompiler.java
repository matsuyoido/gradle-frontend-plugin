package com.matsuyoido.plugin.frontend.task.js;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import com.matsuyoido.plugin.frontend.task.Minifier;
import com.yahoo.platform.yui.compressor.JavaScriptCompressor;

import org.gradle.api.GradleException;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class YuiJsMinifyCompiler extends Minifier {

    public YuiJsMinifyCompiler(boolean isDeleteBeforeCompileFile) {
        super("js", isDeleteBeforeCompileFile);
    }

    @Override
    protected String compile(Path filePath) {
        try {
            boolean munge = false;
            boolean verbose = false;
            boolean preserveAllSemiColons = false;
            boolean disableOptimizations = false;
            File tempFile = File.createTempFile("file", ".tmp");
            tempFile.deleteOnExit();
            try (Reader reader = new FileReader(filePath.toFile()); Writer writer = new FileWriter(tempFile)) {
                JavaScriptCompressor compressor = new JavaScriptCompressor(reader, new ErrorHandle());
                compressor.compress(writer, -1, munge, verbose, preserveAllSemiColons, disableOptimizations); // no break line
            } catch (IndexOutOfBoundsException e) {
                // file is empty
            }
            return Files.readAllLines(tempFile.toPath()).stream().collect(Collectors.joining()); // Files.readString(tempFile.toPath());
        } catch (IOException e) {
            throw new GradleException("compile fail", e);
        }
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