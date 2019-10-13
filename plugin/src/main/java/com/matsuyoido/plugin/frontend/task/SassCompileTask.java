package com.matsuyoido.plugin.frontend.task;

import java.io.File;
import java.nio.file.Path;

import com.matsuyoido.plugin.LineEnd;
import com.matsuyoido.plugin.frontend.css.SassCompiler;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class SassCompileTask extends DefaultTask {
    private File sassFileDirectory;
    private File cssOutputDirectory;
    private LineEnd lineEnd;

    @TaskAction
    public void compileSass(IncrementalTaskInputs inputs) {
        SassCompiler compiler = new SassCompiler(lineEnd);
        new Compiler("css", "glob:[!_]*.scss"){
            @Override
            protected String compile(Path filePath) {
                return compiler.compile(filePath.toFile());
            }
        }.execute(sassFileDirectory, cssOutputDirectory);
    }

    public SassCompileTask setSassFileDirectory(File directory) {
        this.sassFileDirectory = directory;
        return this;
    }
    public SassCompileTask setOutputFileDirectory(File directory) {
        this.cssOutputDirectory = directory;
        return this;
    }
    public SassCompileTask setLineEnd(LineEnd lineEnd) {
        this.lineEnd = lineEnd;
        return this;
    }

}