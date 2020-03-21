package com.matsuyoido.plugin.frontend;

import com.matsuyoido.plugin.frontend.extension.RootExtension;

import java.io.IOException;

import com.matsuyoido.plugin.frontend.task.CssMinifyTask;
import com.matsuyoido.plugin.frontend.task.JsMergeTask;
import com.matsuyoido.plugin.frontend.task.JsMinifyTask;
import com.matsuyoido.plugin.frontend.task.SassCompileTask;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;

public class MainPlugin implements Plugin<Project> {
    private static final String COMPILE_GROUP = "compile";
    private static final String SASS_TASK_NAME = "sassCompile";
    private static final String CSS_MIN_TASK_NAME = "cssMinify";
    private static final String JS_MIN_TASK_NAME = "jsMinify";
    private static final String JS_MERGE_TASK_NAME = "jsMerge";

    @Override
    public void apply(Project project) {
        project.getExtensions().create("frontend", RootExtension.class, project);
        project.afterEvaluate(this::setupTasks);
    }

    void setupTasks(Project project) {
        RootExtension extension = project.getExtensions().getByType(RootExtension.class);
        TaskContainer taskContainer = project.getTasks();

        if (!extension.getScssSetting().isEmpty()) {
            setupSassCompileTask(extension, taskContainer);
        }
        if (!extension.getCssSetting().isEmpty()) {
            setupCssMinifyTask(extension, taskContainer);
        }
        if (!extension.getJSSetting().isEmpty()) {
            setupJsMinifyTask(extension, taskContainer);
            setupJsMergeTask(extension, taskContainer);
        }

        // project.getGradle()
        // .getTaskGraph()
        // .whenReady(graph -> {
        // if (graph.hasTask(SASS_TASK_NAME)) {

        // }
        // });
    }

    private SassCompileTask setupSassCompileTask(RootExtension extension, TaskContainer taskFactory) {
        try {
            SassCompileTask task = taskFactory.create(SASS_TASK_NAME, SassCompileTask.class,
                extension.getLineEndSetting(),
                extension.getScssSetting(), 
                ( (extension.getPrefixerSetting() == null) ? null : new PrefixerCanIUse(extension.getPrefixerSetting()) )
            );
            task.setGroup(COMPILE_GROUP);
            task.setDescription("SCSS(SASS) to CSS file.");
            task.getOutputs().upToDateWhen(t -> false); // always run setting
            return task;
        } catch (IOException e) {
            throw new GradleException(e.getMessage(), e);
        }

    }

    private CssMinifyTask setupCssMinifyTask(RootExtension extension, TaskContainer taskFactory) {
        try {
            CssMinifyTask task = taskFactory.create(CSS_MIN_TASK_NAME, CssMinifyTask.class,
                extension.getLineEndSetting(),
                extension.getCssSetting(),
                ( (extension.getPrefixerSetting() == null) ? null : new PrefixerCanIUse(extension.getPrefixerSetting()) )
            );
            task.setGroup(COMPILE_GROUP);
            task.setDescription("CSS to min file.");
            task.getOutputs().upToDateWhen(t -> false); // always run setting
            return task;
        } catch (IOException e) {
            throw new GradleException(e.getMessage(), e);
        }
    }


    private JsMinifyTask setupJsMinifyTask(RootExtension extension, TaskContainer taskFactory) {
        JsMinifyTask task = taskFactory.create(JS_MIN_TASK_NAME, JsMinifyTask.class, extension.getJSSetting());

        task.setGroup(COMPILE_GROUP);
        task.setDescription("JS to min file.");
        task.getOutputs().upToDateWhen(t -> false); // always run setting
        return task;
    }

    private JsMergeTask setupJsMergeTask(RootExtension extension, TaskContainer taskFactory) {
        JsMergeTask task = taskFactory.create(JS_MERGE_TASK_NAME, JsMergeTask.class, extension.getJSSetting());

        task.setGroup(COMPILE_GROUP);
        task.setDescription("Many JS file to one min file.");
        task.getOutputs().upToDateWhen(t -> false); // always run setting
        return task;
    }

}