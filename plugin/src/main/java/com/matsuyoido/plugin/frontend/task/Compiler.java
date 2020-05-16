package com.matsuyoido.plugin.frontend.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

/**
 * Compiler
 */
public abstract class Compiler {

    private Predicate<Path> targetFilter;
    private Predicate<Path> excludeFilter;
    protected final String outputExtension;
    protected final Logger log;
    private final boolean continueIfErrorExist;

    public Compiler(String outputExtension, String regex, Set<String> excludeRegex, boolean continueIfErrorExist) {
        this.log = Logging.getLogger(this.getClass());
        this.outputExtension = (outputExtension.startsWith(".")) ? outputExtension : ("." + outputExtension);
        PathMatcher targetMatcher = FileSystems.getDefault().getPathMatcher(regex);
        this.targetFilter = path -> targetMatcher.matches(path);
        this.excludeFilter = path -> true;
        if (excludeRegex != null && !excludeRegex.isEmpty()) {
            for (String excludePattern : excludeRegex) {
                PathMatcher excludeMatcher = FileSystems.getDefault().getPathMatcher(excludePattern);
                this.excludeFilter = excludeFilter.and(path -> !excludeMatcher.matches(path));
            }
        }
        this.continueIfErrorExist = continueIfErrorExist;
    }

    public final void execute(File inputDirectory, File outputDirectory) {
        Path inputRootPath = inputDirectory.toPath();
        Path outputRootPath = outputDirectory.toPath();
        try {
            Set<Path> filePaths = getTargets(inputDirectory);
            log.info("[task] Found {} file in [{}]", filePaths.size(), inputRootPath);
            filePaths.forEach(path -> {
                Path outputPath = convetToOutputPath(inputRootPath, outputRootPath, path);
                compileExecute(path, outputPath);
            });
        } catch (IOException e) {
            if (this.continueIfErrorExist) {
                log.debug("[task] Not found targets. {}", inputRootPath);
            } else {
                log.quiet("[task] Not found targets. {}", inputRootPath);
            }
            log.debug("[task] Error exception.", e);
        }
    }

    protected final void compileExecute(Path filePath, Path outputPath) {
        Throwable exception = null;
        try {
            log.info("[task] compile start. Target: {}", filePath);
            String content = compile(filePath);
            if (content != null) {
                outputFile(outputPath.toFile(), content);
                log.info("[task] compile finished. Output: {}", outputPath);
            } else {
                log.info("[task] content empty.");
            }
        } catch (Throwable t) {
            exception = t;
        }
        afterEvaluate(filePath, outputPath, Optional.ofNullable(exception));
    }

    protected abstract String compile(Path filePath);

    /**
     * If we handle compile result, this method override.
     * 
     * @param filePath compile target path
     * @param outputPath compiled output path
     * @param exception if happened error
     */
    protected void afterEvaluate(Path filePath, Path outputPath, Optional<Throwable> exception) {
        exception.ifPresent(t -> {
            if (continueIfErrorExist) {
                log.error(t.getMessage(), t);
            } else {
                throw new GradleException(t.getMessage(), t);
            }
        });
    }

    protected Set<Path> getTargets(File inputDirectory) throws IOException {
        Path directoryRoot = inputDirectory.toPath();
        return Files
                .find(directoryRoot, Integer.MAX_VALUE,
                (filePath, fileAttr) -> {
                    Path relativeFilePath = directoryRoot.relativize(filePath);
                    return fileAttr.isRegularFile() && targetFilter.test(filePath.getFileName()) && excludeFilter.test(relativeFilePath);
                })
                .collect(Collectors.toSet());
    }

    protected Path convetToOutputPath(Path inputRootPath, Path outputRootPath, Path inputFilePath) {
        String relativePath = inputFilePath.toString().substring(inputRootPath.toString().length() + 1);
        String filePath = relativePath.substring(0, relativePath.lastIndexOf(".")) + this.outputExtension;
        return outputRootPath.resolve(filePath);
    }

    protected void outputFile(File outputFile, String content) throws IOException {
            outputFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(outputFile);
                    OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
                writer.write(content);
            }
    }
}