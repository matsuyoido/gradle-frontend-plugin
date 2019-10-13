package com.matsuyoido.plugin.frontend.task;

import java.io.File;
import java.nio.file.Path;

import com.matsuyoido.plugin.frontend.extension.MinifierType;
import com.matsuyoido.plugin.frontend.js.JsMinifyCompiler;
import com.matsuyoido.plugin.frontend.js.MinifyType;
import com.matsuyoido.plugin.frontend.task.Minifier;

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
        JsMinifyCompiler compiler = new JsMinifyCompiler(minifierType == MinifierType.YUI ? MinifyType.YUI : MinifyType.GOOGLE);
        new Minifier("js", false){
            @Override
            protected String compile(Path filePath) {
                return compiler.compile(filePath.toFile());
            }
        }.execute(jsFileDirectory, jsOutputDirectory);
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


}