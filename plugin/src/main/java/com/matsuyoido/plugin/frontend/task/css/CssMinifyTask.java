package com.matsuyoido.plugin.frontend.task.css;

import java.io.File;
import java.util.List;
import java.util.function.Predicate;

import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.caniuse.SupportStatus;
import com.matsuyoido.plugin.frontend.task.Minifier;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class CssMinifyTask extends DefaultTask {

    private File cssFileDirectory;
    private File cssOutputDirectory;
    private boolean isDeleteBeforeCompileFile;
    private List<SupportData> supportData;
    private Predicate<SupportStatus> supportFilter;

    @TaskAction
    public void minifyCss(IncrementalTaskInputs inputs) {
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
    public CssMinifyTask setPrefixer(List<SupportData> supportData, Predicate<SupportStatus> supportFilter) {
        this.supportData = supportData;
        this.supportFilter = supportFilter;
        return this;
    }

    public Minifier minifier() {
        PhCssMinifyCompiler compiler = new PhCssMinifyCompiler(isDeleteBeforeCompileFile);
        if (this.supportData != null) {
            compiler.setPrefixer(this.supportData, this.supportFilter);
        }
        return compiler;
    }
}