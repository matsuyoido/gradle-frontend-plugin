package com.matsuyoido.css;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

import com.helger.commons.system.ENewLineMode;
import com.helger.css.ECSSVersion;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import com.matsuyoido.LineEnd;
import com.yahoo.platform.yui.compressor.CssCompressor;

import org.gradle.api.GradleException;

/**
 * CssMinifyCompiler
 */
public class CssMinifyCompiler {

    private final MinifyType type;
    private final ECSSVersion cssVersion = ECSSVersion.CSS30;
    private final Charset charset = StandardCharsets.UTF_8;
    private final CSSWriterSettings settings = new CSSWriterSettings(cssVersion, true);

    public CssMinifyCompiler(LineEnd lineEnd, MinifyType type) {
        this.type = type;
        this.settings.setNewLineMode(ENewLineMode.getFromTextOrNull(lineEnd.get()));
    }

    /**
     * 
     * @param cssFile CSS File
     * @return minified css
     */
    public String compile(File cssFile) {
        if (type == MinifyType.YUI) {
            try (Reader reader = new FileReader(cssFile)) {
                return convertString(reader);
            } catch (IOException e) {
                throw new GradleException("compile fail", e);
            }
        } else {
            return convertString(CSSReader.readFromFile(cssFile, charset, cssVersion));
        }
    }

    /**
     * 
     * @param cssText CSS
     * @return minified css
     */
    public String compile(String cssText) {
        if (type == MinifyType.YUI) {
            try (Reader reader = new StringReader(cssText)) {
                return convertString(reader);
            } catch (IOException e) {
                throw new GradleException("compile fail", e);
            }
        } else {
            return convertString(CSSReader.readFromString(cssText, charset, cssVersion));
        }
    }

    private String convertString(Reader reader) throws IOException {
        File tempFile = File.createTempFile("file", ".tmp");
        tempFile.deleteOnExit();
        try (Writer writer = new FileWriter(tempFile)) {
            CssCompressor compressor = new CssCompressor(reader);
            compressor.compress(writer, -1); // no break line
        }
        return Files.readAllLines(tempFile.toPath()).stream().collect(Collectors.joining()); //Files.readString(tempFile.toPath());
    }

    private String convertString(CascadingStyleSheet css) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("@charset \"%s\";", StandardCharsets.UTF_8.name()));
        css.getAllRules().forEach(rule -> builder.append(rule.getAsCSSString(settings)));
        return builder.toString();
    }

}