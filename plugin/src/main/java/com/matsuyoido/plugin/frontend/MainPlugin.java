package com.matsuyoido.plugin.frontend;

import com.matsuyoido.plugin.frontend.extension.JavaScriptExtension;
import com.matsuyoido.plugin.frontend.extension.PrefixerExtension;
import com.matsuyoido.plugin.frontend.extension.RootExtension;

import java.io.IOException;

import com.matsuyoido.caniuse.CanIUse;
import com.matsuyoido.plugin.frontend.extension.CssExtension;
import com.matsuyoido.plugin.frontend.task.css.CssMinifyTask;
import com.matsuyoido.plugin.frontend.task.js.JsMinifyTask;
import com.matsuyoido.plugin.frontend.task.sass.SassCompileTask;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class MainPlugin implements Plugin<Project> {
    private static final String COMPILE_GROUP = "compile";
    private static final String SASS_TASK_NAME = "sassCompile";
    private static final String CSS_MIN_TASK_NAME = "cssMinify";
    private static final String JS_MIN_TASK_NAME = "jsMinify";
    private RootExtension extension;

    @Override
    public void apply(Project project) {
        this.extension = project.getExtensions().create("frontend", RootExtension.class, project);
        project.afterEvaluate(this::setupTasks);
    }

    RootExtension getExtension() {
        return this.extension;
    }

    void setupTasks(Project project) {
        TaskContainer taskContainer = project.getTasks();

        CssMinifyTask minifyTask = isActiveCssMinify() ? setupCssMinifyTask(taskContainer) : null;
        if (isActiveSassCompile()) {
            setupSassCompileTask(taskContainer, minifyTask);
        }
        if (isActiveJsMinify()) {
            setupJsMinifyTask(taskContainer);
        }
        // project.getGradle()
        //        .getTaskGraph()
        //        .whenReady(graph -> {
        //             if (graph.hasTask(SASS_TASK_NAME)) {
                        
        //             }
        //        });
    }

    private boolean isActiveSassCompile() {
        CssExtension sassExtension = getExtension().cssConfigure();
        if (sassExtension == null) {
            return false;
        }
        return sassExtension.getSassDir() != null && sassExtension.getCssDir() != null;
    }

    private SassCompileTask setupSassCompileTask(TaskContainer taskFactory, CssMinifyTask minifyTask) {
        CssExtension extension = getExtension().cssConfigure();
        SassCompileTask task = taskFactory.create(SASS_TASK_NAME, SassCompileTask.class);

        task.setSassFileDirectory(extension.getSassDir())
            .setOutputFileDirectory(extension.getCssDir())
            .setLineEnd(this.extension.getLineEnding());

        if (minifyTask != null) {
            task.finalizedBy(minifyTask);
            minifyTask.onlyIf((t) -> {
                boolean notSassRun = !(task.getState().getExecuted());
                boolean runCondition = (task.getState().getFailure() == null && extension.isMinifyEnable());
                return notSassRun || runCondition;
            });
        }

        task.setGroup(COMPILE_GROUP);
        task.setDescription("SCSS(SASS) to CSS file.");
        task.getOutputs().upToDateWhen(t -> false); // always run setting
        return task;
    }


    private boolean isActiveCssMinify() {
        CssExtension cssExtension = getExtension().cssConfigure();
        if (cssExtension == null) {
            return false;
        }
        return cssExtension.getCssDir() != null && cssExtension.getOutDir() != null;
    }

    private CssMinifyTask setupCssMinifyTask(TaskContainer taskFactory) {
        CssExtension extension = getExtension().cssConfigure();
        CssMinifyTask task = taskFactory.create(CSS_MIN_TASK_NAME, CssMinifyTask.class);
        if (extension.isPrefixerEnable()) {
            PrefixerExtension prefixerExtension = extension.prefixerConfig();
            try {
                CanIUse caniuse = new PrefixerCanIUse(prefixerExtension);
                task.setPrefixer(caniuse.getCssSupports());
            } catch (IOException e) {
                throw new GradleException(e.getMessage(), e);
            }
        }

        task.setCssFileDirectory(extension.getCssDir())
            .setOutputFileDirectory(extension.getOutDir())
            .setDeleteBeforeCompileFile(extension.isOriginFileDelete())
            .setLineEnd(this.extension.getLineEnding());

        task.setGroup(COMPILE_GROUP);
        task.setDescription("CSS to min file.");
        task.getOutputs().upToDateWhen(t -> false); // always run setting
        return task;
    }

    private boolean isActiveJsMinify() {
        JavaScriptExtension jsExtension = getExtension().javascriptConfigure();
        if (jsExtension == null) {
            return false;
        }
        return jsExtension.getJsDir() != null && jsExtension.getOutputDir() != null;
    }

    private JsMinifyTask setupJsMinifyTask(TaskContainer taskFactory) {
        JavaScriptExtension extension = getExtension().javascriptConfigure();
        JsMinifyTask task = taskFactory.create(JS_MIN_TASK_NAME, JsMinifyTask.class);

        task.setJsFileDirectory(extension.getJsDir())
            .setOutputFileDirectory(extension.getOutputDir())
            .setMinifierType(extension.getMinifierType());

        task.setGroup(COMPILE_GROUP);
        task.setDescription("JS to min file.");
        task.getOutputs().upToDateWhen(t -> false); // always run setting
        return task;
    }
}