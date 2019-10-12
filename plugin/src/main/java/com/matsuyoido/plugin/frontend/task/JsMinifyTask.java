package com.matsuyoido.plugin.frontend.task;

import java.io.File;
// import java.util.Objects;

import com.matsuyoido.plugin.frontend.task.Minifier;
import com.matsuyoido.plugin.frontend.task.MinifierType;
import com.matsuyoido.plugin.frontend.task.js.GoogleClosureMinifyCompiler;
import com.matsuyoido.plugin.frontend.task.js.YuiJsMinifyCompiler;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class JsMinifyTask extends DefaultTask {

    private File jsFileDirectory;
    private File jsOutputDirectory;
    private MinifierType minifierType;

    @TaskAction
    public void compileJs(IncrementalTaskInputs inputs) {
        jsOutputDirectory.mkdirs();
        minifier().execute(jsFileDirectory, jsOutputDirectory);
    }

    public JsMinifyTask setJsFileDirectory(File directory) {
        this.jsFileDirectory = directory;
        return this;
    }
    public JsMinifyTask setOutputFileDirectory(File directory) {
        this.jsOutputDirectory = directory;
        return this;
    }
    public JsMinifyTask setMinifierType(MinifierType type) {
        this.minifierType = type;
        return this;
    }


    public Minifier minifier() {
        boolean isDeleteBeforeCompileFile = false;
        if (minifierType == null) {
            return new GoogleClosureMinifyCompiler(isDeleteBeforeCompileFile);
        }
        switch (minifierType) {
            case YUI:
                return new YuiJsMinifyCompiler(isDeleteBeforeCompileFile);
            case GOOGLE_CLOSURE:
                return new GoogleClosureMinifyCompiler(isDeleteBeforeCompileFile);
            default:
                return new GoogleClosureMinifyCompiler(isDeleteBeforeCompileFile);
        }
    }
}