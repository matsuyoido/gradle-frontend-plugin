package com.matsuyoido.plugin.frontend.task.css;

import java.io.File;
import java.util.List;

import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.plugin.frontend.task.Minifier;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class CssMinifyTask extends DefaultTask {

    private File cssFileDirectory;
    private File cssOutputDirectory;
    private boolean isDeleteBeforeCompileFile;
    private List<SupportData> supportData;

    @TaskAction
    public void minifyCss(IncrementalTaskInputs inputs) {
        cssOutputDirectory.mkdirs();
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
    public CssMinifyTask setPrefixer(List<SupportData> supportData) {
        this.supportData = supportData;
        return this;
    }

    public Minifier minifier() {
        if (this.supportData != null) {
            return new PhCssMinifyCompiler(this.supportData, isDeleteBeforeCompileFile);
        } else {
            return new YuiCssMinifyCompiler(isDeleteBeforeCompileFile);
        }
    }
}