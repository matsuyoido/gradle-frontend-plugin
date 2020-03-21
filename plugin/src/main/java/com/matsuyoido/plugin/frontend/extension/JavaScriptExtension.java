package com.matsuyoido.plugin.frontend.extension;

import java.io.File;
import java.io.Serializable;


/**
 * ex.
 * <pre>
 * ___ {
 *     inDir = file("$projectDir/src/main/resources/static/js")
 *     outDir = file("$projectDir/src/main/resources/static/js")
 *     type = 'yahoo'
 * }
 * </pre>
 */
public class JavaScriptExtension implements Serializable {
    private static final long serialVersionUID = -2923813369453641197L;

    private File inputDirectory;
    private File outputDirectory;
    private MinifierType type;

    public void setInDir(File path) {
        this.inputDirectory = path;
    }
    public void setOutDir(File path) {
        this.outputDirectory = path;
    }
    public void setType(String type) {
        if (type != null) {
            switch (type) {
                case "yahoo":
                    this.type = MinifierType.YUI;
                    break;
                case "google":
                    this.type = MinifierType.GOOGLE_CLOSURE;
                    break;
            }
        }
    }

    public File getInputDirectory() {
        return this.inputDirectory;
    }
    public File getOutputDirectory() {
        return this.outputDirectory;
    }
    public MinifierType getMinifierType() {
        return this.type == null ? MinifierType.GOOGLE_CLOSURE : this.type;
    }

}