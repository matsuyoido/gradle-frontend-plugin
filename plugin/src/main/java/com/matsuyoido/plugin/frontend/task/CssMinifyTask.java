package com.matsuyoido.plugin.frontend.task;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import com.matsuyoido.LineEnd;
import com.matsuyoido.caniuse.CanIUse;
import com.matsuyoido.css.CssMinifyCompiler;
import com.matsuyoido.css.MinifyType;
import com.matsuyoido.css.PrefixCompiler;
import com.matsuyoido.plugin.frontend.extension.CssExtension;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class CssMinifyTask extends DefaultTask {

    private LineEnd lineEnd;
    private List<CssExtension> settings;
    private Optional<CanIUse> caniuse;

    @Inject
    public CssMinifyTask(LineEnd lineEnd, List<CssExtension> settings, CanIUse caniuse) {
        this.lineEnd = lineEnd;
        this.settings = settings;
        this.caniuse = Optional.ofNullable(caniuse);
    }

    @TaskAction
    public void minifyCss(IncrementalTaskInputs inputs) {
        PrefixCompiler prefixer = caniuse.map(v -> new PrefixCompiler(v.getCssSupports())).orElse(null);
        CssMinifyCompiler compiler = new CssMinifyCompiler(lineEnd, MinifyType.SIMPLE);

        this.settings.forEach(setting -> {
            setting.getOutputDirectory()
                   .mkdirs();
            new Minifier("css", false){
                @Override
                protected String compile(Path filePath) {
                    if (setting.isAddPrefixer()) {
                        if (!caniuse.isPresent()) {
                            throw new IllegalStateException("Not Found caniuse data.");
                        }
                        return compiler.compile(prefixer.addPrefix(filePath.toFile()));
                    } else {
                        return compiler.compile(filePath.toFile());
                    }
                }
            }.execute(setting.getInputDirectory(), setting.getOutputDirectory()); 
        });
    }

}