package com.matsuyoido.plugin.frontend;

import com.matsuyoido.plugin.frontend.extension.JavaScriptExtension;
import com.matsuyoido.plugin.frontend.extension.RootExtension;
import com.matsuyoido.plugin.frontend.extension.CssExtension;
import com.matsuyoido.plugin.frontend.task.css.CssMinifyTask;
import com.matsuyoido.plugin.frontend.task.js.JsMinifyTask;
import com.matsuyoido.plugin.frontend.task.sass.SassCompileTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class MainPlugin implements Plugin<Project> {
    private static final String COMPILE_GROUP = "compile";
    private static final String PRD_GROUP = "prd";
    private RootExtension extension;

    @Override
    public void apply(Project project) {
        this.extension = project.getExtensions().create("frontend", RootExtension.class);

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
        SassCompileTask task = taskFactory.create("sassCompile", SassCompileTask.class);

        task.setSassFileDirectory(extension.getSassDir())
            .setOutputFileDirectory(extension.getCssDir());

        if (minifyTask != null) {
            task.finalizedBy(minifyTask);
            minifyTask.onlyIf((t) -> {
                boolean notSassRun = !(task.getState().getExecuted());
                boolean runCondition = (task.getState().getFailure() == null && extension.isMinifyEnable());
                return notSassRun || runCondition;
            });
        }

        task.setGroup(COMPILE_GROUP);
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
        CssMinifyTask task = taskFactory.create("cssMinify", CssMinifyTask.class);

        task.setCssFileDirectory(extension.getCssDir())
            .setOutputFileDirectory(extension.getOutDir())
            .setDeleteBeforeCompileFile(extension.isOriginFileDelete());

        task.setGroup(PRD_GROUP);
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
        JsMinifyTask task = taskFactory.create("jsMinify", JsMinifyTask.class);

        task.setJsFileDirectory(extension.getJsDir())
            .setOutputFileDirectory(extension.getOutputDir())
            .setMinifierType(extension.getMinifierType());

        task.setGroup(PRD_GROUP);
        return task;
    }
}