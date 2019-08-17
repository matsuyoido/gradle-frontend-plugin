package com.matsuyoido.plugin.frontend.extension;

import java.util.Objects;
import org.gradle.api.Action;

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
public class RootExtension {
    private CssExtension css;
    private JavaScriptExtension js;

    public void css(Action<CssExtension> closure) {
        if (this.css == null) {
            this.css = new CssExtension();
        }
        Objects.requireNonNull(closure).execute(this.css);
    }
    
    public void js(Action<JavaScriptExtension> closure) {
        if (this.js == null) {
            this.js = new JavaScriptExtension();
        }
        Objects.requireNonNull(closure).execute(this.js);
    }


    public CssExtension cssConfigure() {
        return this.css;
    }

    public JavaScriptExtension javascriptConfigure() {
        return this.js;
    }
}