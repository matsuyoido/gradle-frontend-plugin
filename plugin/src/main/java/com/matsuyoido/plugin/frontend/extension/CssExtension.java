package com.matsuyoido.plugin.frontend.extension;

import java.io.File;

/**
 * ex.
 * <pre>
 * ___ {
 *     sassDir = file("$projectDir/src/main/sass")
 *     cssDir = file("$projectDir/src/main/resources/static/css")
 *     minifyEnable = false
 *     originDeleted = false
 *     outDir = file("$projectDir/src/main/resources/static/css")
 * }
 * </pre>
 */
public class CssExtension {
    private File sassDir;
    private File cssDir;
    private boolean minifyEnable;
    private boolean originDeleted;
    private File outDir;

    public void sassDir(File path) {
        this.sassDir = path;
    }
    public void cssDir(File path) {
        this.cssDir = path;
    }
    public void minifyEnable(boolean enable) {
        this.minifyEnable = enable;
    }
    public void originDeleted(boolean deleteDone) {
        this.originDeleted = deleteDone;
    }
    public void outDir(File path) {
        this.outDir = path;
    }

    public File getSassDir() {
        return this.sassDir;
    }
    public File getCssDir() {
        return this.cssDir;
    }
    public boolean isMinifyEnable() {
        return this.minifyEnable;
    }
    public boolean isOriginFileDelete() {
        return this.originDeleted;
    }
    public File getOutDir() {
        return this.outDir;
    }

}