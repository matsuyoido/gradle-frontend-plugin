package com.matsuyoido.plugin.frontend.task;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public abstract class Minifier extends Compiler {

    private boolean isDeleteBeforeCompileFile;
    
    public Minifier(String extension, boolean isDeleteBeforeCompileFile, boolean continueIfErrorExist) {
        super(".min." + extension, "glob:*." + extension, excludeRegex(extension), continueIfErrorExist);
        this.isDeleteBeforeCompileFile = isDeleteBeforeCompileFile;
    }

    private static Set<String> excludeRegex(String extension) {
        Set<String> regex = new HashSet<>();
        regex.add("regex:.*\\.min\\." + extension + "$");
        return regex;
    }

    @Override
    protected void afterEvaluate(Path filePath, Path outputPath, Optional<Throwable> exception) {
        if (!exception.isPresent() && this.isDeleteBeforeCompileFile) {
            filePath.toFile().delete();
        }
        super.afterEvaluate(filePath, outputPath, exception);
    }
}