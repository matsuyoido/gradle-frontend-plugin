package com.matsuyoido.plugin.frontend.task;

import java.nio.file.Path;
import java.util.Optional;

public abstract class Minifier extends Compiler {

    private boolean isDeleteBeforeCompileFile;
    
    public Minifier(String extension, boolean isDeleteBeforeCompileFile) {
        super(".min." + extension, "glob:*[!.min]." + extension);
        this.isDeleteBeforeCompileFile = isDeleteBeforeCompileFile;
    }

    @Override
    protected void afterEvaluate(Path filePath, Path outputPath, Optional<Throwable> exception) {
        if (!exception.isPresent() && this.isDeleteBeforeCompileFile) {
            filePath.toFile().delete();
        }
        super.afterEvaluate(filePath, outputPath, exception);
    }
}