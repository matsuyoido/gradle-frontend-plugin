package com.matsuyoido.plugin.frontend.extension;

import java.io.File;
import java.io.Serializable;

/**
 * PrefixerExtension
 */
public class PrefixerExtension implements Serializable {
    private static final long serialVersionUID = 7180279308716077071L;

    private File caniuseDataFile;
    private String ie;
    private String edge;
    private String chrome;
    private String firefox;
    private String safari;
    private String ios;
    private String android;

    public void setCaniuseData(File data) {
        this.caniuseDataFile = data;
    }

    public void setIe(String version) {
        this.ie = version;
    }

    public void setEdge(String version) {
        this.edge = version;
    }

    public void setChrome(String version) {
        this.chrome = version;
    }

    public void setFirefox(String version) {
        this.firefox = version;
    }

    public void setSafari(String version) {
        this.safari = version;
    }

    public void setIos(String version) {
        this.ios = version;
    }

    public void setAndroid(String version) {
        this.android = version;
    }

    // #region getter
    public File getCaniuseData() {
        return this.caniuseDataFile;
    }
    public String getIe() {
        return this.ie;
    }
    public String getEdge() {
        return this.edge;
    }
    public String getChrome() {
        return this.chrome;
    }
    public String getFirefox() {
        return this.firefox;
    }
    public String getSafari() {
        return this.safari;
    }
    public String getIos() {
        return this.ios;
    }
    public String getAndroid() {
        return this.android;
    }
    //#endregion

}