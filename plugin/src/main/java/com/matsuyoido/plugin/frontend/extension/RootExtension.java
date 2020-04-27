package com.matsuyoido.plugin.frontend.extension;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.matsuyoido.LineEnd;
import org.gradle.api.Project;
import groovy.lang.Closure;

/**
 * ex.
 * <pre>
 * ___ {
 *   setting {
 *     lineEnding = ''
 *     prefixer {
 *       PrefixerExtension.class
 *     }
 *   }
 *   style {
 *     scss {
 *       ScssExtension.class
 *     }
 *     scss {
 *       ScssExtension.class
 *     }
 *     ...
 * 
 *     css {
 *       CssExtension.class
 *     }
 *     css {
 *       CssExtension.class
 *     }
 *     ...
 *   }
 *   script {
 *     js {
 *       JavaScriptExtension.class
 *     }
 *     js {
 *       JavaScriptExtension.class
 *     }
 *     ...
 *   }
 * }
 * </pre>
 */
public class RootExtension implements Serializable {
    private static final long serialVersionUID = -7796482663082939967L;
    private final Project project;

    private final SettingExtension setting;
    private final StyleExtension style;
    private final ScriptExtension script;

    public RootExtension(Project project) {
        this.project = project;
        this.setting = new SettingExtension(project);
        this.style = new StyleExtension(project);
        this.script = new ScriptExtension(project);
    }
    public void setting(Closure<SettingExtension> closure) {
        this.project.configure(this.setting, closure);
    }
    public void style(Closure<StyleExtension> closure) {
        this.project.configure(this.style, closure);
    }
    public void script(Closure<ScriptExtension> closure) {
        this.project.configure(this.script, closure);
    }
    
    static class SettingExtension implements Serializable {
        private static final long serialVersionUID = 4678670337728349376L;
        private final Project project;

        private LineEnd lineEnding;
        private Boolean skipError;
        PrefixerExtension prefixer;

        public SettingExtension(Project project) {
            this.project = project;
            this.prefixer = new PrefixerExtension();
        }
        public void setLineEnding(String value) {
            if (value != null) {
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
        public void setSkipError(boolean isSkip) {
            this.skipError = isSkip;
        }
        public void prefixer(Closure<PrefixerExtension> closure) {
            this.project.configure(this.prefixer, closure);
        }

        
        LineEnd getLineEnding() {
            return (this.lineEnding == null) ? LineEnd.PLATFORM : this.lineEnding;
        }
        boolean isSkipError() {
            return (this.skipError == null) ? true : this.skipError;
        }
    }

    static class StyleExtension implements Serializable {
        private static final long serialVersionUID = -8893508410304218645L;
        private final Project project;
        private List<ScssExtension> scss = new ArrayList<>();
        private List<CssExtension> css = new ArrayList<>();

        public StyleExtension(Project project) {
            this.project = project;
        }
        public void scss(Closure<ScssExtension> closure) {
            ScssExtension extension = new ScssExtension();
            this.project.configure(extension, closure);
            this.scss.add(extension);
        }
        public void css(Closure<CssExtension> closure) {
            CssExtension extension = new CssExtension();
            this.project.configure(extension, closure);
            this.css.add(extension);
        }

        List<ScssExtension> getScssConfig() {
            return Collections.unmodifiableList(this.scss);
        }
        List<CssExtension> getCssConfig() {
            return Collections.unmodifiableList(this.css);
        }
    }

    static class ScriptExtension implements Serializable {
        private static final long serialVersionUID = 2136005739697874623L;
        private final Project project;
        private List<JavaScriptExtension> js = new ArrayList<>();

        public ScriptExtension(Project project) {
            this.project = project;
        }
        public void js(Closure<JavaScriptExtension> closure) {
            JavaScriptExtension extension = new JavaScriptExtension();
            this.project.configure(extension, closure);
            this.js.add(extension);
        }

        List<JavaScriptExtension> getJsConfig() {
            return Collections.unmodifiableList(this.js);
        }
    }


    //#region getter
    public LineEnd getLineEndSetting() {
        return this.setting.getLineEnding();
    }
    public boolean getSkipError() {
        return this.setting.isSkipError();
    }
    public PrefixerExtension getPrefixerSetting() {
        return this.setting.prefixer;
    }
    public List<CssExtension> getCssSetting() {
        return this.style.getCssConfig();
    }
    public List<ScssExtension> getScssSetting() {
        return this.style.getScssConfig();
    }
    public List<JavaScriptExtension> getJSSetting() {
        return this.script.getJsConfig();
    }
    //#endregion

}