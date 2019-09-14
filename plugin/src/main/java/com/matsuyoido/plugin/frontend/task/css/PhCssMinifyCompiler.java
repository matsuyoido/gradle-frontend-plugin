package com.matsuyoido.plugin.frontend.task.css;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import com.helger.css.ECSSVersion;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.plugin.frontend.task.Minifier;
import com.matsuyoido.plugin.frontend.task.css.autoprefixer.Prefixer;

/**
 * PhCssMinifyCompiler
 */
public class PhCssMinifyCompiler extends Minifier {

    private ECSSVersion cssVersion = ECSSVersion.CSS30;
    private Charset charset = StandardCharsets.UTF_8;
    private CSSWriterSettings settings = new CSSWriterSettings(cssVersion, true);

    private boolean isAddPrefixer;
    private Prefixer prefixer;

    public PhCssMinifyCompiler(boolean isDeleteBeforeCompileFile) {
        super("css", isDeleteBeforeCompileFile);
    }

    public PhCssMinifyCompiler(List<SupportData> supportData, boolean isDeleteBeforeCompileFile) {
        this(isDeleteBeforeCompileFile);
        this.isAddPrefixer = true;
        this.prefixer = new Prefixer(settings, supportData);
    }


    @Override
    protected String compile(Path filePath) {
        File cssFile = filePath.toFile();
        if (isAddPrefixer) {
            return this.prefixer.addPrefix(cssFile);
        } else {
            return readCss(cssFile);
        }
    }

    private String readCss(File file) {
        return convertString(CSSReader.readFromFile(file, charset, cssVersion));
    }

    private String convertString(CascadingStyleSheet css) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("@charset \"%s\";", StandardCharsets.UTF_8.name()));
        css.getAllRules().forEach(rule -> builder.append(rule.getAsCSSString(settings)));
        return builder.toString();
    }

}