package com.matsuyoido.plugin.frontend.extension;

import java.io.Serializable;
import java.util.Objects;
import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.tasks.Nested;
import org.gradle.util.ConfigureUtil;

import groovy.lang.Closure;

/**
 * ex.
 * <pre>
 * ___ {
 *   css {
 *     @see CssExtension.class
 *   }
 *   js {
 *     @see JavaScriptExtension.class
 *   }
 * }
 * </pre>
 */
public class RootExtension implements Serializable {
    private static final long serialVersionUID = -7796482663082939967L;
    private final Project project;

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


    public CssExtension cssConfigure() {
        return this.css;
    }

    public JavaScriptExtension javascriptConfigure() {
        return this.js;
    }
}