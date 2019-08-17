package com.matsuyoido.plugin.frontend.task.js;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
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
import com.matsuyoido.plugin.frontend.task.Minifier;

import org.gradle.api.GradleException;

/**
 * JsMinifyCompiler
 */
public class GoogleClosureMinifyCompiler extends Minifier {

    public GoogleClosureMinifyCompiler(boolean isDeleteBeforeCompileFile) {
        super("js", isDeleteBeforeCompileFile);
    }

    
    @Override
    protected String compile(Path filePath) {
        try {
            CompilerOptions option = new CompilerOptions();
            List<SourceFile> externs = CommandLineRunner.getBuiltinExterns(Environment.BROWSER);
            Compiler compiler = new Compiler();

            List<SourceFile> inputs = Collections.singletonList(SourceFile.fromPath(filePath, StandardCharsets.UTF_8));
            CompilationLevel compileLevel = CompilationLevel.SIMPLE_OPTIMIZATIONS;
            WarningLevel warnLevel = WarningLevel.QUIET;

            compileLevel.setOptionsForCompilationLevel(option);
            warnLevel.setOptionsForWarningLevel(option);

            compiler.compile(externs, inputs, option);
            if (compiler.hasErrors()) {
                String errMsg = compiler.getErrorManager().getErrors().stream().map(error -> 
                    error.sourceName + " : " + String.valueOf(error.getLineNumber()) + " - " + error.description
                ).collect(Collectors.joining(System.lineSeparator()));
                throw new GradleException(errMsg);
            }
            return compiler.toSource();
        } catch (IOException e) {
            throw new GradleException(e.getMessage(), e);
        }
	}

    
}