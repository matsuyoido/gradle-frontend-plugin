package com.matsuyoido.plugin.frontend.task.css;

import java.io.File;

import com.matsuyoido.plugin.frontend.task.Minifier;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class CssMinifyTask extends DefaultTask {

    private File cssFileDirectory;
    private File cssOutputDirectory;
    private boolean isDeleteBeforeCompileFile;

    @TaskAction
    public void compileSass(IncrementalTaskInputs inputs) {
        minifier().execute(cssFileDirectory, cssOutputDirectory);
    }

    public CssMinifyTask setCssFileDirectory(File directory) {
        this.cssFileDirectory = directory;
        return this;
    }
    public CssMinifyTask setOutputFileDirectory(File directory) {
        this.cssOutputDirectory = directory;
        return this;
    }
    public CssMinifyTask setDeleteBeforeCompileFile(boolean deleteDone) {
        this.isDeleteBeforeCompileFile = deleteDone;
        return this;
    }

    public Minifier minifier() {
        return new YuiCssMinifyCompiler(isDeleteBeforeCompileFile);
    }
}