package com.matsuyoido.plugin.frontend.task;

import java.nio.file.Path;
import java.util.List;

import javax.inject.Inject;

import com.matsuyoido.js.JsMinifyCompiler;
import com.matsuyoido.js.MinifyType;
import com.matsuyoido.plugin.frontend.extension.JavaScriptExtension;
import com.matsuyoido.plugin.frontend.extension.MinifierType;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class JsMinifyTask extends DefaultTask {

    private List<JavaScriptExtension> settings;

    @Inject
    public JsMinifyTask(List<JavaScriptExtension> settings) {
        this.settings = settings;
    }

    @TaskAction
    public void compileJs(IncrementalTaskInputs inputs) {
        settings.forEach(setting -> {
            setting.getOutputDirectory()
                   .mkdirs();
            MinifyType minifyType = setting.getMinifierType() == MinifierType.YUI ? MinifyType.YUI : MinifyType.GOOGLE;
            JsMinifyCompiler compiler = new JsMinifyCompiler(minifyType);
            new Minifier("js", false){
                @Override
                protected String compile(Path filePath) {
                    return compiler.compile(filePath.toFile());
                }
            }.execute(setting.getInputDirectory(), setting.getOutputDirectory());
        });
    }

}