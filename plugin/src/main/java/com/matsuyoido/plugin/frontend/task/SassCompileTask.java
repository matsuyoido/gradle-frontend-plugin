package com.matsuyoido.plugin.frontend.task;

import java.io.File;

import com.matsuyoido.plugin.LineEnd;
import com.matsuyoido.plugin.frontend.task.sass.SassCompiler;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class SassCompileTask extends DefaultTask {
    private File sassFileDirectory;
    private File cssOutputDirectory;
    private LineEnd lineEnd;

    @TaskAction
    public void compileSass(IncrementalTaskInputs inputs) {
        compiler().execute(sassFileDirectory, cssOutputDirectory);
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

    public SassCompiler compiler() {
        return new SassCompiler(lineEnd);
    }

}