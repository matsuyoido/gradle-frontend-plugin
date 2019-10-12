package com.matsuyoido.plugin.frontend.extension;

import java.io.Serializable;
import java.util.Objects;

import com.matsuyoido.plugin.LineEnd;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.Nested;

import groovy.lang.Closure;

/**
 * ex.
 * <pre>
 * ___ {
 *   css {
 *     CssExtension.class
 *   }
 *   js {
 *     JavaScriptExtension.class
 *   }
 * }
 * </pre>
 */
public class RootExtension implements Serializable {
    private static final long serialVersionUID = -7796482663082939967L;
    private final Project project;

    private LineEnd lineEnding;
    @Nested
    private final CssExtension css;
    @Nested
    private final JavaScriptExtension js;

    public RootExtension(Project project) {
        this.project = project;
        this.css = new CssExtension(project);
        this.js = new JavaScriptExtension();
    }

    public void css(Closure<CssExtension> closure) {
        this.project.configure(this.css, closure);
        // Objects.requireNonNull(closure).execute(this.css);
    }
    
    public void js(Action<JavaScriptExtension> closure) {
        Objects.requireNonNull(closure).execute(this.js);
    }

    public void setLineEnding(String value) {
        if (value == null) {
            this.lineEnding = LineEnd.PLATFORM;
        } else {
            switch (value.toLowerCase()) {
                case "windows":
                    this.lineEnding = LineEnd.WINDOWS;
                    break;
                case "linux":
                    this.lineEnding = LineEnd.LINUX;
                    break;
                case "mac":
                    this.lineEnding = LineEnd.MAC;
                    break;
                default:
                    this.lineEnding = LineEnd.PLATFORM;
                    break;
            }
        }
    }

    public CssExtension cssConfigure() {
        return this.css;
    }

    public JavaScriptExtension javascriptConfigure() {
        return this.js;
    }

    public LineEnd getLineEnding() {
        return (this.lineEnding == null) ? LineEnd.PLATFORM : this.lineEnding;
    }
}