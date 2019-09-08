package com.matsuyoido.plugin.frontend.task.css;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import com.helger.css.ECSSVersion;
import com.helger.css.decl.CascadingStyleSheet;
import com.helger.css.reader.CSSReader;
import com.helger.css.writer.CSSWriterSettings;
import com.matsuyoido.caniuse.SupportData;
import com.matsuyoido.caniuse.SupportStatus;
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
    private Predicate<SupportStatus> supportFilter;

    public PhCssMinifyCompiler(boolean isDeleteBeforeCompileFile) {
        super("css", isDeleteBeforeCompileFile);
    }
	public void setPrefixer(List<SupportData> supportData, Predicate<SupportStatus> supportFilter) {
        this.isAddPrefixer = true;
        this.prefixer = new Prefixer(supportData);
        this.supportFilter = supportFilter;
	}

    @Override
    protected String compile(Path filePath) {
        String css = readCss(filePath.toFile());
        if (isAddPrefixer) {
            return convertString(
                CSSReader.readFromString(this.prefixer.addPrefix(css, this.supportFilter), charset, cssVersion));
        }
        return css;
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