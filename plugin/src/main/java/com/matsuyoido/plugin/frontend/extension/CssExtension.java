package com.matsuyoido.plugin.frontend.extension;

import java.io.File;
import java.io.Serializable;
import org.gradle.api.Project;
import org.gradle.api.tasks.Nested;

import groovy.lang.Closure;

/**
 * ex.
 * <pre>
 * ___ {
 *     sassDir = file("$projectDir/src/main/sass")
 *     cssDir = file("$projectDir/src/main/resources/static/css")
 *     minifyEnable = false
 *     originDeleted = false
 *     outDir = file("$projectDir/src/main/resources/static/css")
 *     prefixerEnable = false
 *     prefixer {
 *     }
 * }
 * </pre>
 */
public class CssExtension implements Serializable {
    private static final long serialVersionUID = 5074741537940502479L;
    private final Project project;
    @Nested
    private final PrefixerExtension prefixer;

    public CssExtension(Project project) {
        this.project = project;
        this.prefixer = new PrefixerExtension();
    }

    private File sassDir;
    private File cssDir;
    private boolean minifyEnable;
    private boolean originDeleted;
    private File outDir;
    private boolean prefixerEnable;

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
    public void prefixerEnable(boolean enable) {
        this.prefixerEnable = enable;
    }
    public void prefixer(Closure<PrefixerExtension> closure) {
        this.project.configure(this.prefixer, closure);
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
    public boolean isPrefixerEnable() {
        return this.prefixerEnable;
    }
    public PrefixerExtension prefixerConfig() {
        return this.prefixer;
    }

}