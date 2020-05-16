package com.matsuyoido.plugin.frontend.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.matsuyoido.LineEnd;
import com.matsuyoido.caniuse.CanIUse;
import com.matsuyoido.css.CssMinifyCompiler;
import com.matsuyoido.css.MinifyType;
import com.matsuyoido.css.PrefixCompiler;
import com.matsuyoido.css.SassCompiler;
import com.matsuyoido.plugin.frontend.extension.RootExtension;
import com.matsuyoido.plugin.frontend.extension.ScssExtension;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class SassCompileTask extends DefaultTask {
    private LineEnd lineEnd;
    private boolean continueIfErrorExist;
    private List<ScssExtension> settings;
    private CanIUse caniuse;

    void setupTask() throws IOException {
        RootExtension extension = getProject().getExtensions().getByType(RootExtension.class);
        this.lineEnd = extension.getLineEndSetting();
        this.continueIfErrorExist = extension.getSkipError();
        this.settings = extension.getScssSetting();
        this.caniuse = new PrefixerCanIUse(getProject(), extension.getPrefixerSetting());
    }

    @TaskAction
    public void compileSass(IncrementalTaskInputs inputs) throws IOException {
        setupTask();
        SassCompiler compiler = new SassCompiler(lineEnd);
        CssMinifyCompiler minifyCompiler = new CssMinifyCompiler(lineEnd, MinifyType.SIMPLE);
        PrefixCompiler prefixerCompiler = new PrefixCompiler(this.caniuse.getCssSupports());

        this.settings.forEach(setting -> {
            setting.getOutputDirectory()
                   .mkdirs();
            String exportExtension = setting.isEnableMinify() ? ".min.css" : "css";
            new Compiler(exportExtension, "glob:[!_]*.scss", null, continueIfErrorExist){
                @Override
                protected String compile(Path filePath) {
                    String cssText = compiler.compile(filePath.toFile());
                    if (setting.isAddPrefixer()) {
                        cssText = prefixerCompiler.addPrefix(cssText);
                    }
                    if (setting.isEnableMinify()) {
                        return minifyCompiler.compile(cssText);
                    } else {
                        return cssText;
                    }
                }
            }.execute(setting.getInputDirectory(), setting.getOutputDirectory());
        });
    }

}