package com.matsuyoido.plugin.frontend.task.sass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.gradle.api.GradleException;

import io.bit3.jsass.CompilationException;
import io.bit3.jsass.Compiler;
import io.bit3.jsass.Options;
import io.bit3.jsass.Output;
import io.bit3.jsass.OutputStyle;

public class SassCompiler extends com.matsuyoido.plugin.frontend.task.Compiler {

    private Compiler compiler;
    private Options option;

    public SassCompiler() {
        super(".css", "glob:[!_]*.scss");
        this.compiler = new Compiler();
        this.option = setupOption();
    }


    @Override
    protected String compile(Path filePath) {
        try {
            Output output = this.compiler.compileFile(filePath.toUri(), File.createTempFile("temp", ".css").toURI(),
                    this.option);
            return output.getCss();
        } catch (CompilationException | IOException e) {
            throw new GradleException("compile failed", e);
        }
    }

    private Options setupOption() {
        Options option = new Options();
        option.setOutputStyle(OutputStyle.EXPANDED);
        // sourcemap not generate
        option.setSourceMapContents(false);
        option.setSourceMapEmbed(false);
        option.setOmitSourceMapUrl(true);
        return option;
    }



    // protected void sassCompile2(Set<Path> targetPaths) {
    //     targetPaths.forEach(path -> {
    //         try {
    //             var sass = ScssStylesheet.get(path.toAbsolutePath().toString());
    //             sass.setFile(path.toFile());
    //             sass.setCharset(StandardCharsets.UTF_8.name());
    //             sass.compile();
    //             Path outputPath = convertOutputPath(path);
    //             outputPath.getParent().toFile().mkdirs();
    //             File outputFile = outputPath.toFile();
                
    //             try (FileOutputStream fos = new FileOutputStream(outputFile);
    //             OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
    //                 sass.write(writer);
    //             }
    //         } catch (CSSException | IOException e) {
    //             throw new GradleException("compile failed", e);
	// 		} catch (Exception e) {
    //             throw new GradleException("compile failed", e);
    //         }
    //     });
    // }

}