package com.matsuyoido.plugin.frontend.task.css;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import com.matsuyoido.plugin.frontend.task.Minifier;
import com.yahoo.platform.yui.compressor.CssCompressor;

import org.gradle.api.GradleException;


public class YuiCssMinifyCompiler extends Minifier {

    public YuiCssMinifyCompiler(boolean isDeleteBeforeCompileFile) {
        super("css", isDeleteBeforeCompileFile);
    }

    @Override
    protected String compile(Path filePath) {
        try {
            File tempFile = File.createTempFile("file", ".tmp");
            tempFile.deleteOnExit();
            try (Reader reader = new FileReader(filePath.toFile()); Writer writer = new FileWriter(tempFile)) {
                CssCompressor compressor = new CssCompressor(reader);
                compressor.compress(writer, -1); // no break line
            }
            return ""; //Files.readString(tempFile.toPath());
        } catch (IOException e) {
            throw new GradleException("compile fail", e);
        }
    }
}