package com.matsuyoido.plugin.frontend.extension;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
// import java.util.Objects;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        if (this.caniuseDataFile != null) {
            return this.caniuseDataFile;
        } else {
            try {
                Path jsonFile = Files.createTempFile("plugin-", ".json");
                try (InputStream in = getClass().getResourceAsStream("/META-INF/resources/webjars/caniuse-db/1.0.30000748/data.json")) {
                    Files.copy(in, jsonFile, StandardCopyOption.REPLACE_EXISTING);
                    File result = jsonFile.toFile();
                    result.deleteOnExit();
                    return result;
                }
            } catch (java.io.IOException e) {
                throw new RuntimeException(e);
            }
        }
        /*
        return Objects.requireNonNullElseGet(
            this.caniuseDataFile, () -> 
            new File(getClass().getResource("/META-INF/resources/webjars/caniuse-db/1.0.30000748/data.json")
                               .toExternalForm()));
        */
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