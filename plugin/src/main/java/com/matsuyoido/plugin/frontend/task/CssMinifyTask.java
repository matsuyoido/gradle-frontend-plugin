package com.matsuyoido.plugin.frontend.task;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.plugin.LineEnd;
import com.matsuyoido.plugin.frontend.css.CssMinifyCompiler;
import com.matsuyoido.plugin.frontend.css.MinifyType;
import com.matsuyoido.plugin.frontend.css.PrefixCompiler;
import com.matsuyoido.plugin.frontend.task.Minifier;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.incremental.IncrementalTaskInputs;

public class CssMinifyTask extends DefaultTask {

    private File cssFileDirectory;
    private File cssOutputDirectory;
    private boolean isDeleteBeforeCompileFile;
    private LineEnd lineEnd;
    private List<SupportData> supportData;

    @TaskAction
    public void minifyCss(IncrementalTaskInputs inputs) {
        cssOutputDirectory.mkdirs();
        MinifyType type = (this.supportData == null) ? MinifyType.YUI : MinifyType.SIMPLE;
        PrefixCompiler prefixer = (this.supportData == null) ? null : new PrefixCompiler(supportData);

        CssMinifyCompiler compiler = new CssMinifyCompiler(lineEnd, type);
        new Minifier("css", isDeleteBeforeCompileFile){
            @Override
            protected String compile(Path filePath) {
                if (prefixer == null) {
                    return compiler.compile(filePath.toFile());
                } else {
                    return compiler.compile(prefixer.addPrefix(filePath.toFile()));
                }
            }
        }.execute(cssFileDirectory, cssOutputDirectory);
    }

    public CssMinifyTask setCssFileDirectory(File directory) {
        this.cssFileDirectory = directory;
        return this;
    }
    public CssMinifyTask setOutputFileDirectory(File directory) {
        this.cssOutputDirectory = directory;
        return this;
    }
    public CssMinifyTask setDeleteBeforeCompileFile(boolean deleteDone) {
        this.isDeleteBeforeCompileFile = deleteDone;
        return this;
    }
    public CssMinifyTask setPrefixer(List<SupportData> supportData) {
        this.supportData = supportData;
        return this;
    }
    public CssMinifyTask setLineEnd(LineEnd lineEnd) {
        this.lineEnd = lineEnd;
        return this;
    }

}