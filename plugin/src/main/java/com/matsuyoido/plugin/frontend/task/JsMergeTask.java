package com.matsuyoido.plugin.frontend.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matsuyoido.js.JsMinifyCompiler;
import com.matsuyoido.plugin.frontend.extension.JavaScriptExtension;
import com.matsuyoido.plugin.frontend.extension.RootExtension;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

/**
 * JsMergeTask
 */
public class JsMergeTask extends DefaultTask {

    private boolean continueIfErrorExist;
    private List<JavaScriptExtension> settings;

    @Inject
    public JsMergeTask() {
        RootExtension extension = getProject().getExtensions().getByType(RootExtension.class);
        this.continueIfErrorExist = extension.getSkipError();
        this.settings = extension.getJSSetting();
    }

    @TaskAction
    public void mergeJavascript(IncrementalTaskInputs inputs) {
        MergeCompile compiler = new MergeCompile();
        settings.forEach(setting -> {
            compiler.execute(setting.getInputDirectory(), setting.getOutputDirectory());
        });
    }

    private class MergeCompile extends Compiler {

        private ObjectMapper objectMapper;
        private JsMinifyCompiler compiler;

        public MergeCompile() {
            super(".min.js", "glob:*.js.map", null, continueIfErrorExist);
            compiler = new JsMinifyCompiler(null);
            objectMapper = new ObjectMapper();
        }

        @Override
        protected String compile(Path filePath) {
            try {
                JsonNode jsMapper = objectMapper.readTree(filePath.toFile());
                List<File> sourcesList = new ArrayList<>();
                jsMapper.get("sources").elements()
                        .forEachRemaining(src -> sourcesList.add(
                            filePath.getParent()
                                    .resolve(src.asText())
                                    .normalize().toFile()
                        ));
                return compiler.compile(sourcesList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected Path convetToOutputPath(Path inputRootPath, Path outputRootPath, Path inputFilePath) {
            String relativePath = inputFilePath.toString().substring(inputRootPath.toString().length() + 1);
            String filePath = relativePath.substring(0, relativePath.lastIndexOf(".js.map")) + this.outputExtension;
            return outputRootPath.resolve(filePath);
        }
    }
}