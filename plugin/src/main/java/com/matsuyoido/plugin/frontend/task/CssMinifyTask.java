package com.matsuyoido.plugin.frontend.task;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import com.matsuyoido.LineEnd;
import com.matsuyoido.caniuse.CanIUse;
import com.matsuyoido.css.CssMinifyCompiler;
import com.matsuyoido.css.MinifyType;
import com.matsuyoido.css.PrefixCompiler;
import com.matsuyoido.plugin.frontend.extension.CssExtension;
import com.matsuyoido.plugin.frontend.extension.RootExtension;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class CssMinifyTask extends DefaultTask {

    private LineEnd lineEnd;
    private boolean continueIfErrorExist;
    private List<CssExtension> settings;
    private CanIUse caniuse;

    void setupTask() throws IOException {
        RootExtension extension = getProject().getExtensions().getByType(RootExtension.class);
        this.lineEnd = extension.getLineEndSetting();
        this.continueIfErrorExist = extension.getSkipError();
        this.settings = extension.getCssSetting();
        this.caniuse = new PrefixerCanIUse(getProject(), extension.getPrefixerSetting());
    }

    @TaskAction
    public void minifyCss(IncrementalTaskInputs inputs) throws IOException {
        setupTask();
        PrefixCompiler prefixer = new PrefixCompiler(caniuse.getCssSupports());
        CssMinifyCompiler compiler = new CssMinifyCompiler(lineEnd, MinifyType.YUI);

        this.settings.forEach(setting -> {
            setting.getOutputDirectory()
                   .mkdirs();
            new Minifier("css", false, continueIfErrorExist){
                @Override
                protected String compile(Path filePath) {
                    if (setting.isAddPrefixer()) {
                        return compiler.compile(prefixer.addPrefix(filePath.toFile()));
                    } else {
                        return compiler.compile(filePath.toFile());
                    }
                }
            }.execute(setting.getInputDirectory(), setting.getOutputDirectory()); 
        });
    }

}