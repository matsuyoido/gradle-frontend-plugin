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
import java.util.stream.Collectors;

import org.gradle.api.GradleException;

/**
 * Compiler
 */
public abstract class Compiler {

    private String fileRegex;
    protected final String outputExtension;

    public Compiler(String outputExtension, String regex) {
        this.outputExtension = (outputExtension.startsWith(".")) ? outputExtension : ("." + outputExtension);
        this.fileRegex = regex;
    }

    public void execute(File inputDirectory, File outputDirectory) {
        Path inputRootPath = inputDirectory.toPath();
        Path outputRootPath = outputDirectory.toPath();
        try {
            Set<Path> filePaths = getTargets(inputDirectory);
            filePaths.forEach(path -> {
                Path outputPath = convetToOutputPath(inputRootPath, outputRootPath, path);
                compileExecute(path, outputPath);
            });
        } catch (IOException e) {
            throw new GradleException("target found error -> " + inputRootPath, e);
        }
    }

    protected void compileExecute(Path filePath, Path outputPath) {
        Throwable exception = null;
        try {
            String content = compile(filePath);
            if (content != null && !content.isEmpty()) {
                outputFile(outputPath.toFile(), content);
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
            throw new GradleException(t.getMessage(), t);
        });
    }

    protected Set<Path> getTargets(File inputDirectory) throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(this.fileRegex);
        return Files
                .find(inputDirectory.toPath(), Integer.MAX_VALUE,
                        (filePath, fileAttr) -> fileAttr.isRegularFile() && matcher.matches(filePath.getFileName()))
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