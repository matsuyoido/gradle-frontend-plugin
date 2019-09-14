package com.matsuyoido.plugin.frontend.extension;

import java.io.File;

import com.matsuyoido.plugin.frontend.task.MinifierType;

/**
 * ex.
 * <pre>
 * ___ {
 *     jsDir = file("$projectDir/src/main/resources/static/js")
 *     outDir = file("$projectDir/src/main/resources/static/js")
 *     type = 'yahoo'
 * }
 * </pre>
 */
public class JavaScriptExtension {
    private File jsDir;
    private File outDir;
    private MinifierType type;
    
    public void jsDir(File path) {
        this.jsDir = path;
    }
    public void outDir(File path) {
        this.outDir = path;
    }
    public void type(String type) {
        switch (type) {
            case "yahoo":
                this.type = MinifierType.YUI;
                break;
            case "google":
                this.type = MinifierType.GOOGLE_CLOSURE;
                break;
        }
    }

    public File getJsDir() {
        return this.jsDir;
    }
    public File getOutputDir() {
        return this.outDir;
    }
    public MinifierType getMinifierType() {
        return this.type == null ? MinifierType.GOOGLE_CLOSURE : this.type;
    }
}